package com.app.controller.v1.repertory;

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
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.repertory.RepertoryGoodsComponentEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：产品配件信息
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/repertory/component")
@Scope("prototype")//设置成多例
public class ComponentAPI extends Result{
    public static Log logger = LogFactory.getLog(ComponentAPI.class);
    
  
    /**
     * 查询列表
     * @param aoData
     * @return
     * @throws Exception
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) throws Exception{
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(RepertoryGoodsComponentEntity.GOODS_COMPONENT_ID));
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsComponentEntity.GOODS_ID)){  
            	sql.and(new EQCnd(RepertoryGoodsComponentEntity.GOODS_ID,jsonObject.get(VALUE).getAsString()));
            }
    	}
    	
    	logger.error(aoData);
    	RepertoryGoodsComponentEntity entity = new RepertoryGoodsComponentEntity(jdbcDao);
    	
    	entity.outPut(RepertoryGoodsComponentEntity.NAME,RepertoryGoodsComponentEntity.TYPE,RepertoryGoodsComponentEntity.CODE);
    	List<RepertoryGoodsComponentEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
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
    	RepertoryGoodsComponentEntity entity = new RepertoryGoodsComponentEntity(jdbcDao);
    	entity.setGoodsComponentId(id).loadVo();
        return success(entity);
    }

    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	RepertoryGoodsComponentEntity entity = new RepertoryGoodsComponentEntity(jdbcDao);
    	try{
    		entity.setGoodsComponentId(id).delete();
    		return success("删除成功");
    	}catch(Exception e){
    		logger.error("删除失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
   
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) throws Exception{
    	RepertoryGoodsComponentEntity entity = new RepertoryGoodsComponentEntity(jdbcDao);
    	logger.error("-------"+aoData);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	
    	entity.parse(jo);
    	
    	
    	
    	try{
    		
    		entity.update();
        	return success("修改成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) throws Exception{
    	RepertoryGoodsComponentEntity entity = new RepertoryGoodsComponentEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	logger.error("aoData="+aoData);
    	
    	try{
    		entity.insert();
        	return success("新增成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("新增失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
