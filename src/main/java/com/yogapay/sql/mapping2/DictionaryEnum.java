package com.yogapay.sql.mapping2;

public class DictionaryEnum {

	protected int code;
	protected String text;

	protected DictionaryEnum() {
	}

	public DictionaryEnum(int code, String text) {
		this.code = code;
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public int getCode() {
		return code;
	}

	public String getText() {
		return text;
	}
}
