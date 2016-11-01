package com.yogapay.sql.mybatis;

import com.yogapay.sql.mapping2.SQLDataConvertible;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.ObjectTypeHandler;

public class CommonTypeHandler extends ObjectTypeHandler {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		if (parameter instanceof SQLDataConvertible) {
			((SQLDataConvertible) parameter).toSQLData(ps, i);
			return;
		}
		if (parameter instanceof Serializable && jdbcType == JdbcType.BLOB) {
			try {
				ByteArrayOutputStream buff = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(buff);
				try {
					out.writeObject(parameter);
					out.flush();
				} finally {
					out.close();
				}
				ps.setBytes(i, buff.toByteArray());
			} catch (IOException ex) {
				throw new SQLException(ex);
			}
			return;
		}
		ps.setObject(i, parameter);
	}

}
