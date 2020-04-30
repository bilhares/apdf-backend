package com.projeto.dto;

public class PdfDto {

	private byte[] file;

	public PdfDto() {
		super();
	}

	public PdfDto(byte[] file) {
		super();
		this.file = file;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

}
