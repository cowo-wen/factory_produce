package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import com.xx.util.string.Format;

@Service
public class JdbcDao {
	public static Log logger = LogFactory.getLog(JdbcDao.class);
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	private PlatformTransactionManager txManager;
	
	private TransactionStatus status;
	
	private boolean autoCommit = true;
	
	private Connection conn = null;
	
	/**
	 * 使用事务
	 * @return
	 */
	public TransactionStatus useTransaction(){
		// DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
		// def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);  //隔离级别 = 提交读
		 //def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  //传播行为 = 必须有事务支持，即如果当前没有事务，就新建一个事务，如果已经存在一个事务中，就加入到这个事务中
		// if(txManager == null){
		//	 txManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
		//	 status = txManager.getTransaction(def);
		//	 autoCommit = false;
		// }
		 
		conn = getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(TransactionDefinition.ISOLATION_READ_COMMITTED);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("-----------------连接异常",e);
		}
		
		 
		 return status;
	}
	
	private Connection getConnection(){
		if(this.conn == null){
			try {
				this.conn = jdbcTemplate.getDataSource().getConnection();
			} catch (SQLException e) {
				logger.error("-----------------获取连接异常",e);
			}
		}
		return this.conn;
	}
	
	public boolean isAutoCommit() {
		return autoCommit;
	}



	/**
	 * 提交事务
	 * @param status
	 */
	public void commit() throws SQLException{
		if(txManager != null){
			txManager.commit(status);
		}
		
		if(conn != null){
			conn.commit();
		}
		 
	}
	
	/**
	 * 回滚事务
	 * @param status
	 */
	public boolean rollback() {
		if(txManager != null){
			txManager.rollback(status);
		}
		
		if(conn != null){
			try{
				conn.rollback();
				return true;
			}catch(SQLException e){
				logger.error("回滚事务失败", e);
				return false;
			}
			
		}else{
			return true;
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
	
	
	public int update (final String sql) throws SQLException{
		Statement  st = getConnection().createStatement();
        return st.executeUpdate(sql);

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
	
	public long insert(final  String sql) throws SQLException{
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
		ps.executeUpdate();
	    ResultSet rs = ps.getGeneratedKeys(); 
	    if (rs.next()) {
	           return rs.getLong(1);
	    }else{
	    	return 0;
	    }
	}
	

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	
}
