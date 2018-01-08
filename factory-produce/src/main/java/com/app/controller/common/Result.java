package com.app.controller.common;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.bean.SysUserDetails;
import com.app.dao.JdbcDao;
import com.app.entity.sys.SysLogEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
@Service
public class Result {
	
	public static Log logger = LogFactory.getLog(Result.class);
	
	
	private static ApplicationContext applicationContext = null;
	
	public static final String NAME = "name";
	public static final String S_ECHO = "sEcho";
	public static final String VALUE = "value";
	public static final String I_DISPLAY_START = "iDisplayStart";
	public static final String I_DISPLAY_LENGTH = "iDisplayLength";
	
	protected int iDisplayStart = 0;// 起始  
	protected int iDisplayLength = 10;// size 
	protected int sEcho = 0;
	
	protected JdbcDao jdbcDao;
	
	
	private static final Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(java.util.Date.class, new GsonDateConverter()).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	
	public Result() {
		super();
		if(applicationContext != null){
			jdbcDao = getJdbcDao();
		}
	}
	
	public Result(String daoName) {
		super();
		if(applicationContext != null){
			jdbcDao = getJdbcDao(daoName);
		}
	}

	public synchronized SysUserDetails getLoginUser(){
		return (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
	}
	
	
	
	

	public static void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}

	/**
	 * 该方法使用了事务，使用完需要提交事务
	 * 出错需要回滚事务
	 * @return
	 */
	public  JdbcDao getJdbcDao(){
		return getJdbcDao("jdbcDao");
	}
	
	public  JdbcDao getJdbcDao(String daoName){
		if(PublicMethod.isEmptyStr(daoName)) daoName = "jdbcDao";
		if(jdbcDao == null){
			synchronized (applicationContext) {
				jdbcDao = (JdbcDao)applicationContext.getBean(daoName);
				jdbcDao.useTransaction();
			}
		}
		return jdbcDao;
	}
	
	/**
	 * 检查登录用户是否是本人或者是超级管理员
	 * @param userId
	 * @return
	 */
	public synchronized boolean isCheckUserSelf(long userId){
		SysUserDetails userDetails =getLoginUser();
		if(userId != userDetails.getUserId().longValue() && userDetails.getType() != SysUserEntity.USER_ADMIN){
			return  false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized String success(Object obj){
		Map<String,Object> map = null;
		if(obj instanceof Map){
			map = (Map<String, Object>)obj;
			map.put("status", 200);
			if(map.containsKey("message")){
				map.put("message", "");
			}
		}else if(obj.getClass().equals(String.class)){
			map = new HashMap<String,Object>();
	    	map.put("status", 200);
	    	map.put("message", obj.toString());
	    	map.put("data", obj);
	    	map.put("id", "成功");
		}else{
			map = new HashMap<String,Object>();
	    	map.put("status", 200);
	    	map.put("data", obj);
	    	map.put("id", "成功");
		}
		if(jdbcDao != null){
			try {
				jdbcDao.commit();
			} catch (SQLException e) {
				boolean bool = jdbcDao.rollback();
				map.put("status", 500);
				if(bool){
					map.put("message", "提交事务失败:"+e.getMessage());
				}else{
					map.put("message", "提交事务失败,并且数据回滚失败:"+e.getMessage());
				}
		    	
			}
		}
		
		return gson.toJson(map);
	}
	
	
	
	public synchronized String success(String message,Object id){
		Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("message", message);
    	map.put("id", id);
    	logger.error("--------------jdbcDao:"+jdbcDao);
    	if(jdbcDao != null){
    		logger.error("--------------提交事务");
			try {
				jdbcDao.commit();
			} catch (SQLException e) {
				boolean bool = jdbcDao.rollback();
				map.put("status", 500);
				if(bool){
					map.put("message", "提交事务失败:"+e.getMessage());
				}else{
					map.put("message", "提交事务失败,并且数据回滚失败:"+e.getMessage());
				}
		    	
			}
		}
    	return gson.toJson(map);
    	//return new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
	}
	
	public synchronized String error(String message){
		Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 500);
    	map.put("message", message);
    	if(jdbcDao != null){
    		jdbcDao.rollback();
		}
    	
    	return gson.toJson(map);
    	//return new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
	}

	
	public void insertLog(String applicationCode,long id,String ip,String message) throws Exception{
		SysLogEntity log = new SysLogEntity(jdbcDao);
		log.setApplicationCode(applicationCode);
		log.setDataId(id);
		log.setIp(ip);
		log.setUserId(getLoginUser().getUserId());
		log.setType(SysLogEntity.TYPE_SUCCESS);
		log.setMessage(message);
		log.insert();
	}
}
