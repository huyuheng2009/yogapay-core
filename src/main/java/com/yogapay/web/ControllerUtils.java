package com.yogapay.web;

import java.util.HashMap;
import java.util.Map;

public class ControllerUtils {

	public static Map replaceMapKey(Map<Integer, ? extends Object> map, String prefix) {
		Map ret = new HashMap();
		for (Map.Entry<Integer, ? extends Object> e : map.entrySet()) {
			ret.put(prefix + e.getKey(), e.getValue());
		}
		return ret;
	}
}
