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
import com.app.entity.common.CacheVo;
import com.app.entity.sys.SysApplicationEntity;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：应用管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/application")
public class ApplicationRest extends Result{
    public static Log logger = LogFactory.getLog(ApplicationRest.class);
    
  
    
    
   
    
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
    	SysApplicationEntity log = new SysApplicationEntity(RedisAPI.REDIS_CORE_DATABASE);
    	
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort("sort_code"));
    	
    	List<CacheVo> list = log.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = log.getCount(sql);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", count);
    	map.put("iTotalDisplayRecords", count);
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
    }
    
    /**
     * 获取单个对象
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/vo/{id}")
    public String vo(@PathVariable("id") Long id) throws Exception{
    	SysApplicationEntity entity = new SysApplicationEntity(RedisAPI.REDIS_CORE_DATABASE);
    	entity.setApplicationId(id);
    	entity.loadVo();
        return success(entity);
    }
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) throws Exception{
    	SysApplicationEntity app = new SysApplicationEntity(RedisAPI.REDIS_CORE_DATABASE);
    	app.setApplicationId(id);
    	//app.deleteNoSql();
    	app.delete();
    	//sysApplicationService.delete(app);
        return "删除成功";
    }
    
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) throws Exception{
    	SysApplicationEntity entity = new SysApplicationEntity(RedisAPI.REDIS_CORE_DATABASE);
    	logger.error("-------"+aoData);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	
    	if(PublicMethod.isEmptyStr(entity.getName())){
    		return error("应用名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getApplicationCode())){
    		return error("应用编号不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getEventType())){
    		return error("事件类型不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getTerminalType())){
    		return error("终端类型不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getAppType())){
    		return error("应用类型不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getParentId())){
    		entity.setParentId(0L);
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getOutCode())){
    		entity.setOutCode("");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getValid())){
    		entity.setValid(1);
    	}
    	try{
    		entity.update();
    		//sysApplicationService.update(entity);
        	return success("修改成功",entity.getApplicationId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) throws Exception{
    	SysApplicationEntity entity = new SysApplicationEntity(RedisAPI.REDIS_CORE_DATABASE);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	
    	if(PublicMethod.isEmptyStr(entity.getName())){
    		return error("应用名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getApplicationCode())){
    		return error("应用编号不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getEventType())){
    		return error("事件类型不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getTerminalType())){
    		return error("终端类型不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getAppType())){
    		return error("应用类型不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getParentId())){
    		entity.setParentId(0L);
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getOutCode())){
    		entity.setOutCode("");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getValid())){
    		entity.setValid(1);
    	}
    	try{
    		entity.insert();
    		//sysApplicationService.save(entity);
        	return success("新增成功",entity.getApplicationId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
