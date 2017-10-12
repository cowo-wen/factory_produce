package com.app.dao.sql.cnd;

public class LikeCnd  extends Cnd{
	

	

	public LikeCnd(String name, String object) {
		super(name, object);
	}

	@Override
	public String toString() {
		return new StringBuilder(name).append( Cnd.LIKE).append("'%").append(object.toString()).append("%'").toString();
	}
	

}
