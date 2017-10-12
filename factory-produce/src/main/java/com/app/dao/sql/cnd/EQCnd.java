package com.app.dao.sql.cnd;



public class EQCnd  extends Cnd{

	public EQCnd(String name, Object object) {
		super(name, object);
	}

	@Override
	public String toString() {
		if(object.getClass().equals(Integer.class) || object.getClass().equals(Long.class) || object.getClass().equals(Float.class)|| object.getClass().equals(Double.class)){
			return new StringBuilder(name).append(Cnd.EQ).append(object.toString()).toString();
		}else {
			return new StringBuilder(name).append(Cnd.EQ).append("'").append(object.toString()).append("'").toString();
		}
	}
	
	

}
