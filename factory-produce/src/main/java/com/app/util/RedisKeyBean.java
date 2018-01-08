/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2016-12-25
 */
package com.app.util;

/**
 * 功能说明：缓存key值
 * 
 * @author chenwen 2014-12-25
 */
public class RedisKeyBean
{
	/**
	 * 用户登录保存的用户信息key
	 */
	public static final String TEMP_USERINFO_LOGIN = "temp:userinfo:login:";
	
	
	/**
	 * 用户当前使用的角色
	 */
	public static final String TEMP_ROLE_CURRENT_SELECT = "temp:role:current:select:";
	
	/**
	 * 用户当前可用的权限
	 */
	public static final String TEMP_PERMISSION_APPLICATION_CODE="temp:permission:application_code:";
	

}
