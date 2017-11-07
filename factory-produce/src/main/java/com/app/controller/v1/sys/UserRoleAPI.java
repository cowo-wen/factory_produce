package com.app.controller.v1.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.app.entity.sys.SysRoleEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.entity.sys.SysUserRoleEntity;
import com.app.util.PublicMethod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：用户角色关联表
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/user_role")
public class UserRoleAPI extends Result{
    public static Log logger = LogFactory.getLog(UserRoleAPI.class);
    
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list_user/{id}")
    public String listByRole(@PathVariable("id") Long id) throws Exception{
    	
    	
    	if(id == null || id < 0){
    		return error("无效的角色id");
    	}
    	
    	SysUserEntity user = new SysUserEntity();
    	user.setUserId(id).loadVo();
    	if(user.getType() == 0){
    		return error("不存在的用户");
    	}else if(user.getLoginName().equals(SysRoleEntity.ADMIN_CODE)){
    		return error("超级管理员不用分配权限");
    	}
    	SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    	SysUserEntity userLogin = new SysUserEntity();
    	userLogin.setUserId(userDetails.getUserId()).loadVo();//查询登录的用户信息
    	String sql ="";
    	if(userLogin.getType() == SysUserEntity.USER_ADMIN){//管理员的角色，可以获取所有的角色
    		
    		sql ="select r.role_id,r.parent_id,role_name ,case when ur.role_id > 0 then 'true' else 'false' end as checked,'true' as open from t_sys_role r  left join t_sys_user_role ur on r.role_id = ur.role_id and ur.user_id ="+id+" where valid = 1 ";
    		
    		
    	}else{
    		sql ="select r.role_id,r.parent_id,role_name ,case when ur.role_id > 0 then 'true' else 'false' end as checked,'true' as open from t_sys_role r  left join t_sys_user_role ur on r.role_id = ur.role_id and ur.user_id ="+id+" where valid = 1 and r.role_id in (select role_id from t_sys_user_role where user_id ="+userDetails.getUserId()+"  )";
    		
    		
    	}
    	logger.error("sql="+sql);
    	List<Map<String, Object>> list = user.getListMap(sql);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
        return success(map);
    }
   
    
    @RequestMapping(method = { RequestMethod.POST },value="/append")
    public String append(@RequestParam String aoData) throws Exception{
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	long userId = jo.get("user_id").getAsLong();
    	String roleids = "";
    	if(jo.has("role_ids")){
    		roleids = jo.get("role_ids").getAsString();
    	}
    	
    	
    	
    	SysUserEntity user = new SysUserEntity();
    	user.setUserId(userId).loadVo();
    	if(PublicMethod.isEmptyStr(user.getType() == 0)){
    		return error("用户不存在");
    	}else{
    		if(user.getLoginName().equals("admin")){
    			return error("超级管理员不用添加角色");
    		}
    	}
    	List<Long> listRoleIds = new ArrayList<Long>();
    	if(!PublicMethod.isEmptyStr(roleids)){
    		String[] ids = roleids.split(",");
    		for(int i=0,len =ids.length ;i<len;i++){
    			try{
    				listRoleIds.add(Long.parseLong(ids[i]));
    			}catch(Exception e){
    				logger.error("输换数据类型错误",e);
    			}
    		}
    		ids = null;
    	}
    	
    	
    	
    	List<SysUserRoleEntity> listRA = new SysUserRoleEntity().getListVO(new SQLWhere(new EQCnd("user_id", userId)));
    	for(SysUserRoleEntity userRole : listRA){
    		if(listRoleIds.indexOf(userRole.getRoleId()) == -1){
    			userRole.delete();
    		}else{
    			listRoleIds.remove(userRole.getRoleId());
    		}
    	}
    	
    	for(Long roleId : listRoleIds){
    		SysRoleEntity role = new SysRoleEntity();
    		role.setRoleId(roleId).loadVo();
    		
    		if(PublicMethod.isEmptyStr(role.getRoleCode()) || role.getRoleCode().equals(SysRoleEntity.ADMIN_CODE))
    			continue;
    		SysUserRoleEntity userRole = new SysUserRoleEntity();
    		userRole.setRoleId(roleId);
    		userRole.setUserId(userId);
    		userRole.insert();
    	}
    	
        return success("分配成功");
    }
    
   
    
    
    
}
