package com.yogapay.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupThreadFactory implements ThreadFactory {

	private final AtomicInteger index = new AtomicInteger();
	private final ThreadGroup group;
	private final String name;
	private final boolean daemon;

	public GroupThreadFactory(String name, boolean daemon) {
		group = new ThreadGroup(name);
		this.name = name;
		this.daemon = daemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, name + "-" + index.getAndIncrement());
		t.setDaemon(daemon);
		return t;
	}

}
