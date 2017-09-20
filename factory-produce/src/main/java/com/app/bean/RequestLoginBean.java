/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-14
 */
package com.app.bean;

import org.springframework.context.annotation.Configuration;

/**
 * 功能说明：
 * @author chenwen 2017-8-14
 *
 */
@Configuration
public class RequestLoginBean
{
    private String name;
    private String pwd;
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getPwd()
    {
        return pwd;
    }
    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }
   
    
    
}
