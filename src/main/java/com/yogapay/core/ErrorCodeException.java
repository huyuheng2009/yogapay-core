package com.yogapay.core;

public class ErrorCodeException extends Exception {

	private final int errorCode;
	private Object value;

	public ErrorCodeException(int errorCode) {
		this.errorCode = errorCode;
	}

	public ErrorCodeException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCodeException(int errorCode, String message, Object value) {
		super(message);
		this.errorCode = errorCode;
		this.value = value;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
