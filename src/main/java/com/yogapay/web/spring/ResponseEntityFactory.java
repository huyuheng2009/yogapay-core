package com.yogapay.web.spring;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFactory {

	public static ResponseEntity<String> textPlain(String text) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-type", "text/plain;charset=utf-8");
		return new ResponseEntity<String>(text, responseHeaders, HttpStatus.OK);
	}
}
