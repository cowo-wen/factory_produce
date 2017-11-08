package com.app.controller.v1.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.sys.SysRoleEntity;
import com.app.entity.sys.SysUserRoleEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：角色管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/role")
public class RoleAPI extends Result{
    public static Log logger = LogFactory.getLog(RoleAPI.class);
    
  
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) throws Exception{
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort("role_id"));
    	int iDisplayStart = 0;// 起始  
    	int iDisplayLength = 10;// size 
    	int sEcho = 0;
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get("name").getAsString().equals("roleName")){  
            	sql.and(new LikeCnd("role_name",jsonObject.get("value").getAsString()));
            }
    	}
    	
    	SysRoleEntity Role = new SysRoleEntity();
    	
    	
    	
    	List<SysRoleEntity> list = Role.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = Role.getCount(sql);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", count);
    	map.put("iTotalDisplayRecords", count);
        return success(map);
    }
    
    /**
     * 获取单个对象
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/vo/{id}")
    public String vo(@PathVariable("id") Long id) throws Exception{
    	SysRoleEntity entity = new SysRoleEntity();
    	entity.setRoleId(id);
    	entity.loadVo();
        return success(entity);
    }
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	SysRoleEntity entity = new SysRoleEntity();
    	entity.setRoleId(id);
    	entity.loadVo();
    	try {
    		if(PublicMethod.isEmptyStr(entity.getRoleCode())){
    			return error("角色不存在");
    		}else if(entity.getRoleCode().equals(SysRoleEntity.ADMIN_CODE)){
    			return error("根角色不能删除");
    		}
    		List<SysRoleEntity> list = entity.getListVO(0, 1000, new SQLWhere(new LikeCnd("link_code", entity.getRoleCode())).or(new EQCnd("role_code", entity.getRoleCode())));
    		if(list != null && list.size() > 0){
    			for(SysRoleEntity role : list ){
    				List<SysUserRoleEntity> listUR = new SysUserRoleEntity().getListVO(0, 1000, new SQLWhere(new EQCnd("role_id", entity.getRoleId())));
    				if(listUR != null && listUR.size() > 0){
    					for(SysUserRoleEntity ur : listUR){
    						ur.delete();//删除人员与角色的关联表
    					}
    				}
    				role.delete();//删除角色
        		}
    		}
    		
			return success("删除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return error("删除失败:"+e.getMessage());
		}
    	
    }
    
   
    
   
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) throws Exception{
    	SysRoleEntity entity = new SysRoleEntity();
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	
    	if(PublicMethod.isEmptyStr(entity.getRoleName())){
    		return error("角色名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getRoleCode())){
    		return error("角色编号不能为空");
    	}
    	
    	try{
    		SysRoleEntity entity2 = new SysRoleEntity();
        	entity2.setRoleId(entity.getRoleId());
        	entity2.loadVo();
        	if(entity2.getRoleCode() == null){
        		return error("角色数据不存在");
        	}else{
        		if(entity2.getRoleCode().equals(SysRoleEntity.ADMIN_CODE)){
            		return error("角色不能修改");
            	}
        		if(entity2.getRoleCode().equals(entity.getRoleCode())){
            		return error("角色编号不能修改");
            	}
        		if(entity2.getParentId() != entity.getParentId()){
            		return error("父节点不能修改");
            	}
        	}
        	
    		
    		entity.update();
        	return success("修改成功",entity.getRoleId());
    	}catch(Exception e){
    		logger.error("更新出错", e);
    		return error(e.getMessage());
    	}
    	
    }
    
   
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) throws Exception{
    	SysRoleEntity entity = new SysRoleEntity();
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	if(PublicMethod.isEmptyStr(entity.getRoleName())){
    		return error("角色名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getRoleCode())){
    		return error("角色编号不能为空");
    	}else if(entity.getRoleCode().length() != 4){
    		return error("角色编号字符长度等于4位");
    	}
    	
    	
    	if(entity.getParentId() != null && entity.getParentId() > 0){
    		SysRoleEntity entity2 = new SysRoleEntity();
        	entity2.setRoleId(entity.getParentId());
        	entity2.loadVo();
        	if(entity2.getRoleCode() == null){
        		return error("父节点数据不存在");
        	}else{
        		entity.setLinkCode(entity2.getLinkCode()+","+entity.getRoleCode());
        	}
    	}else{
    		return error("父节点不存在");
    	}
    	
    	
    	
    	
    	try{
    		entity.setValid(StaticBean.YES);
    		entity.insert();
        	return success("新增成功",entity.getRoleId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
