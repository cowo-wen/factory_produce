package com.app.controller.common;

import java.lang.reflect.Type;
import java.util.Date;

import com.app.util.PublicMethod;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonDateConverter implements JsonSerializer<Date>, JsonDeserializer<Date>{

	@Override
	public Date deserialize(JsonElement paramJsonElement, Type paramType,
			JsonDeserializationContext paramJsonDeserializationContext)
			throws JsonParseException {
		// TODO Auto-generated method stub
		return PublicMethod.stringToDate(paramJsonElement.getAsJsonPrimitive().getAsString(), "yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public JsonElement serialize(Date paramT, Type paramType,
			JsonSerializationContext paramJsonSerializationContext) {
		if(paramT == null){
			return new JsonPrimitive("");
		}else{
			return new JsonPrimitive(PublicMethod.formatDateStr(paramT,"yyyy-MM-dd HH:mm:ss"));
		}
		
	}

}
