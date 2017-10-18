package com.app.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.app.entity.sys.SysUserEntity;
import com.app.service.sys.SysUserService;
import com.app.util.RedisAPI;
import com.app.util.RedisBean;
import com.xx.util.string.MD5;

@Component
public class InitApplicationRunner implements ApplicationRunner {
	
	public static Log logger = LogFactory.getLog(InitApplicationRunner.class);
	@Autowired
	private SysConfigProperties sysConfig;
	
	@Autowired
    private SysUserService sysUserService;

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		RedisBean bean = new RedisBean();
		bean.setJedisIp(sysConfig.getRedis_ip());
		bean.setJedisPort(Integer.parseInt(sysConfig.getRedis_port()));
		RedisAPI.setJedisBeanMap(RedisAPI.REDIS_CORE_DATABASE, bean);
		
		new Thread() {
            @Override
            public void run() {
            	try {
					sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            	SysUserEntity user = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
        		user.setLoginName("admin");
        		user.setType(1);
        		user.setUserName("超级管理员");
        		user.setPassword(MD5.encode("123456"));
        		user.setValid(1);
        		try{
        			sysUserService.save(user);
        		}catch(Exception e){
        			logger.error("新增"+user.getLoginName()+"用户出错", e);
        		}
            }
        }.start();
        
		
		
		
		logger.error("初始化结束");

	}

}
