/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-11
 */
package com.app.oauth;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;

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
import com.app.controller.oauth.IdentifyingCodeAPI;
import com.app.dao.JdbcDao;
import com.app.entity.sys.SysUserBindingInfo;
import com.app.entity.sys.SysUserEntity;
import com.app.exception.LoginAccountStatusException;
import com.app.service.sys.SysUserDetailsService;
import com.app.service.wechat.WeiXinServer;
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
        
        if(!PublicMethod.isEmptyStr(details.getCode())){//微信登录认证逻辑
        	JdbcDao jdbcDao = null;
        	SysUserEntity sysUserEntity = new SysUserEntity(jdbcDao);
			try {
				WxMpOAuth2AccessToken token = WeiXinServer.getWeChatWxMpService().oauth2getAccessToken(details.getCode());
				SysUserBindingInfo userBind = new SysUserBindingInfo(null);
	        	List<SysUserBindingInfo> list = userBind.setType(1).setOpenId(token.getOpenId()).queryCustomCacheValue(0);
				//List<SysUserBindingInfo> list = userBind.setType(1).setOpenId("oBt_pwwNntoknyvswR1wNSJuWM-0").queryCustomCacheValue(0);
	        	if(list != null && list.size() > 0){
	        		sysUserEntity.setUserId(list.get(0).getUserId()).loadVo();
	        	}else{
	        		throw new LoginAccountStatusException("不存在的用户");
	        	}
	        	if(sysUserEntity.getValid() == null || sysUserEntity.getValid() == 2){
	            	 throw new LoginAccountStatusException("用户已被禁用");
	             }else if(sysUserEntity.getValid() == 3){
	            	 throw new LoginAccountStatusException("不存在的用户");
	             }else if(sysUserEntity.getValid() != 1){
	            	 throw new LoginAccountStatusException("用户状态异常");
	             }
			} catch (Exception e) {
				 throw new LoginAccountStatusException(e.getMessage());
			}
        	SysUserDetails user = (SysUserDetails) userService.loadUserByUsername(sysUserEntity.getLoginName());
        	String password =new String(user.getPassword());
        	
        	if(user == null || user.getUserId() == null || user.getUserId() == 0){
                throw new LoginAccountStatusException("不存在的用户名");
            }else{
            	user.setPassword(MD5.encode(user.getPassword()));
            }
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            if(user.getType() != 1 && (authorities == null || authorities.size() == 0)){
            	throw new LoginAccountStatusException("用户未分配角色权限，不能登录");
            }
        	return new UsernamePasswordAuthenticationToken(user, password, authorities);
        }else{//pc登录逻辑
        	 String token = details.getToken();
             if(PublicMethod.isEmptyStr(token)){
             	throw new LoginAccountStatusException("验证码不能为空");
             }
             String checkToken = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE).get(IdentifyingCodeAPI.IDENTIFYINGCODE_LOGIN+session.getId());
             if(PublicMethod.isEmptyStr(checkToken)){
             	throw new LoginAccountStatusException("验证码已失效");
             }
             if(!token.equals(checkToken)){
             	throw new LoginAccountStatusException("验证码不正确");
             }
             String username = authentication.getName();
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
             if(user.getValid() == null || user.getValid() == 2){
            	 throw new LoginAccountStatusException("用户已被禁用");
             }else if(user.getValid() == 3){
            	 throw new LoginAccountStatusException("不存在的用户");
             }else if(user.getValid() != 1){
            	 throw new LoginAccountStatusException("用户状态异常");
             }
             Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
             if(user.getType() != 1 && (authorities == null || authorities.size() == 0)){
             	throw new LoginAccountStatusException("用户未分配角色权限，不能登录");
             }
             return new UsernamePasswordAuthenticationToken(user, password, authorities);
        }
        
       
    }

    public boolean supports(Class<?> arg0) {
        return true;
    }
    
    

}
