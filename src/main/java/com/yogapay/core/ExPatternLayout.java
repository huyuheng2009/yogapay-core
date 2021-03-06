package com.yogapay.core;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

public class ExPatternLayout extends PatternLayout {
	public ExPatternLayout() {
		this(DEFAULT_CONVERSION_PATTERN);
	}

	public ExPatternLayout(String pattern) {
		super(pattern);
	}

        @Override
	public PatternParser createPatternParser(String pattern) {
		return new ExPatternParser(pattern == null ? DEFAULT_CONVERSION_PATTERN : pattern);
	}
}
