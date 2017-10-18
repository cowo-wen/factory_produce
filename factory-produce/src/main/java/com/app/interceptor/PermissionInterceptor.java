/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 功能说明：权限拦截
 * 
 * @author chenwen 2017-7-13
 */

public class PermissionInterceptor implements HandlerInterceptor
{
    public static Log logger = LogFactory.getLog(PermissionInterceptor.class);

    /**
     * 在整个请求结束之后被调用 DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception exc) throws Exception
    {
        //request.getCookies();
        //logger.error("afterCompletion-----权限拦截-------拦截器获取:" + request.getSession().getId());
    }

    /**
     * 请求处理之后进行调用（Controller方法调用之后）
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView mav) throws Exception
    {
        //logger.error("postHandle------权限拦截------拦截器获取:" + request.getSession().getId());

    }

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception
    {
    	logger.error(request.getSession().getId()+"------权限拦截-------url="+request.getRequestURI());
    	boolean bool = checkURL(request.getRequestURI(),request.getRequestURI());
        logger.error(request.getSession().getId()+"------权限拦截-------application_code="+request.getParameter("application_code"));
        if(bool){
        	return true;
        }
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print("没有权限");  
        return false;
    }
    
    /**
     * 验证两个url是否一致
     * @param url 请求的url
     * @param reference 权限的url
     * @return
     */
    private boolean checkURL(String url,String reference){
    	int size =url.indexOf("?");
    	if(size > 0){
    		url = url.substring(0,size);
    	}
    	
    	String [] s = reference.split("\\{\\w+\\}");
    	if(!url.startsWith(s[0])){
    		return false;
    	}
    	for(int i = 1,len = s.length;i<len;i++){
    		url = url.substring(s[i-1].length(), url.length());
    		int index = url.indexOf("/");
    		url=url.substring(index,url.length());
    		if(!url.startsWith(s[i])){
    			return false;
    		}
    	}
    	return true;
    }

}
