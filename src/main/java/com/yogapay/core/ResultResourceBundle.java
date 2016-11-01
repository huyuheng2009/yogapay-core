package com.yogapay.core;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.LoggerFactory;

public class ResultResourceBundle {

	private final ResourceBundle res;

	public ResultResourceBundle() {
		this("error-code");
	}

	public ResultResourceBundle(String bundleName) {
		res = ResourceBundle.getBundle(bundleName);
	}

	public String message(int errorCode) {
		try {
			return res.getString(String.valueOf(errorCode));
		} catch (MissingResourceException ex) {
			LoggerFactory.getLogger(ResultResourceBundle.class).error(String.valueOf(errorCode), ex);
			return "";
		}
	}

	public String message(int errorCode, Object... arguments) {
		try {
			String pattern = res.getString(String.valueOf(errorCode));
			return MessageFormat.format(pattern, arguments);
		} catch (MissingResourceException ex) {
			LoggerFactory.getLogger(ResultResourceBundle.class).error(String.valueOf(errorCode), ex);
			return "";
		}
	}

	public Result result(int errorCode) {
		return new Result(errorCode, message(errorCode), null);
	}

	public Result result(int errorCode, Object... arguments) {
		return new Result(errorCode, message(errorCode, arguments), null);
	}

	public ErrorCodeException errorCodeException(int errorCode) {
		return new ErrorCodeException(errorCode, message(errorCode));
	}

	public ErrorCodeException errorCodeException(int errorCode, Object... arguments) {
		return new ErrorCodeException(errorCode, message(errorCode, arguments));
	}

	public Result create(int errorCode) {
		return new Result(errorCode, message(errorCode), null);
	}

	public Result create(int errorCode, Object value) {
		return new Result(errorCode, message(errorCode), value);
	}

	public Result create(int errorCode, Object... arguments) {
		return null;
	}
}
