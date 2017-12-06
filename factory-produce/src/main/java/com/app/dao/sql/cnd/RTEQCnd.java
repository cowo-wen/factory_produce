package com.app.dao.sql.cnd;

public class RTEQCnd  extends Cnd{
	

	

	public RTEQCnd(String name, Object object) {
		super(name, object);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		if(object.getClass().equals(Integer.class) || object.getClass().equals(Long.class) || object.getClass().equals(Float.class)|| object.getClass().equals(Double.class)){
			return new StringBuilder(name).append( Cnd.RTEQ).append(object.toString()).toString();
		}else {
			return new StringBuilder(name).append( Cnd.RTEQ).append("'").append(object.toString()).append("'").toString();
		}
	}
	
	

}
