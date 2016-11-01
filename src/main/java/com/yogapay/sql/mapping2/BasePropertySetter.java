package com.yogapay.sql.mapping2;

import com.yogapay.core.BeanUtils;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasePropertySetter implements ColumnSetter {

	private static final Map<Class, Method> ResultSetGetMethods;

	private final Pattern P_COLUMN = Pattern.compile("_(\\w)");

	static {
		Map<Class, String> map = new HashMap<Class, String>();
		map.put(BigDecimal.class, "getBigDecimal");
		map.put(InputStream.class, "getBinaryStream");
		map.put(Blob.class, "getBlob");
		map.put(Boolean.class, "getBoolean");
		map.put(Boolean.TYPE, "getBoolean");
		map.put(Byte.class, "getByte");
		map.put(Byte.TYPE, "getByte");
		map.put(byte[].class, "getBytes");
		map.put(Reader.class, "getCharacterStream");
		map.put(Clob.class, "getClob");
		map.put(java.sql.Date.class, "getDate");
		map.put(Double.TYPE, "getDouble");
		map.put(Double.class, "getDouble");
		map.put(Float.class, "getFloat");
		map.put(Float.TYPE, "getFloat");
		map.put(Integer.class, "getInt");
		map.put(Integer.TYPE, "getInt");
		map.put(Long.class, "getLong");
		map.put(Long.TYPE, "getLong");
		map.put(String.class, "getString");
		map.put(java.sql.Time.class, "getTime");
		map.put(java.sql.Timestamp.class, "getTimestamp");
		//
		map.put(java.util.Date.class, "getTimestamp");
		//
		ResultSetGetMethods = new HashMap<Class, Method>(map.size());
		try {
			for (Map.Entry<Class, String> t : map.entrySet()) {
				Method m = ResultSet.class.getMethod(t.getValue(), Integer.TYPE);
				ResultSetGetMethods.put(t.getKey(), m);
			}
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Map<Class, Method> getResultSetGetMethods() {
		return Collections.<Class, Method>unmodifiableMap(ResultSetGetMethods);
	}

	private Object parseDictionaryEnum(ResultSet rs, int columnIndex, Class type) throws SQLException {
		int _code = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			return null;
		}
		try {
			for (Field f : type.getFields()) {
				if ((f.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL)) != 0
						&& type.isAssignableFrom(f.getType())) {
					DictionaryEnum o = (DictionaryEnum) f.get(null);
					if (o.getCode() == _code) {
						return o;
					}
				}
			}
			return null;
		} catch (IllegalArgumentException ex) {
			throw new SQLException(null, ex);
		} catch (IllegalAccessException ex) {
			throw new SQLException(null, ex);
		}
	}

	@Override
	public boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception {
		BeanUtils b = ctx.getBeanUtils();
		String propertyName = columnName;
		Class<?> type = b.getNullablePropertyType(entity, propertyName);
		if (type == null) {
			Matcher m = P_COLUMN.matcher(propertyName);
			StringBuffer sb = null;
			while (m.find()) {
				if (sb == null) {
					sb = new StringBuffer();
				}
				m.appendReplacement(sb, m.group(1).toUpperCase());
			}
			if (sb != null) {
				m.appendTail(sb);
				propertyName = sb.toString();
				type = b.getNullablePropertyType(entity, propertyName);
			}
		}
		if (type == null) {
			return false;
		}
		Object value;
		if (SQLDataConvertible.class.isAssignableFrom(type)) {
			value = type.newInstance();
			if (!((SQLDataConvertible) value).fromSQLData(rs, columnIndex)) {
				value = null;
			}
		} else if (DictionaryEnum.class.isAssignableFrom(type)) {
			value = parseDictionaryEnum(rs, columnIndex, type);
		} else {
			Method rsm = ResultSetGetMethods.get(type);
			if (rsm == null) {
				return false;
			}
			value = rsm.invoke(rs, columnIndex);
			if (rs.wasNull() && !type.isPrimitive()) {
				value = null;
			}
		}
		b.setProperty(entity, propertyName, value);
		return true;
	}
}
