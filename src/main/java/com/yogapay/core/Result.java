package com.yogapay.core;

public class Result {

	private int errorCode;
	private String message;
	private Object value;

	public Result() {
	}

	public Result(int errorCode, String message, Object value) {
		this.errorCode = errorCode;
		this.message = message;
		this.value = value;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
