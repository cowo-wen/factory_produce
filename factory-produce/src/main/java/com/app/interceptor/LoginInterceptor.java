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

import com.app.util.NetworkUtil;

/**
 * 功能说明：登录拦截
 * 
 * @author chenwen 2017-7-13
 */

public class LoginInterceptor implements HandlerInterceptor
{
    public static Log logger = LogFactory.getLog(LoginInterceptor.class);

    /**
     * 在整个请求结束之后被调用 DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception exc) throws Exception
    {
        request.getCookies();
        logger.error("afterCompletion-----权限拦截-------拦截器获取:" + request.getSession().getId());
    }

    /**
     * 请求处理之后进行调用（Controller方法调用之后）
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView mav) throws Exception
    {
        logger.error("postHandle------权限拦截------拦截器获取:" + request.getSession().getId());

    }

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception
    {
        logger.error("preHandle-----权限拦截-------拦截器获取:" + request.getSession().getId());
        logger.error("-------------登录ip"+NetworkUtil.getIpAddress2(request));
        return true;
    }

}
