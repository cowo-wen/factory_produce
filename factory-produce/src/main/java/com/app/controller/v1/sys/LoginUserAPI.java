package com.app.controller.v1.sys;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.bean.SysUserDetails;
import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.sort.AscSort;
import com.app.entity.sys.SysApplicationEntity;
import com.app.entity.sys.SysRoleApplicationEntity;
import com.app.entity.sys.SysRoleEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.entity.sys.SysUserRoleEntity;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.app.util.StaticBean;

/**
 * 功能说明：用户的信息接口
 * 
 * @author chenwen 2017-10-10
 */
@RestController
@RequestMapping("/v1/sys/loginuser")
public class LoginUserAPI extends Result{
    public static Log logger = LogFactory.getLog(LoginUserAPI.class);
    
  
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
    	Set<String> set = redisAPI.keys("temp:"+session.getId()+":application_code:*");
    	if(set != null && set.size() > 0){
    		Iterator<String> iterable = set.iterator();
    		while(iterable.hasNext()){
    			redisAPI.del(iterable.next());
    		}
    	}
    	
    	
    	
    	SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    	SysUserEntity user = new SysUserEntity();
    	user.setUserId(userDetails.getUserId());
    	user.loadVo();
    	int type = Integer.parseInt(terminalType);
    	List<SysRoleEntity> roleList = new ArrayList<SysRoleEntity>();
    	Map<String,String> currentRole = null;
    	List<SysApplicationEntity> list = null;
		if(user.getType() == SysUserEntity.USER_ADMIN){
			SysRoleEntity role = new SysRoleEntity();
			role = role.setRoleCode(SysRoleEntity.ADMIN_CODE).queryCustomCacheVo();
			currentRole = new HashMap<String,String>();
			currentRole.put("role", String.valueOf(role.getRoleId()));
			currentRole.put("name", "超级管理员");
			currentRole.put("time", String.valueOf(System.currentTimeMillis()));
			currentRole.put("pc_index", role.getPcIndex());
			currentRole.put("wc_index", role.getWxIndex());
			list = new SysApplicationEntity().getListVO(new SQLWhere(new EQCnd(SysApplicationEntity.TERMINAL_TYPE,type)).and(new EQCnd(SysApplicationEntity.VALID, StaticBean.YES)).orderBy(new AscSort(SysApplicationEntity.SORT_CODE,SysApplicationEntity.APPLICATION_ID)));
		}else{
			currentRole = redisAPI.hgetAll(session.getId()+":role:current:select");
	    	List<SysUserRoleEntity> roles = new SysUserRoleEntity().setUserId(user.getUserId()).queryCustomCacheValue(0, null);
	    	if(roles != null){
	    		for(SysUserRoleEntity ur : roles){
	    			SysRoleEntity r = new SysRoleEntity();
	    			r.setRoleId(ur.getRoleId()).loadVo();
	    			if(r.getValid() == StaticBean.YES){
	    				roleList.add(r);
	    			}
	    		}
	    	}
	    	if(currentRole == null || currentRole.size() == 0){//获取角色
	    		if(roleList.size() > 0){
	    			currentRole = new HashMap<String,String>();
	    			String key = session.getId()+":role:current:select";
	    			currentRole.put("role", String.valueOf(roleList.get(0).getRoleId()));
	    			currentRole.put("name", roleList.get(0).getRoleName());
	    			currentRole.put("time", String.valueOf(System.currentTimeMillis()));
	    			currentRole.put("pc_index", roleList.get(0).getPcIndex());
    				currentRole.put("wc_index", roleList.get(0).getWxIndex());
	    			redisAPI.hSet(key,new String[]{"role","time","pc_index","wc_index","name"},new String[]{currentRole.get("role"),currentRole.get("time"),currentRole.get("pc_index"),currentRole.get("wc_index"),currentRole.get("name")});
	    			redisAPI.expire(key, 86400);//保存一日
	    		}
	    	}else{
	    		if(System.currentTimeMillis() - Long.parseLong(currentRole.get("time")) > 1000*60*60*16){//当保存的数据大于16小时即更新
	    			if(roleList.size() > 0){
	    				String key = session.getId()+":role:current:select";
		    			currentRole.put("role", String.valueOf(roleList.get(0).getRoleId()));
		    			currentRole.put("time", String.valueOf(System.currentTimeMillis()));
		    			currentRole.put("name", roleList.get(0).getRoleName());
		    			currentRole.put("pc_index", roleList.get(0).getPcIndex());
	    				currentRole.put("wc_index", roleList.get(0).getWxIndex());
	    				redisAPI.hSet(key,new String[]{"role","time","pc_index","wc_index","name"},new String[]{currentRole.get("role"),currentRole.get("time"),currentRole.get("pc_index"),currentRole.get("wc_index"),currentRole.get("name")});
		    			redisAPI.expire(key, 86400);//保存一日
	    			}
	    			
	    		}
	    	}
	    	
	    	list = new ArrayList<SysApplicationEntity>(); //获取角色应用
	    	if(currentRole != null && currentRole.size() > 0){
	    		List<SysRoleApplicationEntity> listRA = new SysRoleApplicationEntity().setRoleId(Long.parseLong(currentRole.get("role"))).queryCustomCacheValue(0,null);
	    		if(listRA != null){
	    			for(SysRoleApplicationEntity ra : listRA){
	    				SysApplicationEntity a = new SysApplicationEntity();
	    				a.setApplicationId(ra.getApplicationId()).loadVo();
	    				if(a.getTerminalType() == type && a.getValid() == StaticBean.YES){
	    					list.add(a);
	    				}
	    			}
	    		}
	    	}
		}
    	
