package com.yogapay.sql.mybatis;

import com.yogapay.sql.QueryCondition;
import com.yogapay.sql.QueryMoreCondition;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDAO {

	protected final String STATEMENT_ID;

	protected SqlSessionFactory sqlSessionFactory;

	public BaseDAO() {
		STATEMENT_ID = getClass().getName() + ".";
	}

	public static java.sql.Date toSQLDate(Date date) {
		if (date == null) {
			return null;
		} else if (date instanceof java.sql.Date) {
			return (java.sql.Date) date;
		}
		return new java.sql.Date(date.getTime());
	}

	public BaseDAO(SqlSessionFactory sqlSessionFactory) {
		this();
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	protected int hasError(SqlSession session, String statement, Object parameter, int errorCode)
			throws PersistenceException {
		try {
			return session.update(statement, parameter);
		} catch (PersistenceException ex) {
			Throwable t = ex.getCause();
			if (t instanceof SQLException) {
				if (((SQLException) t).getErrorCode() == errorCode) {
					return -1;
				}
			}
			throw ex;
		}
	}

	protected static Map singleParameter(Object value) {
		Map map = new HashMap();
		map.put("value", value);
		return map;
	}

	/**
	 *
	 * @param sqlMapId
	 * @param page
	 * @param pageSize
	 * @param param 如果 param 是 {@link Map} ,mapper SQL中可以直接用参数名访问 ${name}； 否则必须用
	 * ${bean.name} 的方式访问。
	 * @return
	 */
	protected Paginated selectPaginatedList(String sqlMapId, int page, int pageSize, Object param) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (param != null) {
				if (param instanceof Map) {
					map.putAll((Map) param);
				} else {
					map.put("param", param);
				}
			}
			map.put("is_count", true);
			map.put("limit", false);
			Integer count = session.selectOne(sqlMapId, map);
			//
			Paginated p = new Paginated();
			p.input(page, pageSize, count);
			//
			if (pageSize > 0) {
				Object result;
				map.put("is_count", false);
				map.put("limit", true);
				map.put("offset", p.getPage() < 1 ? 0 : ((p.getPage() - 1) * p.getEachPageNum()));
				map.put("len", p.getEachPageNum());
				result = session.selectList(sqlMapId, map);
				map.clear();
				//
				p.setData(result);
			}
			return p;
		} finally {
			session.close();
		}
	}

	protected Paginated selectPaginatedOnList(String sqlMapId, int page, int pageSize, Map<String, Object> param) {
		SqlSession session = sqlSessionFactory.openSession();
		try {

			param.put("is_count", true);
			param.put("limit", false);
			Integer count = session.selectOne(sqlMapId, param);

			Paginated p = new Paginated();
			p.input(page, pageSize, count);
			//
			Object result;
			param.put("is_count", false);
			param.put("limit", true);
			param.put("offset", p.getPage() < 1 ? 0 : ((p.getPage() - 1) * p.getEachPageNum()));
			param.put("len", p.getEachPageNum());
			result = session.selectList(sqlMapId, param);
			param.clear();
			p.setData(result);
			return p;
		} finally {
			session.close();
		}
	}

	protected Paginated selectPaginatedCountList(String sqlMapId, int page, int pageSize, Map<String, Object> param) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			int total = (Integer) session.selectOne(sqlMapId + "_count", param);
			Paginated<List<?>> p = new Paginated<List<?>>();
			p.input(page, pageSize, total);
			page = p.getPage();
			if (total > 0) {
				p.setData(session.selectList(sqlMapId, param, new RowBounds((page - 1) * pageSize, pageSize)));
			} else {
				p.setData(Collections.EMPTY_LIST);
			}
			return p;
		} finally {
			session.close();
		}
	}

	protected com.yogapay.sql.Paginated selectPaginated(String sqlMapId, QueryCondition cdt) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			com.yogapay.sql.Paginated p = session.selectOne(sqlMapId, cdt);
			if (p == null) {
				p = com.yogapay.sql.Paginated.empty();
			} else {
				p.setPage(cdt.getPage());
			}
			p.setPageSize(cdt.getPageSize());
			return p;
		} finally {
			session.close();
		}
	}

	protected void selectMore(String sqlMapId, QueryMoreCondition cdt) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			List<Object> data = session.selectList(sqlMapId, cdt);
			if (data != null && data.size() > cdt.getPageSize()) {
				cdt.setHasMore(true);
				data = data.subList(0, cdt.getPageSize());
			}
			cdt.setData(data);
		} finally {
			session.close();
		}
	}

	protected List selectList(String sqlMapId) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.selectList(sqlMapId);
		} finally {
			session.close();
		}

	}

	protected List selectList(String sqlMapId, Object obj) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.selectList(sqlMapId, obj);
		} finally {
			session.close();
		}

	}

	protected Object selectOne(String sqlMapId, Object obj) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.selectOne(sqlMapId, obj);
		} finally {
			session.close();
		}
	}

	protected Integer selectInteger(String sqlMapId, Object obj) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			Integer count = session.selectOne(sqlMapId, obj);
			return count;
		} finally {
			session.close();
		}
	}

	protected int insert(String sqlMapId, Object obj) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.insert(sqlMapId, obj);
		} finally {
			session.close();
		}
	}

	protected int update(String sqlMapId, Object obj) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.update(sqlMapId, obj);
		} finally {
			session.close();
		}
	}

	protected int delete(String sqlMapId, Object obj) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.delete(sqlMapId, obj);
		} finally {
			session.close();
		}
	}

	protected <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.selectMap(statement, mapKey);
		} finally {
			session.close();
		}
	}

	protected <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			return session.selectMap(statement, parameter, mapKey);
		} finally {
			session.close();
		}
	}
}
