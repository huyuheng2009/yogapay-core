package com.yogapay.web.tags;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Functions {

	private static final DateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

	public static String dateText(Date date) {
		if (date == null) {
			return "";
		}
		synchronized (DATE_FORMAT_DATE) {
			return DATE_FORMAT_DATE.format(date);
		}
	}

	public static String moneyText(int money) {
		return String.format("%.2f", 1f * money / 100);
	}

	public static void main(String[] args) throws Exception {
		Method m = Functions.class.getMethod("moneyText", Integer.TYPE);
		System.out.println(m);
	}
}
