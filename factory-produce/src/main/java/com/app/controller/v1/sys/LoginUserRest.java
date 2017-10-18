package com.app.controller.v1.sys;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.bean.SysUserDetails;
import com.google.gson.JsonObject;

/**
 * 功能说明：用户的信息接口
 * 
 * @author chenwen 2017-10-10
 */
@RestController
@RequestMapping("/v1/sys/loginuser")
public class LoginUserRest {
    public static Log logger = LogFactory.getLog(LoginUserRest.class);
    
  
    @Autowired  
    private HttpSession session;
    
   
    /**
     * 获取登录的用户信息
     * @return
     * @throws Exception
     */
    @RequestMapping(method=RequestMethod.GET,value="/logininfo")
    public String loginInfo() throws Exception{
    	
    	SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    	logger.error(userDetails.getUserId()+"----*************----"+userDetails.getUserName());
    	JsonObject jo = new JsonObject();
    	jo.addProperty("user_name", userDetails.getUserName());
    	jo.addProperty("number", userDetails.getNumber());
    	if(userDetails.getType() == 1){
    		jo.addProperty("url", "/index-2.html");
    	}else{
    		jo.addProperty("url", "/index.html");
    	}
    	
        return jo.toString();
    }
    
    
}
