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

import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    	
    	long time = System.currentTimeMillis();
        try{
        	String value = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE).get(request.getSession().getId()+":userinfo");
            if(PublicMethod.isEmptyStr(value)){
            	request.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().print("非法请求");  
            }else{
            	JsonObject jsonObject= new JsonParser().parse(value).getAsJsonObject();
            	if(jsonObject.get("login_name").getAsString().equals("admin")){
            		return true;
            	}else{
            		String applicationCode = request.getParameter("application_code");
            		if(PublicMethod.isEmptyStr(applicationCode)){
            			request.setCharacterEncoding("UTF-8");
                        response.setContentType("text/html;charset=utf-8");
                        response.getWriter().print("参数不全");
            		}else{
            			String url = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE).get(request.getSession().getId()+":applicationCode:"+applicationCode);
            			if(PublicMethod.isEmptyStr(url)){
            				request.setCharacterEncoding("UTF-8");
                            response.setContentType("text/html;charset=utf-8");
                            response.getWriter().print("没有权限操作!");
            			}else{
            				boolean bool = checkURL(request.getRequestURI(),url);
            				if(bool){
            					return true;
            				}else{
            					request.setCharacterEncoding("UTF-8");
                                response.setContentType("text/html;charset=utf-8");
                                response.getWriter().print("没有权限操作.");
            				}
            			}
            			
            		}
                }
            }
            return false;
        }catch(Exception e){
        	logger.error("验证权限", e);
        	throw e;
        }finally{
        	logger.error("验证权限耗时----------time="+(System.currentTimeMillis()-time)+"毫秒");
        }
        
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
