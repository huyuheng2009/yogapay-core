package com.yogapay.sql.mybatis;

import com.yogapay.sql.mapping2.SQLDataConvertible;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class SQLDataConvertibleHandler implements TypeHandler<SQLDataConvertible> {

	@Override
	public void setParameter(PreparedStatement ps, int i, SQLDataConvertible parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			ps.setNull(i, jdbcType.TYPE_CODE);
			return;
		}
		parameter.toSQLData(ps, i);
	}

	@Override
	public SQLDataConvertible getResult(ResultSet rs, String columnName) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public SQLDataConvertible getResult(ResultSet rs, int columnIndex) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public SQLDataConvertible getResult(CallableStatement cs, int columnIndex) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
