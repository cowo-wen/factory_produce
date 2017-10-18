package com.app.dao.sql.sort;

import com.google.gson.Gson;


public class AscSort extends Sort
{

  
  
  

	public AscSort(String... name) {
		super(name);
	}




	@Override
	public String toString() {
	  	String value = new Gson().toJson(name);
		return value.substring(1, value.length()-1).replaceAll("\"", "") + Sort.ASC;
	}


}

