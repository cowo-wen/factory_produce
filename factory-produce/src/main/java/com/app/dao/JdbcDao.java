package com.app.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
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
		Map<String, Object> mapVo = jdbcTemplate.queryForMap(sql);
		return mapVo;
	}
	
	@Transactional(readOnly = true)  
	public List<Map<String, Object>> getList(String sql) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	
	
}
