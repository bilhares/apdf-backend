package com.projeto.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.controller.PdfController;
import com.projeto.dto.PdfDto;

@RestController
@RequestMapping("/api/tools")
public class PdfResource {

	@Autowired
	PdfController controller;

	@PostMapping("/merge-pdf")
	public PdfDto mergePdf(@RequestBody List<PdfDto> dto) {
		return controller.mergePdf(dto);
	}

	@PostMapping("/paginate-pdf")
	public PdfDto paginatePdf(@RequestBody PdfDto dto) {
		return controller.paginatePdf(dto);
	}

	// add codebar
	// split
	@PostMapping("/split-pdf")
	public void splitPdf(@RequestBody PdfDto dto) {
		controller.splitPdf(dto);
	}

	// assinar

}
