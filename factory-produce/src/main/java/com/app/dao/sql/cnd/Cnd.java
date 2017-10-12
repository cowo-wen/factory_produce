package com.app.dao.sql.cnd;

public abstract class Cnd
{
  public static final String LT = " < ";
  public static final String LTEQ = " <= ";
  public static final String RT = " > ";
  public static final String RTEQ = " >= ";
  public static final String EQ = " = ";
  public static final String NO = " <> ";
  public static final String IN = " IN ";
  public static final String NOT_IN = " NOT IN ";
  public static final String IS = " IS ";
  public static final String IS_NOT = " IS_NOT ";
  public static final String LIKE = " LIKE ";

  protected String name;
  
  protected Object object;
  
  
  

  public Cnd(String name, Object object) {
	super();
	this.name = name;
	this.object = object;
}




  public abstract String toString();


}

