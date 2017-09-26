/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-16
 */
package com.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;



/**
 * 功能说明：
 * @author chenwen 2017-8-16
 *
 */
@Component
@PropertySource("classpath:sys_config.properties")
@ConfigurationProperties(prefix="redis_core_database")
public class SysConfigProperties
{
    private String redis_ip;
    
    private String redis_port;

	public String getRedis_ip() {
		return redis_ip;
	}

	public void setRedis_ip(String redis_ip) {
		this.redis_ip = redis_ip;
	}

	public String getRedis_port() {
		return redis_port;
	}

	public void setRedis_port(String redis_port) {
		this.redis_port = redis_port;
	}

	

	
	
    

    
    
    
}