    	if(list != null && list.size() > 0){
    		for(SysApplicationEntity entity : list){
    			try{
    				redisAPI.putOneDay("temp:"+session.getId()+":application_code:"+entity.getApplicationCode(), entity.getUrl());//保存用户信息 保存一天
    			}catch(Exception e){
    				logger.error("获取应用", e);
    			}
    			
    		}
    	}
    	
    	Collections.sort(list);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("user_name", user.getUserName());
    	map.put("login_name", user.getLoginName());
    	map.put("number", user.getNumber());
    	map.put("user_id", user.getUserId());
    	map.put("application", list);
    	map.put("role_list", roleList);
    	map.put("current_role", currentRole);
    	map.put("url", "/index.html");
    	
    	
    	
    	String result = success(map);
    	redisAPI.putOneDay("temp:"+session.getId()+":userinfo", result);//保存用户信息
        return result;
    }
    
    /**
     * 切换用户登录角色
     * @return
     * @throws Exception
     */
    @RequestMapping(method=RequestMethod.PUT,value="/role/{id}")
    public String role(@PathVariable("id") Long id) throws Exception{
    	SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    	String key = session.getId()+":role:current:select";
    	RedisAPI redisAPI = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
    	if(id != null && id > 0){
    		SysUserRoleEntity userRole = new SysUserRoleEntity().setUserId(userDetails.getUserId()).queryCustomCacheVo(0, id.toString());
    		if(userRole != null &&  !PublicMethod.isEmptyValue(userRole.getId()) ){
    			SysRoleEntity role = new  SysRoleEntity();
    			role.setRoleId(userRole.getRoleId()).loadVo();
    			redisAPI.hSet(key,new String[]{"role","time"},new String[]{String.valueOf(userRole.getRoleId()),String.valueOf(System.currentTimeMillis())});
    			redisAPI.hSet(key,new String[]{"role","time","pc_index","wc_index","name"},new String[]{String.valueOf(userRole.getRoleId()),String.valueOf(System.currentTimeMillis()),role.getPcIndex(),role.getWxIndex(),role.getRoleName()});
    			redisAPI.expire(key, 86400);//保存一日
    			return success("切换角色成功");
    		}else{
    			return error("非法的角色");
    		}
    	}else{
    		return error("角色不能为空");
    	}
		
    }
    
}
