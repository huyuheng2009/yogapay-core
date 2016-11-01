package com.yogapay.sql.mapping2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

@Deprecated
public class XMLStringSQLData implements SQLDataConvertible {

	private List<String> value;

	public XMLStringSQLData() {
	}

	public XMLStringSQLData(int size) {
		this.value = new ArrayList<String>(size);
	}

	public XMLStringSQLData(List<String> value) {
		this.value = value;
	}

	@Override
	public void toSQLData(PreparedStatement pstmt, int index) throws SQLException {
		if (value == null) {
			pstmt.setNull(index, Types.VARCHAR);
		} else {
			Element eArray = DocumentHelper.createElement("Array");
			for (String t : value) {
				Element eValue = DocumentHelper.createElement("value");
				eValue.setText(t == null ? "" : t);
				eArray.add(eValue);
			}
			pstmt.setString(index, eArray.asXML());
		}
	}

	@Override
	public boolean fromSQLData(ResultSet rs, int index) throws SQLException {
		String xml = rs.getString(index);
		if (rs.wasNull()) {
			return false;
		}
		try {
			value = new ArrayList<String>();
			Document doc = DocumentHelper.parseText(xml);
			for (Iterator<Element> iterator = doc.getRootElement().elementIterator("value"); iterator.hasNext();) {
				Element next = iterator.next();
				value.add(next.getText());
			}
		} catch (DocumentException ex) {
			throw new SQLException("\r\n" + xml, ex);
		}
		return true;
	}

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

}
