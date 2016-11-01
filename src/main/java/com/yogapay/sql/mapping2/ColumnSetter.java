package com.yogapay.sql.mapping2;

import java.sql.ResultSet;

public interface ColumnSetter {

	public boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception;
}
