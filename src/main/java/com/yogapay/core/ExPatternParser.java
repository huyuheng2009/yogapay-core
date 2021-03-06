package com.yogapay.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

public class ExPatternParser extends PatternParser {

	static final ThreadLocal<Map<String, Object>> TH_LOCAL = new ThreadLocal<Map<String, Object>>(){
		@Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<String, Object>();
        } 
	};

	public static void setCurrentValue(String key, Object value) {
		Map<String, Object> map = TH_LOCAL.get();
		map.put(key, value);
		
	}

	public ExPatternParser(String pattern) {
		super(pattern);
	}

	public void finalizeConverter(char c) {
		if (c == '#') {
			String exs = super.extractOption();
			addConverter(new ExrPatternConverter(formattingInfo, exs));
			currentLiteral.setLength(0);

		} else {
			super.finalizeConverter(c);
		}
	}

	private class ExrPatternConverter extends PatternConverter {

		private String cfg;

		ExrPatternConverter(FormattingInfo formattingInfo, String cfg) {
			super(formattingInfo);
			this.cfg = cfg;
		}

		public String convert(LoggingEvent event) {
			Map<String, Object> valueMap = TH_LOCAL.get();
			if (valueMap != null) {
				Object value = valueMap.get(cfg);
				if (value != null) {
					return String.valueOf(value);
				}
			}
			return "";
		}
	}
}
