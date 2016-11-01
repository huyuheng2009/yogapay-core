package com.yogapay.sql.mapping2;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import org.apache.commons.io.IOUtils;

public class SerializableSetter extends ByPropertyTypeSetter {

	@Override
	protected Object createValue(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex, Property property) throws Exception {
		if (Serializable.class.isAssignableFrom(property.type)) {
			byte[] bytes = rs.getBytes(columnIndex);
			if (bytes == null) {
				return null;
			} else {
				ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(bytes));
				try {
					return oin.readObject();
				} finally {
					IOUtils.closeQuietly(oin);
				}
			}
		} else {
			return INVALID_VALUE;
		}
	}

}
