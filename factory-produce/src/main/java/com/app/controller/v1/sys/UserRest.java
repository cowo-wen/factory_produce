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
import com.app.dao.sql.sort.DescSort;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xx.util.string.Format;
import com.xx.util.string.MD5;

/**
 * 功能说明：应用管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/user")
public class UserRest extends Result{
    public static Log logger = LogFactory.getLog(UserRest.class);
    
  
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) throws Exception{
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	
    	int iDisplayStart = 0;// 起始  
    	int iDisplayLength = 10;// size 
    	int sEcho = 0;
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get("name").getAsString().equals("sEcho"))  
                sEcho = jsonObject.get("value").getAsInt();  
            else if (jsonObject.get("name").getAsString().equals("iDisplayStart"))  
                iDisplayStart = jsonObject.get("value").getAsInt();  
            else if (jsonObject.get("name").getAsString().equals("iDisplayLength"))  
                iDisplayLength = jsonObject.get("value").getAsInt(); 
    	}
    	
    	logger.error(aoData);
    	SysUserEntity user = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
    	
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort("user_id"));
    	
    	List<SysUserEntity> list = user.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = user.getCount(sql);
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
    	SysUserEntity entity = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
    	entity.setUserId(id);
    	entity.loadVo();
        return success(entity);
    }
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	SysUserEntity entity = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
    	entity.setUserId(id);
    	entity.loadVo();
    	if(!PublicMethod.isEmptyStr(entity.getLoginName()) && entity.getLoginName().equals("admin")){
    		return error("超级用户不能删除");
    	}else{
    		try {
				entity.delete();
				return success("删除成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return error("删除失败:"+e.getMessage());
			}
            
    	}
    	
    }
    
   
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) throws Exception{
    	SysUserEntity entity = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
    	logger.error("-------"+aoData);
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
    		if(entity.getLoginName().equals("admin")){
    			return error("超级管理员不能修改");
    		}
    		entity.setLoginName(entity.getMobile());
    	}
    	
    	
    	
    	if(PublicMethod.isEmptyValue(entity.getValid())){
    		entity.setValid(1);
    	}
    	try{
    		
    		entity.update();
    		//sysApplicationService.update(entity);
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
    	SysUserEntity entity = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
    	logger.error("-------"+aoData);
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
    	SysUserEntity entity = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
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
    	SysUserEntity entity = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	logger.error("aoData="+aoData);
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
    	entity.setType(2);
    	
    	if(PublicMethod.isEmptyValue(entity.getValid())){
    		entity.setValid(1);
    	}
    	try{
    		entity.insert();
        	return success("新增成功",entity.getUserId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
