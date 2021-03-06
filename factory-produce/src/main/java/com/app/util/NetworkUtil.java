/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 功能说明：常用获取客户端信息的工具 
 * @author chenwen 2017-8-11
 *
 */
public class NetworkUtil
{
    /** 
     * Logger for this class 
     */  
    public static Log logger = LogFactory.getLog(NetworkUtil.class);
  
    public synchronized final static String getIpAddress(HttpServletRequest request){
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
        //return ip.equals("0:0:0:0:0:0:0:1")? "127.0.0.1":ip;
        return ip;
    }
    
   
    
    /** 
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址; 
     *  
     * @param request 
     * @return 
     * @throws IOException 
     */  
    public final static String getIpAddress2(HttpServletRequest request) throws IOException {  
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址  
  
        String ip = request.getHeader("X-Forwarded-For");  
        if (logger.isInfoEnabled()) {  
            logger.error("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);  
        }  
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("Proxy-Client-IP");  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);  
                }  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("WL-Proxy-Client-IP");  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);  
                }  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_CLIENT_IP");  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);  
                }  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);  
                }  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getRemoteAddr();  
                if (logger.isInfoEnabled()) {  
                    logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);  
                }  
            }  
        } else if (ip.length() > 15) { 
            String[] ips = ip.split(",");  
            for (int index = 0,len = ips.length; index < len; index++) {  
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {  
                    ip = strIp;  
                    break;  
                }  
            }
        } 
        
        logger.error("X-Forwarded-For="+request.getHeader("X-Forwarded-For"));
        logger.error("Proxy-Client-IP="+request.getHeader("Proxy-Client-IP"));
        logger.error("WL-Proxy-Client-IP="+request.getHeader("WL-Proxy-Client-IP"));
        logger.error("HTTP_CLIENT_IP="+request.getHeader("HTTP_CLIENT_IP"));
        logger.error("HTTP_X_FORWARDED_FOR="+request.getHeader("HTTP_X_FORWARDED_FOR"));
        logger.error("RemoteAddr="+request.getRemoteAddr());
        return ip;  
    }  
}
