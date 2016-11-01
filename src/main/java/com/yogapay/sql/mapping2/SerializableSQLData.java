package com.yogapay.sql.mapping2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Deprecated
public class SerializableSQLData<V extends Serializable> implements SQLDataConvertible {

	private V value;

	public SerializableSQLData() {
	}

	public SerializableSQLData(V value) {
		this.value = value;
	}

	@Override
	public void toSQLData(PreparedStatement pstmt, int index) throws SQLException {
		if (value == null) {
			pstmt.setNull(index, Types.BLOB);
		} else {
			try {
				ByteArrayOutputStream buff = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(buff);
				try {
					out.writeObject(value);
					out.flush();
				} finally {
					out.close();
				}
				pstmt.setBinaryStream(index, new ByteArrayInputStream(buff.toByteArray()), buff.size());
			} catch (IOException ex) {
				throw new SQLException(ex);
			}
		}
	}

	@Override
	public boolean fromSQLData(ResultSet rs, int index) throws SQLException {
		InputStream in = rs.getBinaryStream(index);
		if (in == null) {
			return false;
		} else {
			try {
				ObjectInputStream oin = new ObjectInputStream(in);
				try {
					value = (V) oin.readObject();
				} finally {
					oin.close();
				}
			} catch (ClassNotFoundException ex) {
				throw new SQLException(ex);
			} catch (IOException ex) {
				throw new SQLException(ex);
			}
			return true;
		}
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

}
