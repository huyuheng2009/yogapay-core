package com.yogapay.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class LazyIterator<E> implements Iterator {

	private int last = -1;
	private int next = 0;
	private Object[] lastData = {null};

	protected abstract E nextElement() throws NoSuchElementException;

	public int getNextIndex() {
		return next;
	}

	@Override
	public boolean hasNext() {
		if (last < next) {
			try {
				lastData[0] = nextElement();
			} catch (NoSuchElementException ignored) {
				lastData = null;
			}
			last++;
		}
		return lastData != null;
	}

	@Override
	public E next() {
		if (hasNext()) {
			next++;
			return (E) lastData[0];
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
