package com.yogapay.core.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import static com.yogapay.core.LangUitls.closeObject;

public class CommonCacheService<V extends Serializable> {

	private String name;
	private DataSource dataSource;
	private volatile int timeToLiveSeconds;
	private volatile int timeToIdleSeconds;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public V get(String key) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(
					"SELECT id, name, `key`, value, time_to_idle_seconds, last_access_time FROM common_cache WHERE name=? AND `key`=?",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			int k = 1;
			pstmt.setString(k++, name);
			pstmt.setString(k++, key);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				byte[] bytes = rs.getBytes("value");
				Object value = SerializationUtils.deserialize(bytes);
				if (rs.getInt("time_to_idle_seconds") > 0) {
					rs.updateTimestamp("last_access_time", new Timestamp(System.currentTimeMillis()));
					rs.updateRow();
				}
				return (V) value;
			} else {
				return null;
			}
		} finally {
			closeObject(rs);
			closeObject(pstmt);
			closeObject(conn);
		}
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public V putIfAbsent(String key, V value) throws SQLException {
		if (value == null) {
			throw new NullPointerException();
		}
		V v = get(key);
		if (v != null) {
			return v;
		}
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(""
					+ " INSERT common_cache (name,`key`,value,time_to_live_seconds,time_to_idle_seconds,last_access_time,create_time)"
					+ " VALUES (?,?,?,?,?,?,?)");
			int k = 1;
			pstmt.setString(k++, name);
			pstmt.setString(k++, key);
			pstmt.setBytes(k++, SerializationUtils.serialize(value));
			pstmt.setInt(k++, timeToLiveSeconds);
			pstmt.setInt(k++, timeToIdleSeconds);
			Timestamp time = new Timestamp(System.currentTimeMillis());
			pstmt.setTimestamp(k++, time);
			pstmt.setTimestamp(k++, time);
			pstmt.executeUpdate();
		} finally {
			closeObject(pstmt);
			closeObject(conn);
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(int timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	public int getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public void setTimeToIdleSeconds(int timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

}
