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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.xx.util.string.Format;

@Service
public class JdbcDao {
	public static Log logger = LogFactory.getLog(JdbcDao.class);
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	private PlatformTransactionManager txManager;
	
	private TransactionStatus status;
	
	private boolean autoCommit = true;
	
	/**
	 * 使用事务
	 * @return
	 */
	public TransactionStatus useTransaction(){
		 DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
		 def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);  //隔离级别 = 提交读
		 def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  //传播行为 = 必须有事务支持，即如果当前没有事务，就新建一个事务，如果已经存在一个事务中，就加入到这个事务中
		 if(txManager == null){
			 txManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
			 status = txManager.getTransaction(def);
			 autoCommit = false;
		 }
		 return status;
	}
	
	public boolean isAutoCommit() {
		return autoCommit;
	}



	/**
	 * 提交事务
	 * @param status
	 */
	public void commit(){
		if(txManager != null){
			txManager.commit(status);
		}
		
		 
	}
	
	/**
	 * 回滚事务
	 * @param status
	 */
	public void rollback(){
		if(txManager != null){
			txManager.rollback(status);
		}
	}
	
	//@Transactional(readOnly = true)  
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
	
	//@Transactional(readOnly = true)  
	public List<Map<String, Object>> getList(String sql) {
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}
	
	public int update(String sql, Object [] parem) {
		return jdbcTemplate.update(sql,parem);
	}
	
	
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
