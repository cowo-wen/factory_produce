/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-15
 */
package com.app.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.app.config.SysConfigProperties;
import com.app.entity.common.CacheVo;
import com.app.entity.sys.SysLogEntity;
import com.app.service.sys.SysLogRepository;
import com.app.util.RedisAPI;

/**
 * 功能说明：
 * @author chenwen 2017-8-15
 *
 */
public class LoginFailHandler implements  AuthenticationFailureHandler { 
	
	public static Log logger = LogFactory.getLog(LoginFailHandler.class);
	@Autowired
    private SysLogRepository sysLogRepository;
	
	@Autowired
    private SysConfigProperties sysConfig;
      
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
	        SysLogEntity sysLogEntity = new SysLogEntity(RedisAPI.REDIS_CORE_DATABASE);
	        
	        sysLogEntity.setUserId(0L);
	        sysLogEntity.setApplicationId(1L);
	        sysLogEntity.setIp(getIpAddress(request));
	        sysLogEntity.setType(SysLogEntity.TYPE_FAIL);
	        //System.out.println(exception.getLocalizedMessage());
	        //exception.printStackTrace();
	        sysLogEntity.setMessage(request.getParameter("user_name")+exception.getMessage());
	        sysLogRepository.save(sysLogEntity);
	        sysLogEntity.setLogId(3L);
	        sysLogEntity.loadVo();
	        logger.error("----aaa"+sysLogEntity.toString());
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put("field_1", "application_id");
	        map.put("field_2", "data_id");
	        map.put("value_1", "11");
	        map.put("value_2", "101");
	        @SuppressWarnings("unchecked")
			List<SysLogEntity> list= (List<SysLogEntity>) sysLogEntity.getCustomCache(map,"type");
	        for(SysLogEntity log : list){
	        	logger.error("-------"+log.getLogId()+"   -------    "+log.getMessage());
	        }
	        logger.error("-------"+sysLogEntity.toString());
	        response.sendRedirect("/login.html?type=1");
		
	}    
}  
