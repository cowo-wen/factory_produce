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
import com.app.entity.sys.SysConfigEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：系统配置
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/config")
@Scope("prototype")
public class ConfigAPI extends Result{
    public static Log logger = LogFactory.getLog(ConfigAPI.class);
    
  
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) {
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(SysConfigEntity.ID));
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
            else if (jsonObject.get(NAME).getAsString().equals(SysConfigEntity.NAME)){  
            	sql.and(new LikeCnd(SysConfigEntity.NAME,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(SysConfigEntity.GROUP_CODE)){  
            	sql.and(new LikeCnd(SysConfigEntity.GROUP_CODE,jsonObject.get(VALUE).getAsString()));
            }
    	}
    	
    	SysConfigEntity entity = new SysConfigEntity(jdbcDao);
    	List<SysConfigEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
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
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/vo/{id}")
    public String vo(@PathVariable("id") Long id) {
    	SysConfigEntity entity = new SysConfigEntity(jdbcDao);
    	entity.setId(id);
    	entity.loadVo();
        return success(entity);
    }
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	SysConfigEntity entity = new SysConfigEntity(jdbcDao);
    	entity.setId(id);
    	
		try {
			entity.delete();
			return success("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return error("删除失败:"+e.getMessage());
		}
            
    	
    	
    }
    
   
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) {
    	SysConfigEntity entity = new SysConfigEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	if(PublicMethod.isEmptyStr(entity.getName())){
    		return error("名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getGroupCode())){
    		return error("组编码不能为空");
    	}
    	
    	
    	
    	
    	
    	if(PublicMethod.isEmptyValue(entity.getValid())){
    		entity.setValid(StaticBean.YES);
    	}
    	try{
    		
    		entity.update();
        	return success("修改成功",entity.getId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) {
    	SysConfigEntity entity = new SysConfigEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	if(PublicMethod.isEmptyStr(entity.getName())){
    		return error("名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getGroupCode())){
    		return error("组编码不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getValid())){
    		entity.setValid(StaticBean.YES);
    	}
    	try{
    		entity.insert();
        	return success("新增成功",entity.getId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
