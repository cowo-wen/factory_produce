/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-11
 */
package com.app.oauth;

import java.util.Collection;

import javax.servlet.http.HttpSession;

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
import com.app.service.sys.SysUserDetailsService;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.xx.util.string.MD5;

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

    @Autowired  
    private HttpSession session;
    /**
     * 自定义验证方式
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
        String token = details.getToken();
        if(PublicMethod.isEmptyStr(token)){
        	throw new LoginAccountStatusException("验证码不能为空");
        }
        String checkToken = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE).get("identifyingcode:login:"+session.getId());
        if(PublicMethod.isEmptyStr(checkToken)){
        	throw new LoginAccountStatusException("验证码已失效");
        }
        
        if(!token.equals(checkToken)){
        	throw new LoginAccountStatusException("验证码不正确");
        }
        
        String username = authentication.getName();
        logger.error("|||"+username);
        if(PublicMethod.isEmptyStr(username)){
        	throw new LoginAccountStatusException("用户名不能为空");
        }
        
        String password = (String) authentication.getCredentials();
        
        if(PublicMethod.isEmptyStr(password)){
        	throw new LoginAccountStatusException("密码不能为空");
        }
        
        SysUserDetails user = (SysUserDetails) userService.loadUserByUsername(username);
        if(user == null || user.getUserId() == null || user.getUserId() == 0){
            throw new LoginAccountStatusException("不存在的用户名");
        }

        //加密过程在这里体现
        if (!MD5.encode(password).equals(user.getPassword())) {
            throw new LoginAccountStatusException("密码不正确");
        }
        if(user.getValid() == null || user.getValid() != 1){
        	throw new LoginAccountStatusException("用户已被锁，请联系管理员");
        }
        

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        if(user.getType() != 1 && (authorities == null || authorities.size() == 0)){
        	throw new LoginAccountStatusException("用户未分配角色权限，不能登录");
        }
        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }
    
    

}
