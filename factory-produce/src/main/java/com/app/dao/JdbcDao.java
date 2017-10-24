package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xx.util.string.Format;

@Service
public class JdbcDao {
	public static Log logger = LogFactory.getLog(JdbcDao.class);
	@Resource
	JdbcTemplate jdbcTemplate;
	
	@Transactional(readOnly = true)  
	public Map<String, Object> getVo(String table, Object value, String id) {
		String sql = null;
		if(Format.isNumeric(value.toString())){
			sql = "select * from " + table + " where " + id + "="+ Long.parseLong(value.toString());
		}else{
			sql = "select * from " + table + " where " + id + "='"+ value.toString()+"'";
		}
		logger.error(sql);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly = true)  
	public List<Map<String, Object>> getList(String sql) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
	
	@Transactional 
	public int update(String sql, Object [] parem) {
		return jdbcTemplate.update(sql,parem);
	}
	
	@Transactional 
	public long insert(final  String sql, final Object [] parem){
		KeyHolder keyHolder = new GeneratedKeyHolder();  
		jdbcTemplate.update(new PreparedStatementCreator() {  
		    @Override  
		    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {  
		        PreparedStatement ps = (PreparedStatement) connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		        return ps;  
		    }  
		}, keyHolder);  
		return keyHolder.getKey().longValue();
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	
	
}
