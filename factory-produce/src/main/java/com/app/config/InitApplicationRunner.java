package com.app.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.app.controller.common.Result;
import com.app.dao.JdbcDao;
import com.app.entity.sys.SysRoleEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.util.RedisAPI;
import com.app.util.RedisBean;
import com.app.util.StaticBean;
import com.xx.util.string.MD5;

@Component
public class InitApplicationRunner implements ApplicationRunner,ApplicationContextAware {
	
	public static Log logger = LogFactory.getLog(InitApplicationRunner.class);
	
	private ApplicationContext applicationContext;
	
	@Autowired
	private SysConfigProperties sysConfig;
	

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}


	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		RedisBean bean = new RedisBean();
		bean.setJedisIp(sysConfig.getRedis_ip());
		bean.setJedisPort(Integer.parseInt(sysConfig.getRedis_port()));
		RedisAPI.setJedisBeanMap(RedisAPI.REDIS_CORE_DATABASE, bean);
		Result.setApplicationContext(applicationContext);
		SystemTaskThread.startThread();
		new Thread() {
            @Override
            public void run() {
            	try {
					sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            	JdbcDao jdbcDao = (JdbcDao)applicationContext.getBean("jdbcDao");
            	SysUserEntity user = new SysUserEntity(jdbcDao);
        		user.setLoginName(SysUserEntity.ADMIN_USER_NAME);
        		user.setType(SysUserEntity.USER_ADMIN);
        		user.setUserName("超级管理员");
        		user.setPassword(MD5.encode("123456"));
        		user.setValid(StaticBean.YES);
        		user.setNumber("0");
        		try{
        			user.insert();
        			
        		}catch(Exception e){
        			logger.error("新增"+user.getLoginName()+"用户出错", e);
        		}
        		SysRoleEntity role = new SysRoleEntity(jdbcDao);
        		role.setRemark("系统创建");
        		role.setRoleCode(SysRoleEntity.ADMIN_CODE);
        		role.setLinkCode(SysRoleEntity.ADMIN_CODE);
        		role.setParentId(0L);
        		role.setPcIndex("/welcome.html");
        		role.setValid(StaticBean.YES);
        		role.setRoleName("超级管理员");
        		try{
        			role.insert();
        		}catch(Exception e){
        			logger.error("新增"+role.getRoleName()+"角色出错", e);
        		}
            }
        }.start();
        
		
		
		
		logger.error("初始化结束");

	}

}
