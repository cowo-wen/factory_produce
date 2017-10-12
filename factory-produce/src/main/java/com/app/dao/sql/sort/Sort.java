package com.app.dao.sql.sort;

public abstract class Sort
{
  public static final String DESC = " desc ";
  public static final String ASC = " asc ";

  protected String[] name;
  
  
  

  public Sort(String... name) {
	super();
	this.name = name;
  }




  public abstract String toString();


}

