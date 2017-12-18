package com.app.entity.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CustomCacheBean {
	
	public static Log logger = LogFactory.getLog(CustomCacheBean.class);
	private CacheVo vo;
	
	private int groupName;
	
	private List<Field> field;
	
	private Field hashSetKey;
	
	private Map<Integer,Field> map = new HashMap<Integer,Field>();
	
	
	
	
	
	public CustomCacheBean(int groupName) {
		super();
		this.groupName = groupName;
	}



	


	public int getGroupName() {
		return groupName;
	}




	


	public List<Field> getField() {
		if(field == null){
			int len = map.size();
			field = new ArrayList<Field>();
			for(int i = 0;i<len;i++){
				field.add(map.get(i));
			}
		}
		return field;
	}






	public void setMap(int sort,Field field) {
		map.put(sort, field);
	}



	public String getHashSetKey(CacheVo vo) {
		this.vo = vo;
		if(hashSetKey == null){
			return "id";
		}
		Object obj = getFieldValue(hashSetKey);
		if(obj == null){
			return "0";
		}
		return obj.toString();
	}



	public void setHashSetKey(Field hashSetKey) {
		this.hashSetKey = hashSetKey;
	}



	private synchronized Object getFieldValue(Field field){
		try {
			field.setAccessible(true);
			return field.get(vo);
		}catch (Exception e) {
			logger.error("字段【"+field.getName()+"】获取失败", e);
		}finally{
			field.setAccessible(false);
		}
		
		return null;
	}
	
	

	public String toString(CacheVo vo){
		this.vo = vo;
		field = getField();
		StringBuilder sb = new StringBuilder();
		Object tableName = vo.getTableName();//获取表名
		sb.append(tableName).append(":").append("custom");
		for(Field f : field){
			sb.append(":").append(f.getName()).append(":").append(getFieldValue(f));
		}
		return sb.toString();
	}
	
}
