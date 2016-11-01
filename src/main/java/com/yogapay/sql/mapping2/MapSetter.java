package com.yogapay.sql.mapping2;

import java.sql.ResultSet;
import java.util.Map;

public class MapSetter implements ColumnSetter {

	@Override
	public boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception {
		if (!(entity instanceof Map)) {
			return false;
		}
		((Map) entity).put(columnName, rs.getObject(columnIndex));
		return true;
	}

}
