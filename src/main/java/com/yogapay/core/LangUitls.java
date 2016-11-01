package com.yogapay.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.LoggerFactory;

public class LangUitls {

	public static <E> E getNotNull(E nullable, E defaultValue) {
		return nullable == null ? defaultValue : nullable;
	}

	public static <K, V> V putIfAbsent(ConcurrentMap<K, V> map, K key, V value) {
		V v = map.putIfAbsent(key, value);
		return v == null ? value : v;
	}

	public static void closeObject(Object obj) {
		try {
			if (obj == null) {
			} else if (obj instanceof ResultSet) {
				((ResultSet) obj).close();
			} else if (obj instanceof Statement) {
				((Statement) obj).close();
			} else if (obj instanceof Connection) {
				((Connection) obj).close();
			} else {
				throw new UnsupportedOperationException();
			}
		} catch (SQLException ex) {
			LoggerFactory.getLogger(LangUitls.class).error(null, ex);
		}
	}
}
