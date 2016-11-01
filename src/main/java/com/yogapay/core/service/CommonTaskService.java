package com.yogapay.core.service;

import com.yogapay.core.entity.CommonTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

import static com.yogapay.core.LangUitls.closeObject;

public class CommonTaskService {

	private DataSource dataSource;

	public void insertTask(CommonTask task) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("INSERT IGNORE common_task (name, single, alias) VALUES (?, 1, ?)");
			int k = 1;
			pstmt.setString(k++, task.getName());
			pstmt.setString(k++, task.getAlias());
			pstmt.executeUpdate();
		} finally {
			closeObject(pstmt);
			closeObject(conn);
		}
	}

	public boolean tryLock(CommonTask task) throws SQLException {
		if (!task.isSingle()) {
			throw new UnsupportedOperationException();
		}
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(""
					+ " UPDATE common_task SET locked=1, alias=?, runtime=?,  begin_time=NOW()"
					+ " WHERE name=? AND single=1 AND locked IS NULL");
			int k = 1;
			pstmt.setString(k++, task.getAlias());
			pstmt.setString(k++, task.getRuntime());
			pstmt.setString(k++, task.getName());
			return pstmt.executeUpdate() > 0;
		} finally {
			closeObject(pstmt);
			closeObject(conn);
		}
	}

	public void unlock(CommonTask task) throws SQLException {
		if (!task.isSingle()) {
			throw new UnsupportedOperationException();
		}
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(""
					+ " UPDATE common_task SET locked=NULL, alias=?, runtime=?,  end_time=NOW()"
					+ " WHERE name=? AND single=1 AND locked = 1");
			int k = 1;
			pstmt.setString(k++, task.getAlias());
			pstmt.setString(k++, task.getRuntime());
			pstmt.setString(k++, task.getName());
			pstmt.executeUpdate();
		} finally {
			closeObject(pstmt);
			closeObject(conn);
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
