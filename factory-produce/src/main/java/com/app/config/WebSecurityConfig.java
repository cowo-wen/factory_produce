/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.config;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.app.handler.LoginFailHandler;
import com.app.handler.LoginSuccessHandler;
import com.app.oauth.OAuthenticationProvider;

/**
 * Created by sang on 2017/1/10.
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    public static Log logger = LogFactory.getLog(WebSecurityConfig.class);

    @Autowired
    private OAuthenticationProvider provider;// 自定义验证

    @Autowired
    private UserDetailsService userDetailsService;// 自定义用户服务
    
    @Autowired
    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        // 将验证过程交给自定义验证工具
        logger.error("----------------调用自定义验证--------------------");
        //auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        // auth.userDetailsService(customUserService());
        logger.error("----------" + userDetailsService.getClass());
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(provider);
    }

    /*
     * http.authorizeRequests()
     * .antMatchers(StaticParams.PATHREGX.NOAUTH,
     * StaticParams.PATHREGX.CSS,StaticParams.PATHREGX.JS,StaticParams.PATHREGX.IMG).permitAll()//无需访问权限
     * .antMatchers(StaticParams.PATHREGX.AUTHADMIN).hasAuthority(StaticParams.USERROLE.ROLE_ADMIN)//admin角色访问权限
     * .antMatchers(StaticParams.PATHREGX.AUTHUSER).hasAuthority(StaticParams.USERROLE.ROLE_USER)//user角色访问权限
     * .anyRequest()//all others request authentication
     * .authenticated()
     * .and()
     * .formLogin().loginPage("/login").permitAll()
     * .and()
     * .logout().permitAll();
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        //AuthenticationManager authentication = null;
        logger.error("----------------===================--------------------");
        //http.authorizeRequests().anyRequest().authenticated().and().formLogin().loginPage("/login").failureUrl("/login?error").permitAll().and().logout().permitAll();

        http.authorizeRequests()
        // 例如以下代码指定了/other和/custom不需要任何认证就可以访问，其他的路径都必须通过身份验证。
                .antMatchers("/OAuth/**", "/allow2/**").permitAll().anyRequest().authenticated().and()
                // 通过formLogin()定义当需要用户登录时候，转到的登录页面。
                .formLogin().loginPage("/login").successHandler(loginSuccessHandler()).failureHandler(loginFailHandler()).authenticationDetailsSource(authenticationDetailsSource).permitAll()
                .and()
                // 注销
                .logout().permitAll();
        // 关闭csrf 防止循环定向
        http.csrf().disable();
    }
    
    @Bean  
    public LoginSuccessHandler loginSuccessHandler(){  
        return new LoginSuccessHandler();  
    }
    
    @Bean  
    public LoginFailHandler loginFailHandler(){  
        return new LoginFailHandler();  
    } 

}
