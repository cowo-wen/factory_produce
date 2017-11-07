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
import com.app.dao.sql.cnd.INCnd;
import com.app.entity.sys.SysApplicationEntity;
import com.app.entity.sys.SysRoleApplicationEntity;
import com.app.entity.sys.SysRoleEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：角色应用关联管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/role_application")
public class RoleApplicationAPI extends Result{
    public static Log logger = LogFactory.getLog(RoleApplicationAPI.class);
    
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list_role/{id}")
    public String listByRole(@PathVariable("id") Long id) throws Exception{
    	
    	
    	if(id == null || id < 0){
    		return error("无效的角色id");
    	}
    	
    	SysRoleEntity role = new SysRoleEntity();
    	role.setRoleId(id).loadVo();
    	if(PublicMethod.isEmptyStr(role.getRoleCode())){
    		return error("不存在的角色");
    	}else if(role.getRoleCode().equals(SysRoleEntity.ADMIN_CODE)){
    		return error("超级管理员不用分配权限");
    	}
    	SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    	SysUserEntity user = new SysUserEntity();
    	user.setUserId(userDetails.getUserId()).loadVo();//查询登录的用户信息
    	String sql ="";
    	if(user.getType() == SysUserEntity.USER_ADMIN){//管理员的角色，可以获取所有的应用
    		SysRoleEntity parent = new SysRoleEntity();
    		parent.setRoleId(role.getParentId()).loadVo();
    		if(PublicMethod.isEmptyStr(parent.getRoleCode())){
    			return error("不存在的父角色");
    		}else if(parent.getRoleCode().equals(SysRoleEntity.ADMIN_CODE)){//查询所有权限
    			sql = "select a.parent_id, a.application_id ,a.name,a.application_code,case when ra.application_id > 0 then 'true' else 'false' end as checked,'true' as open   from t_sys_application a left join t_sys_role_application ra  on a.application_id = ra.application_id and ra.role_id = "+role.getRoleId()+" where a.valid= 1";
    		}else{
    			sql ="select a.parent_id,a.application_id,a.name,a.application_code ,case when ra.application_id > 0 then 'true' else 'false' end as checked,'true' as open from t_sys_application a  left join t_sys_role_application ra  on a.application_id = ra.application_id and ra.role_id = "+role.getRoleId()+" where a.valid= 1 and a.application_id in ( select application_id from t_sys_role_application  where role_id = "+parent.getRoleId()+") ";
    		}
    	}else{
    		SysRoleEntity parent = new SysRoleEntity();
    		parent.setRoleId(role.getParentId()).loadVo();
    		if(PublicMethod.isEmptyStr(parent.getRoleCode())){
    			return error("不存在的父角色");
    		}else if(parent.getRoleCode().equals(SysRoleEntity.ADMIN_CODE)){//查询所有权限
    			sql = "select a.parent_id,a.application_id,a.name,a.application_code,case when ra.application_id > 0 then 'true' else 'false' end as checked,'true' as open   from t_sys_application a left join t_sys_role_application ra  on a.application_id = ra.application_id and ra.role_id = "+role.getRoleId()+" where a.valid= 1 and a.application_id in ( select application_id from t_sys_role_application  where  role_id in (select role_id from t_sys_user_role where user_id ="+userDetails.getUserId()+"))";
    		}else{
    			sql ="select a.parent_id,a.application_id,a.name,a.application_code ,case when ra.application_id > 0 then 'true' else 'false' end as checked,'true' as open from t_sys_application a  left join t_sys_role_application ra  on a.application_id = ra.application_id and ra.role_id = "+role.getRoleId()+" where a.valid= 1 and a.application_id in ( select application_id from t_sys_role_application  where role_id = "+parent.getRoleId()+" and role_id in (select role_id from t_sys_user_role where user_id ="+userDetails.getUserId()+")) ";
    		}
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
    	long roleId = jo.get("role_id").getAsLong();
    	String appids = "";
    	if(jo.has("application_ids")){
    		appids = jo.get("application_ids").getAsString();
    	}
    	
    	
    	
    	SysRoleEntity role = new SysRoleEntity();
    	role.setRoleId(roleId);
    	role.loadVo();
    	if(PublicMethod.isEmptyStr(role.getRoleCode())){
    		return error("角色不存在");
    	}else{
    		if(role.getRoleCode().equals("1001")){
    			return error("超级管理员不用添加应用");
    		}
    	}
    	List<Long> listAppIds = new ArrayList<Long>();
    	if(!PublicMethod.isEmptyStr(appids)){
    		String[] ids = appids.split(",");
    		for(int i=0,len =ids.length ;i<len;i++){
    			try{
    				listAppIds.add(Long.parseLong(ids[i]));
    			}catch(Exception e){
    				logger.error("输换数据类型错误",e);
    			}
    		}
    		ids = null;
    	}
    	
    	
    	
    	SysRoleApplicationEntity ra = new SysRoleApplicationEntity();
    	List<SysRoleApplicationEntity> listRA = ra.getListVO(new SQLWhere(new EQCnd("role_id", roleId)));
    	List<Long> appidList = new ArrayList<Long>();
    	for(SysRoleApplicationEntity roleApp : listRA){
    		if(listAppIds.indexOf(roleApp.getApplicationId()) == -1){
    			SysApplicationEntity app = new SysApplicationEntity();
        		app.setApplicationId(roleApp.getApplicationId());
        		app.loadVo();
        		if(app.getValid() == StaticBean.YES){
        			roleApp.delete();
        			appidList.add(roleApp.getApplicationId());
        		}
    		}else{
    			listAppIds.remove(roleApp.getApplicationId());
    		}
    	}
    	try{
    		if(appidList.size() > 0){
    			deleteRoleApp(appidList, roleId);//删除关联的角色与应用数据
    		}
    	}catch(Exception e){
    		logger.error("查询删除关联的角色与应用数据", e);
    	}
    	
    	for(Long appId : listAppIds){
    		SysApplicationEntity app = new SysApplicationEntity();
    		app.setApplicationId(appId);
    		app.loadVo();
    		if(PublicMethod.isEmptyStr(app.getApplicationCode()))
    			continue;
    		SysRoleApplicationEntity roleApp = new SysRoleApplicationEntity();
    		roleApp.setRoleId(roleId);
    		roleApp.setApplicationId(appId);
    		roleApp.insert();
    	}
    	
        return success("分配成功");
    }
    
   
    private void deleteRoleApp(List<Long> appid,Long roleId){
    	List<SysRoleEntity> list =new SysRoleEntity().getListVO(new SQLWhere(new EQCnd("parent_id", roleId)));
    	for(SysRoleEntity role: list){
    		List<SysRoleApplicationEntity> sraList = new SysRoleApplicationEntity().getListVO(new SQLWhere(new EQCnd("role_id", role.getRoleId())).and(new INCnd("application_id", appid)));
    		for(SysRoleApplicationEntity sra : sraList){
    			try {
					sra.delete();
				} catch (Exception e) {
					logger.error("删除关联的角色与应用数据出错", e);
				}
    		}
    		deleteRoleApp(appid, role.getRoleId());
    	}
    	
    }
    
    
}
