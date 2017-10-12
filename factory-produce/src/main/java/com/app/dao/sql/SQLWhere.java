package com.app.dao.sql;

import java.util.ArrayList;
import java.util.List;

import com.app.dao.sql.cnd.Cnd;
import com.app.dao.sql.sort.Sort;

public class SQLWhere {
	private List<SQLWhere> listOR = new ArrayList<SQLWhere>();
	private StringBuilder sb = new StringBuilder();
	private StringBuilder sortSB = new StringBuilder();
	
	public SQLWhere or (SQLWhere where){
		if(where != null){
			listOR.add(where);
		}
		return this;
		
	}
	
	public SQLWhere(Cnd cnd) {
		super();
		sb.append(cnd.toString());
	}
	
	public SQLWhere() {
		super();
	}

	public SQLWhere and(Cnd cnd){
		if(sb.length() > 0){
			sb.append(" and ").append(cnd.toString());
		}else{
			sb.append(cnd.toString());
		}
		return this;
	}
	
	public SQLWhere or(Cnd cnd){
		if(sb.length() > 0){
			sb.append(" or ").append(cnd.toString());
		}else{
			sb.append(cnd.toString());
		}
		return this;
	}
	
	public SQLWhere orderBy(Sort... sort){
		if(sort.length > 0){
			for(Sort s : sort){
				if(sortSB.length() > 0){
					sortSB.append(",").append(s.toString());
				}else{
					sortSB.append(s.toString());
				}
			}
		}
		return this;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(" where (").append(sb.toString()).append(")");
		if(listOR.size() > 0){
			for(SQLWhere sql : listOR){
				s.append(" or ").append(sql.toString());
			}
		}
		if(sortSB.length() > 0){
			s.append(" order by ").append(sortSB.toString());
		}
		return s.toString();
	}
	
	
}
