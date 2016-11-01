package com.yogapay.sql.mapping2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class StringListSQLData extends ArrayList<String> implements SQLDataConvertible {

	public StringListSQLData(int initialCapacity) {
		super(initialCapacity);
	}

	public StringListSQLData() {
	}

	public StringListSQLData(Collection<? extends String> c) {
		super(c);
	}

	@Override
	public void toSQLData(PreparedStatement pstmt, int index) throws SQLException {
		Element eArray = DocumentHelper.createElement("List");
		for (String t : this) {
			Element eValue = DocumentHelper.createElement("value");
			eValue.setText(t == null ? "" : t);
			eArray.add(eValue);
		}
		pstmt.setString(index, eArray.asXML());
	}

	@Override
	public boolean fromSQLData(ResultSet rs, int index) throws SQLException {
		String xml = rs.getString(index);
		if (rs.wasNull()) {
			return false;
		}
		try {
			Document doc = DocumentHelper.parseText(xml);
			for (Iterator<Element> iterator = doc.getRootElement().elementIterator("value"); iterator.hasNext();) {
				Element next = iterator.next();
				this.add(next.getText());
			}
		} catch (DocumentException ex) {
			throw new SQLException("\r\n" + xml, ex);
		}
		return true;
	}

}
