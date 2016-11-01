package com.yogapay.core.task;

import com.yogapay.core.entity.CommonTask;
import com.yogapay.core.service.CommonTaskService;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import static com.yogapay.core.LangUitls.closeObject;

public class CommonCacheTask implements InitializingBean {
	
	private DataSource dataSource;
	private CommonTaskService commonTaskService;
	
	private CommonTask createTask() {
		CommonTask task = new CommonTask();
		task.setName(CommonCacheTask.class.getName());
		task.setSingle(true);
		return task;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		CommonTask task = createTask();
		commonTaskService.insertTask(task);
	}
	
	public void clearTimeout() throws SQLException {
		CommonTask task = createTask();
		task.setAlias("定时清除缓存");
		if (commonTaskService.tryLock(task)) {
			try {
				Connection conn = dataSource.getConnection();
				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					stmt.executeUpdate(""
							+ " DELETE FROM common_cache WHERE 0"
							+ " OR (time_to_live_seconds > 0 AND NOW() >= DATE_ADD(create_time, INTERVAL time_to_live_seconds SECOND))"
							+ " OR (time_to_idle_seconds > 0 AND NOW() >= DATE_ADD(last_access_time, INTERVAL time_to_idle_seconds SECOND))");
				} finally {
					closeObject(stmt);
					closeObject(conn);
				}
			} catch (Exception ex) {
				LoggerFactory.getLogger(CommonCacheTask.class).error(null, ex);
			} finally {
				commonTaskService.unlock(task);
			}
		}
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public CommonTaskService getCommonTaskService() {
		return commonTaskService;
	}
	
	public void setCommonTaskService(CommonTaskService commonTaskService) {
		this.commonTaskService = commonTaskService;
	}
	
}
