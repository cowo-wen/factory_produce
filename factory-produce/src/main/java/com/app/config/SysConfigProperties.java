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
@ConfigurationProperties(prefix="")
public class SysConfigProperties
{
    private String redis_connection_source;
    

    public String getRedis_connection_source()
    {
        return redis_connection_source;
    }

    public void setRedis_connection_source(String redis_connection_source)
    {
        this.redis_connection_source = redis_connection_source;
    }
    
    
    
}
