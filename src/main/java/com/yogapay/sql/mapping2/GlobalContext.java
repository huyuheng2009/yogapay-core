package com.yogapay.sql.mapping2;

import com.yogapay.core.BeanUtils;

public class GlobalContext {

	protected BeanUtils beanUtils;
	protected String defaultEntityPackage;
	protected ColumnSetter[] columnSetters;

	public GlobalContext() {
		beanUtils = new BeanUtils();
		columnSetters = new ColumnSetter[]{new CachedColumnSetter(
			new MapSetter(),
			new DirectResultSetter(),
			new SQLDataSetter(), new BasePropertySetter2(), new SerializableSetter()
			)};
	}

	public BeanUtils getBeanUtils() {
		return beanUtils;
	}

	public void setBeanUtils(BeanUtils beanUtils) {
		this.beanUtils = beanUtils;
	}

	public String getDefaultEntityPackage() {
		return defaultEntityPackage;
	}

	public void setDefaultEntityPackage(String defaultEntityPackage) {
		this.defaultEntityPackage = defaultEntityPackage;
	}

	public ColumnSetter[] getColumnSetters() {
		return columnSetters;
	}

	public void setColumnSetters(ColumnSetter[] columnSetters) {
		this.columnSetters = columnSetters;
	}
}
