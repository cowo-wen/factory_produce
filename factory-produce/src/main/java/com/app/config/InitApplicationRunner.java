package com.app.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.app.util.RedisAPI;
import com.app.util.RedisBean;

@Component
public class InitApplicationRunner implements ApplicationRunner {
	
	public static Log logger = LogFactory.getLog(InitApplicationRunner.class);
	@Autowired
	private SysConfigProperties sysConfig;
	

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		RedisBean bean = new RedisBean();
		bean.setJedisIp(sysConfig.getRedis_ip());
		bean.setJedisPort(Integer.parseInt(sysConfig.getRedis_port()));
		RedisAPI.setJedisBeanMap(RedisAPI.REDIS_CORE_DATABASE, bean);
		
		logger.error("初始化结束");

	}

}