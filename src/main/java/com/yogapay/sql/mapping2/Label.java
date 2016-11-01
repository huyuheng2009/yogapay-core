package com.yogapay.sql.mapping2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

public class Label {
	
	public static final String[] EMPTY_PARAMS = new String[0];
	private static final Pattern P_FUNCTION = Pattern.compile(":(\\w+)(?:\\x28([\\.\\w]*(?:,[\\.\\w]+)*)\\x29)?");
	private static final Pattern P_LABLE = Pattern.compile("(?:(\\.+)(\\w*))?((?:" + P_FUNCTION.pattern() + ")*)");
	private static final Pattern P_PROPERTY = Pattern.compile("(.+?)(\\..*)?");
	public int order;
	public int columnIndex;
	public String columnLabel;
	public int zindex;
	public String property;
	public Map<String, String[]> functions;
	public SortedMap<Integer, String> propertyColumns = new TreeMap<Integer, String>();
	
	private void init() {
		if (functions != null) {
			String[] _ignored = functions.get("IGNORED");
			if (_ignored != null) {
				List<String> ignored = Arrays.asList(_ignored);
				for (Iterator<Map.Entry<Integer, String>> iterator = propertyColumns.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<Integer, String> next = iterator.next();
					if (ignored.contains(next.getValue())) {
						iterator.remove();
					}
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return columnLabel + " <" + order + ">";
	}
	
	public static List<Label> parse(String[] labelNames) {
		List<Label> labels = new ArrayList<Label>();
		Label last = null;
		for (int i = 0; i < labelNames.length; i++) {
			String labelName = labelNames[i];
			Matcher m = P_LABLE.matcher(labelName);
			if (!m.matches()) {
				if (last != null) {
					Matcher m2 = P_PROPERTY.matcher(labelName);
					if (!m2.matches()) {
						LoggerFactory.getLogger(Label.class).warn("label({})不符合表达式({})", labelName, P_PROPERTY.pattern());
					} else {
						last.propertyColumns.put(i + 1, m2.group(1));
					}
				}
				continue;
			}
			Label label = new Label();
			label.order = labels.size();
			label.columnIndex = i + 1;
			label.columnLabel = labelName;
			String str;
			str = m.group(1);
			if (str != null) {
				label.zindex = str.length();
			}
			str = m.group(2);
			if (str != null) {
				label.property = str;
			}
			str = m.group(3);
			if (!str.isEmpty()) {
				label.functions = new HashMap<String, String[]>();
				Matcher m2 = P_FUNCTION.matcher(str);
				while (m2.find()) {
					str = m2.group(2);
					label.functions.put(m2.group(1), str == null ? EMPTY_PARAMS : str.split(","));
				}
			} else {
				label.functions = Collections.emptyMap();
			}
			last = label;
			labels.add(label);
		}
		for (Label label : labels) {
			label.init();
		}
		return labels;
	}
}
