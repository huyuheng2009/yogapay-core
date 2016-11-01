package com.yogapay.sql;

import java.util.List;

public class QueryMoreCondition {

	protected int page = 1;
	protected int pageSize = 20;
	protected int maxPageSize = 100;
	private boolean hasMore;
	private List data;

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
		return pageSize + 1;
	}

	public int getBegin() {
		return (page - 1) * pageSize + 1;
	}

	public int getEnd() {
		return getBegin() + pageSize - 1 + 1;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

}
