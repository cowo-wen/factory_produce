/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.config;

/**
 * 功能说明：
 * 
 * @author chenwen 2017-7-13
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.app.interceptor.LoginInterceptor;


@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter
{
    public static Log logger = LogFactory.getLog(WebConfiguration.class);

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        //registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
        //registry.addInterceptor(new MyInterceptor2()).addPathPatterns("/**");
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/v1/**", "/security2/**");
        //.excludePathPatterns("/allow1/**", "/allow2/**");
        
        super.addInterceptors(registry);
    }
    
    
}
