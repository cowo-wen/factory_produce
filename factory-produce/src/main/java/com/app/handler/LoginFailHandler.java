/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-15
 */
package com.app.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.app.entity.sys.SysLogEntity;
import com.app.service.sys.SysLogRepository;

/**
 * 功能说明：
 * @author chenwen 2017-8-15
 *
 */
public class LoginFailHandler implements  AuthenticationFailureHandler { 
	
	
	@Autowired
    private SysLogRepository sysLogRepository;
	
   
      
    public String getIpAddress(HttpServletRequest request){      
        String ip = request.getHeader("x-forwarded-for");      
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
            ip = request.getHeader("Proxy-Client-IP");      
        }      
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
            ip = request.getHeader("WL-Proxy-Client-IP");      
        }      
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
            ip = request.getHeader("HTTP_CLIENT_IP");      
        }      
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");      
        }      
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
            ip = request.getRemoteAddr();      
        }      
        return ip;      
    }

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,HttpServletResponse response,AuthenticationException exception)throws IOException, ServletException {   
	        SysLogEntity sysLogEntity = new SysLogEntity();
	        
	        sysLogEntity.setUserId(0L);
	        sysLogEntity.setApplicationId(1L);
	        sysLogEntity.setIp(getIpAddress(request));
	        sysLogEntity.setType(SysLogEntity.TYPE_FAIL);
	        //System.out.println(exception.getLocalizedMessage());
	        //exception.printStackTrace();
	        sysLogEntity.setMessage(request.getParameter("user_name")+exception.getMessage());
	        
	        
	        System.out.println(exception.getMessage());
	        sysLogRepository.save(sysLogEntity);
	        response.sendRedirect("/login.html?type=1");
		
	}    
}  
