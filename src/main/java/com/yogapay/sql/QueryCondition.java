package com.yogapay.sql;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryCondition {

	protected static final Pattern P_SORT_FIELD = Pattern.compile("sort_(\\w+)");
	protected int page = 1;
	protected int pageSize = 20;
	protected int maxPageSize = 100;
	public final Map<String, Field> sortFieldMap;

	public QueryCondition() {
		Map<String, Field> map = new LinkedHashMap<String, Field>();
		for (Field f : getClass().getFields()) {
			Matcher m = P_SORT_FIELD.matcher(f.getName());
			if (!m.matches()) {
				continue;
			}
			map.put(m.group(1), f);
		}
		sortFieldMap = Collections.unmodifiableMap(map);
	}

	public boolean isSortEmpty() {
		try {
			for (Field f : sortFieldMap.values()) {
				if (f.getInt(this) != 0) {
					return false;
				}
			}
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		return true;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = Math.max(1, page);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		pageSize = Math.min(pageSize, maxPageSize);
		pageSize = Math.max(pageSize, 1);
		this.pageSize = pageSize;
	}

	public int getOffset() {
		return (page - 1) * pageSize;
	}

	public int getLen() {
		return pageSize;
	}

	public int getBegin() {
		return (page - 1) * pageSize + 1;
	}

	public int getEnd() {
		return getBegin() + pageSize - 1;
	}

}
