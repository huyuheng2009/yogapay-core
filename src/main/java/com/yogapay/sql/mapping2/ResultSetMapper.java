package com.yogapay.sql.mapping2;

import com.yogapay.core.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetMapper {

	protected static final Object INVALID_ENTITY = new Object();
	//
	private final GlobalContext context;
	//
	private final Set<Object> cantSet = new HashSet<Object>();
	private final Map IDMap = new HashMap();
	private final Deque node = new LinkedList();
	private ResultSet rs;
	private Object result;
	//
	private Label label;
	private Object target;
	private String property;
	private MultiKey IDKey;
	private Class entityClass;
	private String MAPKeyPropertyName;
	private INJECTING_TYPE injectingType;
	private Object injectingParam;
	private Object entity;
	private Object cachedEntity;

	private static enum INJECTING_TYPE {

		LIST, MAP
	}

	public ResultSetMapper(GlobalContext context) {
		this.context = context;
	}

	protected void releaseMappingScopeVariables() {
		IDMap.clear();
		node.clear();
		rs = null;
		result = null;
	}

	protected void releaseLabelScopeVariables() {
		label = null;
		target = null;
		property = null;
		IDKey = null;
		entityClass = null;
		MAPKeyPropertyName = null;
		injectingType = null;
		injectingParam = null;
		entity = null;
		cachedEntity = null;
	}

	protected void handleID(String[] params) throws Exception {
		if (params.length == 0) {
			IDKey = new MultiKey(label.columnIndex, null);
			cachedEntity = IDMap.get(IDKey);
			return;
			//throw new SQLException(String.format("[%s][ID]必须添加一个columnLabel作为Key", label));
		}
		String columnLabel = params[0];
		for (Map.Entry<Integer, String> entry : label.propertyColumns.entrySet()) {
			if (columnLabel.equals(entry.getValue())) {
				Integer index = entry.getKey();
				Object key = rs.getObject(index);
				if (rs.wasNull()) {
					entity = INVALID_ENTITY;
					return;
				}
				IDKey = new MultiKey(index, key);
				MAPKeyPropertyName = columnLabel;
				cachedEntity = IDMap.get(IDKey);
				return;
			}
		}
		throw new SQLException(String.format("[%s][ID]找不到相应的columnLabel", label));
	}

	protected void handleENTITY(String[] params) throws Exception {
		if (params.length < 1) {
			throw new SQLException(String.format("[%s][ENTITY]必须添加一个参数指定实体类Class", label));
		}
		String className = params[0];
		if (className.indexOf('.') < 0 && context.defaultEntityPackage != null) {
			className = context.defaultEntityPackage + "." + className;
		}
		entityClass = Thread.currentThread().getContextClassLoader().loadClass(className);
	}

	public static void checkPropertyType(Label label, Class propertyType, Class dest) throws SQLException {
		if (!propertyType.isAssignableFrom(dest)) {
			throw new SQLException(String.format("[%s]的属性不是 %s 类型", label, dest.getName()));
		}
	}

	protected void handleLIST(String[] params) throws Exception {
		injectingType = INJECTING_TYPE.LIST;
		//
		if (target == null) {
			if (result == null) {
				result = new ArrayList();
			}
			injectingParam = result;
		} else {
			BeanUtils bu = context.getBeanUtils();
			Class propertyType = bu.getPropertyType(target, property);
			checkPropertyType(label, propertyType, List.class);
			//
			Object list = (List) bu.getProperty(target, property);
			if (list == null) {
				if (List.class.isAssignableFrom(propertyType)) {
					if (propertyType.isInterface()) {
						list = new ArrayList();
					} else {
						list = propertyType.newInstance();
					}
				} else {
					list = new ArrayList();
				}
				bu.setProperty(target, property, list);
			}
			injectingParam = list;
			if (entityClass == null) {
				ParameterizedType type = null;
				Type _type = bu.getGenericPropertyType(target, property);
				while (_type != null) {
					if (_type instanceof ParameterizedType) {
						type = (ParameterizedType) _type;
						break;
					}
					if (_type instanceof Class) {
						_type = ((Class) _type).getGenericSuperclass();
					} else {
						break;
					}
				}
				if (type != null) {
					Type[] args = type.getActualTypeArguments();
					if (args.length > 0 && args[0] instanceof Class) {
						entityClass = (Class) args[0];
					}
				}
			}
		}
	}

	protected void handleMAP(String[] params) throws Exception {
		injectingType = INJECTING_TYPE.MAP;
		if (params.length > 0) {
			MAPKeyPropertyName = params[0];
		}
		if (MAPKeyPropertyName == null) {
			throw new SQLException(String.format("[%s][MAP]没有相应的Key", label));
		}
		if (target == null) {
			if (result == null) {
				result = new HashMap();
			}
			injectingParam = result;
		} else {
			BeanUtils bu = context.getBeanUtils();
			checkPropertyType(label, bu.getPropertyType(target, property), Map.class);
			Map map = (Map) bu.getProperty(target, property);
			if (map == null) {
				map = new HashMap();
				bu.setProperty(target, property, map);
			}
			injectingParam = map;
			if (entityClass == null) {
				ParameterizedType type = (ParameterizedType) bu.getGenericPropertyType(target, property);
				Type[] args = type.getActualTypeArguments();
				if (args.length > 1 && args[1] instanceof Class) {
					entityClass = (Class) args[0];
				}
			}
		}
	}

	protected void deduce() throws Exception {
		if (target == null) {
			if (injectingType == null) {
				handleLIST(Label.EMPTY_PARAMS);
			}
		} else {
			BeanUtils bu = context.getBeanUtils();
			Class<?> propertyType = bu.getPropertyType(target, property);
			if (injectingType == null) {
				if (List.class.isAssignableFrom(propertyType)) {
					handleLIST(Label.EMPTY_PARAMS);
				}
			}
			if (entityClass == null) {
				entityClass = propertyType;
			}
		}
	}

	protected void createEntity(Label label, Object target, String property) throws Exception {
		if (entityClass == null) {
			throw new SQLException(String.format("[%s]无法获取实体类的Class", label));
		}
		entity = entityClass.newInstance();
	}

	protected void writeEntityProperty() throws Exception {
		for (Map.Entry<Integer, String> entry : label.propertyColumns.entrySet()) {
			Integer columnIndex = entry.getKey();
			String columnName = entry.getValue();
			a:
			{
				for (ColumnSetter t : context.columnSetters) {
					if (t.set(context, rs, entity, columnName, columnIndex)) {
						break a;
					}
				}
				Logger log = LoggerFactory.getLogger(ResultSetMapper.class);
				if (log.isDebugEnabled() && cantSet.add(columnIndex)) {
					log.debug(String.format(
							"不能转换结果集：在 [%s] 中找不到字段 [%s(%s)] 对应的写入方法。",
							entity.getClass(), columnName, rs.getMetaData().getColumnTypeName(columnIndex)));
				}
			}
		}
	}

	protected void injectEntity() throws Exception {
		/**
		 * 扩展时注意当使用ID标签时，可能取到缓存的entity
		 *
		 * 此时注入的目标可能一样,也可能不一样
		 */
		BeanUtils bu = context.getBeanUtils();
		if (injectingType == null) {
			bu.setProperty(target, property, entity);
			return;
		}
		switch (injectingType) {
			case LIST:
				Collection collection = (Collection) injectingParam;
				if (!collection.contains(entity)) {
					collection.add(entity);
				}
				break;
			case MAP:
				Map map = (Map) injectingParam;
				Object key = bu.getProperty(entity, MAPKeyPropertyName);
				map.put(key, entity);
				break;
			default:
				throw new SQLException(String.format("[%s]未知的注入类型[%d]", label, injectingType));
		}
	}

	protected void handleLabel_noResult(List<Label> labels) throws Exception {
		label = labels.get(0);
		String[] params;
		if ((params = label.functions.get("MAP")) != null) {
			handleMAP(params);
		} else {
			handleLIST(params);
		}
	}

	protected void handleLabel() throws Exception {
		this.property = label.property;
		if (target != null && property == null) {
			throw new SQLException(String.format("[%s]没有属性名", label));
		}
		if (INVALID_ENTITY == target) {
			return;
		}
		String[] params;
		if ((params = label.functions.get("ID")) != null) {
			handleID(params);
			if (entity == INVALID_ENTITY) {
				return;
			}
		}
		if ((params = label.functions.get("ENTITY")) != null) {
			handleENTITY(params);
		}
		if ((params = label.functions.get("LIST")) != null) {
			handleLIST(params);
		} else if ((params = label.functions.get("MAP")) != null) {
			handleMAP(params);
		}
		deduce();
		if (cachedEntity == null) {
			createEntity(label, target, property);
			writeEntityProperty();
			if (IDKey != null) {
				IDMap.put(IDKey, entity);
			}
		} else {
			entity = cachedEntity;
		}
		injectEntity();
	}

	protected void updateNode() throws Exception {
		for (int k; (k = label.zindex - node.size()) != 0;) {
			if (k > 0) {
				throw new SQLException(String.format("[%s] zindex 错误", label));
			}
			node.removeLast();
		}
	}

	public Object mapping(ResultSet rs) throws SQLException {
		try {
			this.rs = rs;
			//
			ResultSetMetaData md = rs.getMetaData();
			int n = md.getColumnCount();
			String[] labelNames = new String[n];
			for (int i = 0; i < n; i++) {
				labelNames[i] = md.getColumnLabel(i + 1);
			}
			List<Label> labels = Label.parse(labelNames);
			//
			try {
				if (rs.next()) {
					do {
						for (Label t : labels) {
							try {
								this.label = t;
								updateNode();
								this.target = node.isEmpty() ? null : node.getLast();
								handleLabel();
								node.addLast(entity);
							} finally {
								releaseLabelScopeVariables();
							}
						}
						node.clear();
					} while (rs.next());
				} else {
					handleLabel_noResult(labels);
					releaseLabelScopeVariables();
				}
			} catch (SQLException ex) {
				throw ex;
			} catch (InvocationTargetException ex) {
				throw new SQLException(String.valueOf(label), ex.getTargetException());
			} catch (Exception ex) {
				throw new SQLException(String.valueOf(label), ex);
			}
			return result;
		} finally {
			releaseMappingScopeVariables();
		}
	}
}
