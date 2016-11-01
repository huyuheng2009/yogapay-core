package com.yogapay.sql.mapping2;

import com.yogapay.core.BeanUtils;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.keyvalue.MultiKey;

public abstract class ByPropertyTypeSetter implements ColumnSetter {

	private static final Pattern P_COLUMN = Pattern.compile("_(\\w)");
	protected static final Object INVALID_VALUE = new Object();
	protected ConcurrentMap<Object, Property> propertyCache = new ConcurrentHashMap<Object, Property>();

	protected static class Property {

		protected final Class<?> type;
		protected final String propertyName;

		public Property(Class<?> type, String propertyName) {
			this.type = type;
			this.propertyName = propertyName;
		}

	}

	@Override
	public final boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception {
		BeanUtils b = ctx.getBeanUtils();
		MultiKey key = new MultiKey(entity.getClass(), columnName);
		Property p = propertyCache.get(key);
		if (p == null) {
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
			p = new Property(type, propertyName);
		}
		Object value = createValue(ctx, rs, entity, columnName, columnIndex, p);
		if (value == INVALID_VALUE) {
			return false;
		}
		b.setProperty(entity, p.propertyName, value);
		return true;
	}

	protected abstract Object createValue(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex, Property property) throws Exception;
}
