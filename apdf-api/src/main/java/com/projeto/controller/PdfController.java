package com.projeto.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;

import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
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
			FileUtils.writeByteArrayToFile(new File("c://temp//teste.pdf"), retorno);
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

				doc.showTextAligned(new Paragraph(String.format("page %s of %s", i, numberOfPages)), 559, 806, i,
						TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
			}
			doc.close();
			return new PdfDto(dest.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new PdfDto();
	}
}
