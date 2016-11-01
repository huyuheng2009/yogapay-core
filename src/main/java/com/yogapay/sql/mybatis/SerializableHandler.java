package com.yogapay.sql.mybatis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class SerializableHandler implements TypeHandler<Serializable> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Serializable parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			ps.setNull(i, Types.BLOB);
		} else {
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
		}
	}

	@Override
	public Serializable getResult(ResultSet rs, String columnName) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Serializable getResult(ResultSet rs, int columnIndex) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Serializable getResult(CallableStatement cs, int columnIndex) throws SQLException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
