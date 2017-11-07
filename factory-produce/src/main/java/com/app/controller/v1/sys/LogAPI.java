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
import com.app.entity.sys.SysLogEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：日志信息
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/sys/log")
public class LogAPI extends Result{
    public static Log logger = LogFactory.getLog(LogAPI.class);
    
  
    
   
    
    @RequestMapping(method=RequestMethod.GET,value="/list")
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
    	SysLogEntity log = new SysLogEntity(null);
    	
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort("log_id"));
    	//if(!PublicMethod.isEmptyStr(ip)){
    	//	sql.and(new EQCnd("ip", ip));
    	//}
    	List<CacheVo> list = log.getListVO(iDisplayStart, iDisplayLength, sql);
    	long count = log.getCount(sql);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", count);
    	map.put("iTotalDisplayRecords", count);
        return success(map);
    }
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) throws Exception{
    	SysLogEntity log = new SysLogEntity();
    	log.setLogId(id);
    	log.delete();
        return success("删除成功");
    }
    
    
    /**
     * 批量删除
     * @param ids
     * @return
     * @throws Exception
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/deleteBatch/{ids}")
    public String deleteBatch(@PathVariable(name="ids",required=true) String ids) throws Exception{
    	String [] logIds = ids.split(",");
    	for(String id : logIds){
    		SysLogEntity log = new SysLogEntity();
    		log.setLogId(Long.parseLong(id));
    		log.delete();
    	}
    	return success("批量删除成功");
    }
    
    
   
    
}
