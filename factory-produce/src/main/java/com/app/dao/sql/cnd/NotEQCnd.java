package com.app.dao.sql.cnd;



public class NotEQCnd  extends Cnd{

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
