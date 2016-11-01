package com.yogapay.sql.mybatis;

import com.yogapay.sql.mapping2.GlobalContext;
import com.yogapay.sql.mapping2.ResultSetMapper;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Properties;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

@Intercepts(
		@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}))
public class ResultSetMapperInterceptor implements Interceptor {

	protected GlobalContext globalContext;

	public ResultSetMapperInterceptor(GlobalContext globalContext) {
		this.globalContext = globalContext;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		ResultSet rs = ((Statement) invocation.getArgs()[0]).getResultSet();
		if (rs.getMetaData().getColumnLabel(1).startsWith(":")) {
			Object result = new ResultSetMapper(globalContext).mapping(rs);
			if (result == null) {
				return Collections.emptyList();
			} else {
				return result;
			}
		} else {
			return invocation.proceed();
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}
}
