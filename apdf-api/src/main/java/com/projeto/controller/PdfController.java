package com.projeto.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;

import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PageRange;
import com.itextpdf.kernel.utils.PdfSplitter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.projeto.dto.PdfDto;

@Controller
public class PdfController {

	public PdfDto mergePdf(List<PdfDto> dto) {

		List<PdfReader> readers = new ArrayList<>();

		try {
			for (PdfDto pdf : dto) {
				ByteArrayInputStream bis = new ByteArrayInputStream(pdf.getFile());
				PdfReader reader = new PdfReader(bis);
				readers.add(reader);
			}

			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
			pdfDoc.initializeOutlines();

			for (PdfReader reader : readers) {
				PdfDocument readerDoc = new PdfDocument(reader);
				readerDoc.copyPagesTo(1, readerDoc.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
				readerDoc.close();
			}
			pdfDoc.close();

			byte[] retorno = dest.toByteArray();
//			FileUtils.writeByteArrayToFile(new File("c://temp//teste.pdf"), retorno);
			return new PdfDto(retorno);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new PdfDto();
	}

	public PdfDto paginatePdf(PdfDto dto) {

		ByteArrayOutputStream dest = new ByteArrayOutputStream();
		try {

			ByteArrayInputStream bis = new ByteArrayInputStream(dto.getFile());
			PdfReader reader = new PdfReader(bis);

			PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(dest));
			Document doc = new Document(pdfDoc);

			int numberOfPages = pdfDoc.getNumberOfPages();
			for (int i = 1; i <= numberOfPages; i++) {

				doc.showTextAligned(new Paragraph(String.format("page %s of %s", i, numberOfPages)), 559, 50, i,
						TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
			}
			doc.close();
			return new PdfDto(dest.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new PdfDto();
	}

	public PdfDto splitPdf(PdfDto dto) {

		File folder = new File("temp");
		if (!folder.exists()) {
			folder.mkdir();
		}

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(dto.getFile());
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(bis));

			List<PdfDocument> splitDocuments = new PdfSplitter(pdfDoc) {
				int partNumber = 1;

				@Override
				protected PdfWriter getNextPdfWriter(PageRange documentPageRange) {
					try {
						return new PdfWriter(String.format("temp/splitDocument1_" + getId() + "_%s.pdf", partNumber++));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

				public int getId() {
					return new Random().nextInt((30000 - 10000) + 1) + 10000;
				}

			}.splitByPageCount(1);

			for (PdfDocument doc : splitDocuments) {
				doc.close();
			}

			pdfDoc.close();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(bout);
			int cont = 1;

			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.toString().contains("splitDocument")) {
					ZipEntry e = new ZipEntry("split_" + cont + ".pdf");
					out.putNextEntry(e);
					Path pdfPath = Paths.get(file.getPath());
					byte[] data = Files.readAllBytes(pdfPath);
					out.write(data, 0, data.length);
					out.closeEntry();
					file.delete();
					cont++;
				}
			}

			out.close();
			return new PdfDto(bout.toByteArray());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
