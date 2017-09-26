/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2016-12-25
 */
package com.app.util;

/**
 * 功能说明：缓存端口与ip信息
 * 
 * @author chenwen 2014-12-25
 */
public class RedisBean
{
    private String jedisIp;

    private Integer jedisPort;

    public String getJedisIp()
    {
        return jedisIp;
    }

    public void setJedisIp(String jedisIp)
    {
        this.jedisIp = jedisIp;
    }

    public Integer getJedisPort()
    {
        return jedisPort;
    }

    public void setJedisPort(Integer jedisPort)
    {
        this.jedisPort = jedisPort;
    }

}
