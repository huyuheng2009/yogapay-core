package com.yogapay.sql;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

public class Paginated {

	private int page;
	private int pageSize;
	private int count;
	private int optionLength = 5;
	private Object data;

	public Paginated() {
	}

	public Paginated(int page, int pageSize, int count) {
		if (count < 0 || pageSize < 1) {
			throw new IllegalArgumentException();
		}
		this.pageSize = pageSize;
		this.count = count;
		this.page = page;
	}

	public static Paginated empty() {
		Paginated p = new Paginated();
		p.data = Collections.emptyList();
		return p;
	}

	public Collection<Integer> getPageOptions() {
		if (isEmpty()) {
			return Collections.<Integer>emptyList();
		}
		Deque<Integer> d = new LinkedList<Integer>();
		for (int i = page - (optionLength - 1) / 2; i <= page + optionLength / 2; i++) {
			if (i >= 1 && i <= getTotalPage()) {
				d.add(i);
			}
		}
		int first = d.peekFirst(), last = d.peekLast();
		if (first > 2) {
			d.addFirst(null);
			d.addFirst(1);
		} else if (first > 1) {
			d.addFirst(1);
		}
		if (last < getTotalPage() - 1) {
			d.addLast(null);
			d.addLast(getTotalPage());
		} else if (last < getTotalPage()) {
			d.addLast(getTotalPage());
		}
		return d;
	}

	public int getTotalPage() {
		return (count + pageSize - 1) / pageSize;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public boolean isHasNext() {
		return page < getTotalPage();
	}

	public boolean isHasPrevious() {
		return page > 1;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getOptionLength() {
		return optionLength;
	}

	public void setOptionLength(int optionLength) {
		this.optionLength = Math.max(1, optionLength);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
