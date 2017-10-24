package com.app.controller.common;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;

public class Result {
	
	
	@SuppressWarnings("unchecked")
	public String success(Object obj){
		Map<String,Object> map = null;
		if(obj instanceof Map){
			map = (Map<String, Object>)obj;
			map.put("status", 200);
			if(map.containsKey("message")){
				map.put("message", "");
			}
		}else{
			map = new HashMap<String,Object>();
	    	map.put("status", 200);
	    	map.put("data", obj);
	    	map.put("id", "成功");
		}
		return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
	}
	
	
	
	public String success(String message,Object id){
		Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("message", message);
    	map.put("id", id);
    	return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
	}
	
	public String error(String message){
		Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 500);
    	map.put("message", message);
    	return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
	}

}
