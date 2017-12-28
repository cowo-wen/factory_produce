package com.app.service.sys;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.app.entity.sys.SysApplicationEntity;
import com.app.entity.sys.SysRoleEntity;
import com.app.util.RedisAPI;
import com.app.util.RedisKeyBean;

public class UserCache {
	
	public static Log logger = LogFactory.getLog(UserCache.class);
	
	private static RedisAPI redis = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
	
	/**
	 * 模糊删除多个用户的登录缓存
	 * @param userId
	 */
	public static void delUserLoginTemp(long userId){
		
		String tempLoginUser = RedisKeyBean.TEMP_USERINFO_LOGIN+userId+":*";//用户登录保存的用户信息key
		Set<String> userKeys = redis.keys(tempLoginUser);
		if(userKeys != null){
			Iterator<String> Iterator = userKeys.iterator();
			while(Iterator.hasNext()){
				redis.del(Iterator.next());
			}
		}
		
	}
	
	/**
	 * 删除除用户的登录缓存
	 * @param userId
	 */
	public static void delUserLoginTemp(long userId,String sessionId){
		
		redis.del(RedisKeyBean.TEMP_USERINFO_LOGIN+userId+":"+sessionId);//删除登录信息
		
	}
	
	public static void delUserLoginTemp(){
    	Set<String> set =redis.keys(RedisKeyBean.TEMP_USERINFO_LOGIN+"*");
    	if(set != null){
    		Iterator<String> iterator = set.iterator();
        	while(iterator.hasNext()){
        		String key = redis.get(iterator.next());
        		redis.del(key);
        	}
    	}
    	
	}
	
	/**
	 * 获取登录用户的缓存信息
	 * @param userId
	 */
	public static String getUserLoginInfo(long userId,String sessionId){
		return redis.get(RedisKeyBean.TEMP_USERINFO_LOGIN+userId+":"+sessionId);
		
	}
	
	/**
	 * 保存登录用户的缓存信息
	 * @param userId
	 */
	public static void saveUserLoginInfo(long userId,String sessionId,String result){
		 redis.putOneDay(RedisKeyBean.TEMP_USERINFO_LOGIN+userId+":"+sessionId,result);
	}
	
	/**
	 * 获取不前用户登录的角色
	 * @return
	 */
	public static Map<String,String> getCurrentRole(long userId,String sessionId){
		return redis.hgetAll(RedisKeyBean.TEMP_ROLE_CURRENT_SELECT+sessionId);
		
	}

	
	/**
	 * 保存当前用户的角色信息
	 * @param userId
	 * @param sessionId
	 * @param currentRole
	 */
	public static void setCurrentRole(long userId,String sessionId,SysRoleEntity role){
		
		redis.hSet(RedisKeyBean.TEMP_ROLE_CURRENT_SELECT+sessionId,new String[]{"role","time","pc_index","wc_index","name"},new String[]{String.valueOf(role.getRoleId()),String.valueOf(System.currentTimeMillis()),role.getPcIndex(),role.getWxIndex(),role.getRoleName()});
		redis.expire(RedisKeyBean.TEMP_ROLE_CURRENT_SELECT+sessionId, 86400);//保存一日
		
	}
	
	/**
	 * 删除用户登录的权限
	 * @param userId
	 * @param sessionId
	 */
	public static void delUserLoginPermission(long userId,String sessionId){
		redis.del(RedisKeyBean.TEMP_PERMISSION_APPLICATION_CODE+userId+":"+sessionId);
	}
	
	/**
	 * 保存用户登录权限
	 * @param userId
	 * @param sessionId
	 * @param list
	 */
	public static void setUserLoginPermission(long userId,String sessionId,List<SysApplicationEntity> list){
		String appCodeKey = RedisKeyBean.TEMP_PERMISSION_APPLICATION_CODE+userId+":"+sessionId;
		for(SysApplicationEntity entity : list){
			try{
				redis.hSet(appCodeKey, entity.getApplicationCode(), entity.getUrl());
			}catch(Exception e){
				logger.error("获取应用", e);
			}
		}
		redis.expire(appCodeKey, 86400);//设置一天有效
		
	}
	
	/**
	 * 获取用户指定的权限
	 * @param userId
	 * @param sessionId
	 * @param applicationCode
	 * @return
	 */
	public static String getUserOperatorPermission(long userId,String sessionId,String applicationCode){
		
		return redis.hget(RedisKeyBean.TEMP_PERMISSION_APPLICATION_CODE+userId+":"+sessionId, applicationCode,2);
		
	}
}
