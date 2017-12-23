package com.app.dao.sql.cnd;

import com.google.gson.Gson;



public class NotINCnd  extends Cnd{

	/**
	 * 不包含条件
	 * @param name 字段名称
	 * @param object 可以为数组或List集合或select开始的查询语句
	 */
	public NotINCnd(String name, Object object) {
		super(name, object);
	}

	@Override
	public String toString() {
		if(object instanceof java.util.List || object.getClass().isArray()){
			String value = new Gson().toJson(object);
			if(value.length() > 0){
				return new StringBuilder(name).append( Cnd.NOT_IN).append("(").append(value.substring(1, value.length() -1)).append(")").toString();
			}
    	}else if(object.getClass().equals(String.class) ){
    		String value = object.toString().trim().toLowerCase();
    		if(value.startsWith("select")){
    			return new StringBuilder(name).append( Cnd.NOT_IN).append("(").append(object.toString()).append(")").toString();
    		}
    	}
		return "";
		
	}
	
	

}
