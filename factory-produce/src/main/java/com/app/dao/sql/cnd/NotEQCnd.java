package com.app.dao.sql.cnd;



public class NotEQCnd  extends Cnd{

	/**
	 * 不等于条件
	 * @param name 字段名称
	 * @param object 可以为数组或List集合或select开始的查询语句
	 */
	public NotEQCnd(String name, Object object) {
		super(name, object);
	}

	@Override
	public String toString() {
		if(object.getClass().equals(Integer.class) || object.getClass().equals(Long.class) || object.getClass().equals(Float.class)|| object.getClass().equals(Double.class)){
			return new StringBuilder(name).append(Cnd.NO).append(object.toString()).toString();
		}else {
			return new StringBuilder(name).append(Cnd.NO).append("'").append(object.toString()).append("'").toString();
		}
	}
	
	

}
