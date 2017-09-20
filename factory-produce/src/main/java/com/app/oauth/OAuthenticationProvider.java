/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-11
 */
package com.app.oauth;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.app.bean.SysUserDetails;
import com.app.config.CustomWebAuthenticationDetails;
import com.app.exception.LoginAccountStatusException;
import com.app.service.SysUserDetailsService;

/**
 * 功能说明：
 * @author chenwen 2017-8-11
 * http://blog.csdn.net/luguling200802544/article/details/46438241
 */
@Component
public class OAuthenticationProvider implements AuthenticationProvider {
    public static Log logger = LogFactory.getLog(OAuthenticationProvider.class);
    @Autowired
    private SysUserDetailsService userService;

    /**
     * 自定义验证方式
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
        logger.error("----------自定义认证------------"+details.getToken());
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        
        SysUserDetails user = (SysUserDetails) userService.loadUserByUsername(username);
        if(user == null){
            throw new LoginAccountStatusException("不存在的用户名");
        }

        //加密过程在这里体现
        if (!password.equals(user.getPassword())) {
            throw new LoginAccountStatusException("密码不正确");
        }

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }

}
