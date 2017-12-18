package com.app.controller.v1.sys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：用户的信息接口
 * 
 * @author chenwen 2017-10-10
 */
@RestController
@RequestMapping("/v1/sys/loginuser")
@Scope("prototype")//设置成多例
public class LoginUserAPI extends Result{
    public static Log logger = LogFactory.getLog(LoginUserAPI.class);
    
  
    @Autowired  
    private HttpSession session;
    
   
    /**
     * 获取登录的用户信息
     * @return
     * @
     */
    @RequestMapping(method=RequestMethod.GET,value="/logininfo")
    public String loginInfo(@RequestParam String terminalType) {
    	RedisAPI redisAPI = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
    	Map<String,String> currentRole = redisAPI.hgetAll(session.getId()+":role:current:select");
    	String result = redisAPI.get("temp:"+session.getId()+":userinfo");
    	if(PublicMethod.isEmptyStr(result) || currentRole == null || currentRole.size() == 0 || (System.currentTimeMillis() - Long.parseLong(currentRole.get("time")) > 1000*60*60*16)){
    		long time = System.currentTimeMillis();
        	String appCodeKey = "temp:"+session.getId()+":application_code";
        	redisAPI.del(appCodeKey);
        	
        	logger.error("==========1=================消耗时间："+(System.currentTimeMillis()-time));
        	SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        	SysUserEntity user = new SysUserEntity(jdbcDao);
        	user.setUserId(userDetails.getUserId());
        	user.loadVo();
        	logger.error("==========2=================消耗时间："+(System.currentTimeMillis()-time));
        	int type = Integer.parseInt(terminalType);
        	List<SysRoleEntity> roleList = new ArrayList<SysRoleEntity>();
        	
        	List<SysApplicationEntity> list = null;
        	String roleId = "0";
    		if(user.getType() == SysUserEntity.USER_ADMIN){
    			SysRoleEntity role = new SysRoleEntity(jdbcDao);
    			role = role.setRoleCode(SysRoleEntity.ADMIN_CODE).queryCustomCacheVo();
    			currentRole = new HashMap<String,String>();
    			currentRole.put("role", String.valueOf(role.getRoleId()));
    			currentRole.put("name", "超级管理员");
    			currentRole.put("time", String.valueOf(System.currentTimeMillis()));
    			currentRole.put("pc_index", role.getPcIndex());
    			currentRole.put("wc_index", role.getWxIndex());
    			String key = session.getId()+":role:current:select";
    			redisAPI.hSet(key,new String[]{"role","time","pc_index","wc_index","name"},new String[]{currentRole.get("role"),currentRole.get("time"),currentRole.get("pc_index"),currentRole.get("wc_index"),currentRole.get("name")});
    			redisAPI.expire(key, 86400);//保存一日
    			list = new SysApplicationEntity(jdbcDao).getListVO(new SQLWhere(new EQCnd(SysApplicationEntity.TERMINAL_TYPE,type)).and(new EQCnd(SysApplicationEntity.VALID, StaticBean.YES)).orderBy(new AscSort(SysApplicationEntity.SORT_CODE,SysApplicationEntity.APPLICATION_ID)));
    		}else{
    			currentRole = redisAPI.hgetAll(session.getId()+":role:current:select");
    	    	List<SysUserRoleEntity> roles = new SysUserRoleEntity(jdbcDao).setUserId(user.getUserId()).queryCustomCacheValue(0, null);
    	    	if(roles != null){
    	    		for(SysUserRoleEntity ur : roles){
    	    			SysRoleEntity r = new SysRoleEntity(jdbcDao);
    	    			r.setRoleId(ur.getRoleId()).loadVo();
    	    			if(r.getValid() == StaticBean.YES){
    	    				roleList.add(r);
    	    				roleId +=","+r.getRoleId();
    	    			}
    	    		}
    	    	}
    	    	logger.error("==========3=================消耗时间："+(System.currentTimeMillis()-time));
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
    	    	logger.error("==========4=================消耗时间："+(System.currentTimeMillis()-time));
    	    	list = new ArrayList<SysApplicationEntity>(); //获取角色应用
    	    	if(currentRole != null && currentRole.size() > 0){
    	    		if(type == 1){
    	    			roleId = currentRole.get("role");
    	    		}
    	    		List<Map<String, Object>> listAPP = new SysRoleApplicationEntity(jdbcDao).getListMap("select * from t_sys_application where application_id in (select application_id from t_sys_role_application where role_id in ( "+roleId+") ) and terminal_type = "+terminalType);
    	    		for(Map<String, Object> map : listAPP){
    	    			SysApplicationEntity a = new SysApplicationEntity(jdbcDao);
    	    			a.setApplicationId(Long.parseLong(map.get(SysApplicationEntity.APPLICATION_ID).toString()));
    	    			a.setParentId(Long.parseLong(map.get(SysApplicationEntity.PARENT_ID).toString()));
    	    			a.setApplicationCode(map.get(SysApplicationEntity.APPLICATION_CODE).toString());
    	    			a.setAppType(Integer.parseInt(map.get(SysApplicationEntity.APP_TYPE).toString()));
    	    			a.setEventType(Integer.parseInt(map.get(SysApplicationEntity.EVENT_TYPE).toString()));
    	    			a.setIconCode(map.get(SysApplicationEntity.ICON_CODE)+"");
    	    			a.setName(map.get(SysApplicationEntity.NAME).toString());
    	    			a.setOutCode(map.get(SysApplicationEntity.OUT_CODE).toString());
    	    			a.setParentApplicationCode(map.get(SysApplicationEntity.PARENT_APPLICATION_CODE).toString());
    	    			a.setSortCode(Integer.parseInt(map.get(SysApplicationEntity.SORT_CODE).toString()));
    	    			a.setTerminalType(Integer.parseInt(map.get(SysApplicationEntity.TERMINAL_TYPE).toString()));
    	    			a.setUrl(map.get(SysApplicationEntity.URL)+"");
    	    			a.setValid(Integer.parseInt(map.get(SysApplicationEntity.VALID).toString()));
    	    			list.add(a);
    	    		}
    	    	}
    	    	logger.error("==========5=================消耗时间："+(System.currentTimeMillis()-time));
    		}
    		
        	if(list != null && list.size() > 0){
        		for(SysApplicationEntity entity : list){
        			try{
        				redisAPI.hSet(appCodeKey, entity.getApplicationCode(), entity.getUrl());
        			}catch(Exception e){
        				logger.error("获取应用", e);
        			}
        		}
        		redisAPI.expire(appCodeKey, 86400);//设置一天有效
        	}
        	logger.error("==========6=================消耗时间："+(System.currentTimeMillis()-time));
        	Collections.sort(list);
        	logger.error("==========7=================消耗时间："+(System.currentTimeMillis()-time));
        	Map<String,Object> map = new HashMap<String,Object>();
        	map.put("user_name", user.getUserName());
        	map.put("login_name", user.getLoginName());
        	map.put("number", user.getNumber());
        	map.put("user_id", user.getUserId());
        	map.put("application", list);
        	map.put("role_list", roleList);
        	map.put("current_role", currentRole);
        	map.put("url", "/index.html");
        	
        	logger.error("==========8=================消耗时间："+(System.currentTimeMillis()-time));
        	result = success(map);
        	logger.error("==========9=================消耗时间："+(System.currentTimeMillis()-time));
        	redisAPI.putOneDay("temp:"+session.getId()+":userinfo", result);//保存用户信息
    	}
    	
        return result;
    }
    
