package com.app.controller.common;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Result {
	public static final String NAME = "name";
	public static final String S_ECHO = "sEcho";
	public static final String VALUE = "value";
	public static final String I_DISPLAY_START = "iDisplayStart";
	public static final String I_DISPLAY_LENGTH = "iDisplayLength";
	
	protected int iDisplayStart = 0;// 起始  
	protected int iDisplayLength = 10;// size 
	protected int sEcho = 0;
	
	private static final Gson gson = new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
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
		
		return new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
	}
	
	
	
	public String success(String message,Object id){
		Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("message", message);
    	map.put("id", id);
    	synchronized (gson) {
			return gson.toJson(map);
		}
    	//return new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
	}
	
	public String error(String message){
		Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 500);
    	map.put("message", message);
    	synchronized (gson) {
			return gson.toJson(map);
		}
    	//return new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(map);
	}

}
