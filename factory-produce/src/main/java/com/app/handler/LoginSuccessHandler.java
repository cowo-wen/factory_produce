/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-15
 */
package com.app.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.app.entity.sys.SysLogEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;

/**
 * 功能说明：
 * @author chenwen 2017-8-15
 *
 */
public class LoginSuccessHandler extends  SavedRequestAwareAuthenticationSuccessHandler { 
	
	
	
    @Override    
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {    
        //获得授权后可得到用户信息   可使用SUserService进行数据库操作  
        SysUserEntity userDetails = (SysUserEntity)authentication.getPrincipal();    
        SysLogEntity sysLogEntity = new SysLogEntity(null);
        sysLogEntity.setUserId(userDetails.getUserId());
        sysLogEntity.setApplicationCode("100");
        //sysLogEntity.setApplicationId(1L);
        sysLogEntity.setIp(getIpAddress(request));
        sysLogEntity.setType(SysLogEntity.TYPE_SUCCESS);
        if(PublicMethod.isEmptyStr(request.getParameter("code"))){
        	sysLogEntity.setMessage(userDetails.getUserName()+"于"+PublicMethod.formatDateStr("yyyy-MM-dd HH:mm:ss") + " 登录成功");
            
        }else{
        	sysLogEntity.setMessage(userDetails.getUserName()+"于"+PublicMethod.formatDateStr("yyyy-MM-dd HH:mm:ss") + " 微信登录成功");
            
        }
        try {
			sysLogEntity.insert();
		} catch (Exception e) {
			logger.error("保存日志出错", e);
		}
        //sysLogRepository.save(sysLogEntity);
                
        super.onAuthenticationSuccess(request, response, authentication);    
    }    
      
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
}  