    /**
     * 获取登录的用户信息
     * @return
     * @
     */
    @RequestMapping(method=RequestMethod.GET,value="/children_app/{appcode}")
    public String childrenApp(@PathVariable("appcode") String appcode){
    	RedisAPI redisAPI = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
    	String childrenAppKey = "temp:"+session.getId()+":children_app:"+appcode;
    	List<SysApplicationEntity> list = new ArrayList<SysApplicationEntity>();
    	if(redisAPI.exists(childrenAppKey)){//判断是否存在子类的app列表
    		return redisAPI.get(childrenAppKey);
    	}else{
    		String userinfo = redisAPI.get("temp:"+session.getId()+":userinfo");//保存用户信息
    		if(!PublicMethod.isEmptyStr(userinfo)){
    			JsonObject jo = new JsonParser().parse(userinfo).getAsJsonObject();
    			JsonArray ja = jo.get("application").getAsJsonArray();
    			if(ja.size() > 0){
    				for(JsonElement je : ja){
        				SysApplicationEntity entity = new SysApplicationEntity(jdbcDao);
        				entity.parse(je.getAsJsonObject(),1);
        				if(entity.getParentApplicationCode().equals(appcode)){
        					list.add(entity);
        				}
        			}
    			}
    			String result = success(list);
    			redisAPI.putTowHours(childrenAppKey, result);
    			return result;
    		}else{
    			return success("");
    		}
    	}
    }
    
  

    
    /**
     * 切换用户登录角色
     * @return
     * @
     */
    @RequestMapping(method=RequestMethod.PUT,value="/role/{id}")
    public String role(@PathVariable("id") Long id) {
    	SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    	String key = session.getId()+":role:current:select";
    	RedisAPI redisAPI = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
    	if(id != null && id > 0){
    		SysUserRoleEntity userRole = new SysUserRoleEntity(jdbcDao).setUserId(userDetails.getUserId()).queryCustomCacheVo(0, id.toString());
    		if(userRole != null &&  !PublicMethod.isEmptyValue(userRole.getId()) ){
    			SysRoleEntity role = new  SysRoleEntity(jdbcDao);
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
