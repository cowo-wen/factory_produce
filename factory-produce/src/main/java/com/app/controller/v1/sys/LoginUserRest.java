package com.app.controller.v1.sys;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.bean.SysUserDetails;
import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.entity.sys.SysApplicationEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.util.RedisAPI;

/**
 * 功能说明：用户的信息接口
 * 
 * @author chenwen 2017-10-10
 */
@RestController
@RequestMapping("/v1/sys/loginuser")
public class LoginUserRest extends Result{
    public static Log logger = LogFactory.getLog(LoginUserRest.class);
    
  
    @Autowired  
    private HttpSession session;
    
   
    /**
     * 获取登录的用户信息
     * @return
     * @throws Exception
     */
    @RequestMapping(method=RequestMethod.GET,value="/logininfo")
    public String loginInfo(@RequestParam String terminalType) throws Exception{
    	RedisAPI redisAPI = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
    	Set<String> set = redisAPI.keys(session.getId()+":applicationCode:*");
    	if(set != null && set.size() > 0){
    		Iterator<String> iterable = set.iterator();
    		while(iterable.hasNext()){
    			redisAPI.del(iterable.next());
    		}
    	}
    	List<SysApplicationEntity> list = null;
    	if(terminalType.equals("1")){
    		list = new SysApplicationEntity().getListVO(new SQLWhere(new EQCnd("terminal_type",Integer.parseInt(terminalType))));
    	}
    	
    	if(list != null && list.size() > 0){
    		for(SysApplicationEntity entity : list){
    			try{
    				redisAPI.putOneDay(session.getId()+":applicationCode:"+entity.getApplicationCode(), entity.getUrl());//保存用户信息 保存一天
    			}catch(Exception e){
    				logger.error("获取应用", e);
    			}
    			
    		}
    	}
    	
    	SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    	SysUserEntity user = new SysUserEntity();
    	user.setUserId(userDetails.getUserId());
    	user.loadVo();
    	
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("user_name", user.getUserName());
    	map.put("login_name", user.getLoginName());
    	map.put("number", user.getNumber());
    	map.put("user_id", user.getUserId());
    	map.put("application", list);
    	map.put("url", "/index.html");
    	
    	String result = success(map);
    	redisAPI.putOneDay(session.getId()+":userinfo", result);//保存用户信息
        return result;
    }
    
    
}
