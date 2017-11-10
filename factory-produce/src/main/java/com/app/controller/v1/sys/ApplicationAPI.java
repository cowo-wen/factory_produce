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
import com.app.dao.sql.cnd.NotINCnd;
import com.app.dao.sql.sort.AscSort;
import com.app.entity.sys.SysApplicationEntity;
import com.app.entity.sys.SysRoleApplicationEntity;
import com.app.util.PublicMethod;
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
public class ApplicationAPI extends Result {
    public static Log logger = LogFactory.getLog(ApplicationAPI.class);
    
  
    
    
   
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) throws Exception{
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	
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
    	}
    	
    	logger.error(aoData);
    	SysApplicationEntity entity = new SysApplicationEntity();
    	
    	SQLWhere sql = new SQLWhere().orderBy(new AscSort(SysApplicationEntity.SORT_CODE));
    	
    	List<SysApplicationEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = 0;
    	//count = entity.getCount(sql);
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
    	SysApplicationEntity entity = new SysApplicationEntity();
    	entity.setApplicationId(id);
    	entity.loadVo();
        return success(entity);
    }
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) throws Exception{
    	SysApplicationEntity app = new SysApplicationEntity();
    	app.setApplicationId(id);
    	app.deleteLinkChild(SysApplicationEntity.PARENT_ID);
    	List<SysRoleApplicationEntity> list = new SysRoleApplicationEntity().getListVO(new SQLWhere(new NotINCnd(SysApplicationEntity.APPLICATION_ID, " select application_id from t_sys_application ")));
    	if(list != null && list.size() > 0){
    		for(SysRoleApplicationEntity ra : list){
        		ra.delete();
        	}
    	}
    	return success("删除成功");
    }
    
   
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) throws Exception{
    	SysApplicationEntity entity = new SysApplicationEntity();
    	logger.error("-------"+aoData);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	jo.remove(SysApplicationEntity.APPLICATION_CODE);
    	jo.remove("applicationCode");
    	entity.parse(jo);
    	
    	
    	//SysApplicationEntity entity2 = new SysApplicationEntity();
    	//entity2.setApplicationId(entity.getApplicationId()).loadVo();
    	//entity.setParentApplicationCode(entity2.getParentApplicationCode());
    	//entity.setApplicationCode(entity2.getApplicationCode());
    	if(PublicMethod.isEmptyStr(entity.getName())){
    		return error("应用名称不能为空");
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
    	
    	if(PublicMethod.isEmptyValue(entity.getUrl())){
    		//return error("url路径不能为空");
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
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) throws Exception{
    	SysApplicationEntity entity = new SysApplicationEntity();
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
    	
    	if(PublicMethod.isEmptyValue(entity.getUrl())){
    		//return error("url路径不能为空");
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
    		if(entity.getParentId() > 0){
    			SysApplicationEntity entity2 = new SysApplicationEntity();
    			entity2.setApplicationId(entity.getParentId());
    			entity2.loadVo();
    			entity.setParentApplicationCode(entity2.getApplicationCode());
    		}else{
    			entity.setParentApplicationCode("0");
    		}
    		entity.insert();
    		//sysApplicationService.save(entity);
        	return success("新增成功",entity.getApplicationId());
    	}catch(Exception e){
    		logger.error("新增出错", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
