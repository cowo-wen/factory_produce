package com.app.controller.v1.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.sys.SysUserEntity;
import com.app.entity.sys.SysUserRoleEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xx.util.string.Format;
import com.xx.util.string.MD5;

/**
 * 功能说明：用户管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/user")
@Scope("prototype")//设置成多例
public class UserAPI extends Result{
    public static Log logger = LogFactory.getLog(UserAPI.class);
    
  
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) throws Exception{
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(SysUserEntity.USER_ID));
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
            else if (jsonObject.get(NAME).getAsString().equals(SysUserEntity.USER_NAME)){  
            	sql.and(new LikeCnd(SysUserEntity.USER_NAME,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(SysUserEntity.MOBILE)){  
            	sql.and(new LikeCnd(SysUserEntity.MOBILE,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(SysUserEntity.NUMBER)){  
            	sql.and(new LikeCnd(SysUserEntity.NUMBER,jsonObject.get(VALUE).getAsString()));
            }
    	}
    	
    	SysUserEntity entity = new SysUserEntity(jdbcDao);
    	entity.outPut("role");
    	List<SysUserEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = entity.getCount(sql);
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
    	SysUserEntity entity = new SysUserEntity(jdbcDao);
    	entity.setUserId(id);
    	entity.loadVo();
        return success(entity);
    }
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	SysUserEntity entity = new SysUserEntity(jdbcDao);
    	entity.setUserId(id);
    	entity.loadVo();
    	if(!PublicMethod.isEmptyStr(entity.getLoginName()) && entity.getLoginName().equals(SysUserEntity.ADMIN_USER_NAME)){
    		return error("超级用户不能删除");
    	}else{
    		
    		entity.useTransaction();
    		try {
    			SysUserRoleEntity sur = new SysUserRoleEntity(jdbcDao);
				List<SysUserRoleEntity>  list = sur.setUserId(id).queryCustomCacheValue(0, null);
				entity.delete();
				if(list != null){
					for(SysUserRoleEntity ur : list){
						ur.delete();
					}
				}
				return success("删除成功");
			} catch (Exception e) {
				e.printStackTrace();
				return error("删除失败:"+e.getMessage());
			}
            
    	}
    	
    }
    
   
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) throws Exception{
    	SysUserEntity entity = new SysUserEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	if(PublicMethod.isEmptyStr(entity.getUserName())){
    		return error("人员名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getNumber())){
    		return error("人员编号不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getMobile())){
    		return error("手机号码不能为空");
    	}else{
    		if(entity.getLoginName().equals(SysUserEntity.ADMIN_USER_NAME)){
    			return error("超级管理员不能修改");
    		}
    		entity.setLoginName(entity.getMobile());
    	}
    	
    	
    	
    	if(PublicMethod.isEmptyValue(entity.getValid())){
    		entity.setValid(StaticBean.YES);
    	}
    	try{
    		
    		entity.update();
        	return success("修改成功",entity.getUserId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    /**
     * 管理员修改用户密码
     * @param aoData
     * @return
     * @throws Exception
     */
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/managePW")
    public String managePW(@RequestParam String aoData) throws Exception{
    	SysUserEntity entity = new SysUserEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	
    	if(PublicMethod.isEmptyStr(entity.getPassword()) || entity.getPassword().length() != 32){
    		return error("密码不能为空");
    	}
    	entity.setPassword(MD5.encode(entity.getPassword()));
    	try{
    		
    		entity.update();
        	return success("修改成功",entity.getUserId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    /**
     * 用户修改个人密码
     * @param aoData
     * @return
     * @throws Exception
     */
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/updatePW")
    public String updatePW(@RequestParam String aoData) throws Exception{
    	SysUserEntity entity = new SysUserEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	if(PublicMethod.isEmptyStr(entity.getPassword()) || entity.getPassword().length() != 32){
    		return error("密码不能为空");
    	}
    	entity.setPassword(MD5.encode(entity.getPassword()));
    	try{
    		entity.update();
        	return success("修改成功",entity.getUserId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) throws Exception{
    	SysUserEntity entity = new SysUserEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	if(PublicMethod.isEmptyStr(entity.getUserName())){
    		return error("人员名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getNumber())){
    		return error("人员编号不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getMobile())){
    		return error("手机号码不能为空");
    	}else{
    		if(!Format.isMobile(entity.getMobile())){
    			return error("非法的手机号码");
    		}
    		entity.setLoginName(entity.getMobile());
    	}
    	
    	entity.setPassword(MD5.encode(entity.getMobile().substring(5)));
    	entity.setType(SysUserEntity.USER_GENERAL);
    	
    	if(PublicMethod.isEmptyValue(entity.getValid())){
    		entity.setValid(StaticBean.YES);
    	}
    	try{
    		entity.insert();
        	return success("新增成功",entity.getUserId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
