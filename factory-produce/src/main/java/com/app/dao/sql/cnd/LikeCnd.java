package com.app.dao.sql.cnd;

public class LikeCnd  extends Cnd{
	

	
	/**
	 * 模糊like条件
	 * @param name 字段名称
	 * @param object 字段值
	 */
	public LikeCnd(String name, String object) {
		super(name, object);
	}

	@Override
	public String toString() {
		return new StringBuilder(name).append( Cnd.LIKE).append("'%").append(object.toString()).append("%'").toString();
	}
	

}
