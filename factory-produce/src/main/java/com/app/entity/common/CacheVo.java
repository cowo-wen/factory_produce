/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.app.dao.JdbcDao;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xx.util.string.Format;

/**
 * 功能说明：缓存表
 * 
 * @author chenwen 2017-8-11
 */
@Service
public class CacheVo {
	public static Log logger = LogFactory.getLog(CacheVo.class);
	
	private static final String FIXED_DEFINITION_TABLE="table";
	
	private static final String FIXED_DEFINITION_NAME="name";
	
	private static final String FIXED_DEFINITION_COLUMN_ORM="column_orm";
	
	private static final String FIXED_DEFINITION_ID="id";
	
	private static final String FIXED_DEFINITION_ID_ORM="id_orm";
	
	private static final String FIXED_DEFINITION_COLUMN="column";
	
	private static final String FIXED_DEFINITION_COLON=":";
	
	private static final String FIXED_DEFINITION_CUSTOM ="custom";
	
	private static final String FIXED_DEFINITION_FIELD_1 ="field_1";
	
	private static final String FIXED_DEFINITION_FIELD_2 ="field_2";
	
	private static final String FIXED_DEFINITION_FIELD_3 ="field_3";
	
	private static final String FIXED_DEFINITION_FIELD_4 ="field_4";
	
	private static final String FIXED_DEFINITION_FIELD_5 ="field_5";
	
	private static final String FIXED_DEFINITION_VALUE_1 ="value_1";
	
	private static final String FIXED_DEFINITION_VALUE_2 ="value_2";
	
	private static final String FIXED_DEFINITION_VALUE_3 ="value_3";
	
	private static final String FIXED_DEFINITION_VALUE_4 ="value_4";
	
	private static final String FIXED_DEFINITION_VALUE_5 ="value_5";
	
	private static final String FIXED_DEFINITION_TIME_FORMAT="yyyy-MM-dd HH:mm:ss";
	
	private final static Map<Class<?>, Map<String, Object>> map = new HashMap<Class<?>, Map<String, Object>>();
	
	 
	private static ApplicationContext applicationContext = null;//启动类set入，调用下面set方法
	
	private String redisAPIName = null;

    public synchronized static void setApplicationContext(ApplicationContext context) {
    	if(applicationContext == null){
    		applicationContext = context;
    	}
    }
    
	
	
	public CacheVo() {
		super();
	}

	public CacheVo(String redisAPIName) {
		super();
		this.redisAPIName = redisAPIName;
	}

	public synchronized JdbcDao getJdbcDao(){
		return (JdbcDao)applicationContext.getBean("jdbcDao");
	}
	
	private synchronized void setFieldValue(Field field,Object object){
		synchronized (field) {
			try {
				field.setAccessible(true);
				field.set(this, object);
			}catch (Exception e) {
				logger.error("字段【"+field.getName()+"】赋值失败", e);
			}finally{
				field.setAccessible(false);
			}
		}
	}
	
	private synchronized Object getFieldValue(Field field){
		try {
			field.setAccessible(true);
			return field.get(this);
		}catch (Exception e) {
			logger.error("字段【"+field.getName()+"】获取失败", e);
		}finally{
			field.setAccessible(false);
		}
		
		return null;
	}
	
	public synchronized Object getTableName(){
		Object tableName = getClassInfo(this.getClass(), FIXED_DEFINITION_TABLE);
		try{
			if(PublicMethod.isEmptyStr(tableName)){
				Table t = this.getClass().getAnnotation(Table.class);
				Method met = t.annotationType().getDeclaredMethod(FIXED_DEFINITION_NAME);
				tableName =met.invoke(t).toString();
				setClassInfo(this.getClass(), FIXED_DEFINITION_TABLE,tableName);
			}
		}catch(Exception e){
			logger.error("查找数据表名称失败", e);
			return null;
		}
		return tableName;
	}
	
	public CacheVo getVo(){
		Object tableName = getTableName();
		try{
			List<Field>  list = getCacheColumn();
			@SuppressWarnings("unchecked")
    		Map<String,String> columnORM = (Map<String,String>)getClassInfo(this.getClass(), FIXED_DEFINITION_COLUMN_ORM);
			String idColumn = columnORM.get(getIdName().getName());
			Map<String,Object> mapVo =getJdbcDao().getVo(tableName.toString(), getIdKeyValue(), idColumn);
			if(mapVo != null && mapVo.size() > 0){
	        	//Field[] fields = this.getClass().getDeclaredFields();
				for (Field field : list) {
					if (field.getAnnotation(Column.class) != null) {
						if(mapVo.containsKey(columnORM.get(field.getName()))){
							setFieldValue(field,mapVo.get(columnORM.get(field.getName())));
							//this.getClass().getMethod(parSetName(field.getName())).invoke(this, new Object[]{field.getClass(),mapVo.get(columnName)});
						}
						
					}
				}
	        }
	        
	        return this;
		}catch(Exception e){
			logger.error("查找对象失败", e);
			return null;
		}
    }

	public void insertInNosql() {
		if (!PublicMethod.isEmptyStr(redisAPIName)) {
			String value = new Gson().toJson(this);
			new RedisAPI(redisAPIName).put(getCacheIDKey(), value,60*60*24*30);//保存一个月
		}
	}
	
	public CacheVo loadVo(){
		
		if (!PublicMethod.isEmptyStr(redisAPIName)) {
			String result = new RedisAPI(redisAPIName).get(getCacheIDKey());
			if(PublicMethod.isEmptyStr(result)){
				CacheVo vo = getVo();
				vo.insertInNosql();//插入缓存
				return vo;
			}else{
				return new Gson().fromJson(result, this.getClass());
			}
		}else{
			return getVo();
		}
	}
	
	public void deleteNoSql(){
		if (!PublicMethod.isEmptyStr(redisAPIName))
		new RedisAPI(redisAPIName).del(getCacheIDKey());
	}

	public String getCacheKey(String name, String value) {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(getTableName()).append(FIXED_DEFINITION_COLON).append(name).append(FIXED_DEFINITION_COLON).append(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private String getORMName(String name){
		char [] ss = name.toCharArray();
		StringBuilder sb = new StringBuilder();
		for(char c : ss){
			if(c >= 'A' && c <= 'Z'){
				sb.append("_").append(String.valueOf(c).toLowerCase());
			}else{
				sb.append(String.valueOf(c));
			}
		}
		return sb.toString();
	}
	
	private List<Field> getCacheColumn() {
		@SuppressWarnings("unchecked")
		List<Field> obj = (List<Field>) getClassInfo(this.getClass(), FIXED_DEFINITION_COLUMN);
		if(obj == null){
			Map<String,String> columnORM = new HashMap<String,String>();
			obj= new ArrayList<Field>();
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(Column.class) != null) {
					String columnName = field.getAnnotation(Column.class).name();
					if(PublicMethod.isEmptyStr(columnName)){
						columnName = getORMName(field.getName());
					}
					columnORM.put(field.getName(), columnName);
					obj.add(field);
				}
			}
			setClassInfo(this.getClass(),FIXED_DEFINITION_COLUMN_ORM,columnORM);
			setClassInfo(this.getClass(),FIXED_DEFINITION_COLUMN,obj);
			return obj;
		}
		return obj;
	}

	/**
	 * 获取实体类的缓存键值
	 * 
	 * @return
	 */
	public String getCacheIDKey() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(getTableName()).append(FIXED_DEFINITION_COLON).append(FIXED_DEFINITION_ID).append(FIXED_DEFINITION_COLON).append(getIdKeyValue());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取实体类的缓存键值异常", e);
		}
		return sb.toString();
	}

	/**
	 * 获取实体类的id值
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getIdKeyValue() throws Exception {
		
		//Method f = (Method) getClassInfo(this.getClass(), FIXED_DEFINITION_ID);
		//Field f = getIdName();
		/*
		Field f = (Field) getClassInfo(this.getClass(), FIXED_DEFINITION_ID);
		if (PublicMethod.isEmptyStr(f)) {
			Field[] fields = this.getClass().getDeclaredFields();
			// List<Field> result = new ArrayList<Field>();
			for (Field field : fields) {
				if (field.getAnnotation(Id.class) != null) {
					//f = this.getClass().getMethod(parGetName(field.getName()));
					f = field;
					setClassInfo(this.getClass(), FIXED_DEFINITION_ID, f);
					//setClassInfo(this.getClass(), "idField", field);
					break;
				}
			}
		}*/
		try {
			
			Object value = getFieldValue(getIdName());//f.invoke(this);
			if (PublicMethod.isEmptyStr(value)) {
				throw new Exception("主键值为空");
			}
			return value;
		} catch (Exception e) {
			logger.error("主键获取失败", e);
			throw new Exception("主键获取失败");
		}
	}

	/**
	 * 拼接某属性的 get方法
	 * 
	 * @param fieldName
	 * @return String
	 * /
	public String parGetName(String fieldName) {
		if (PublicMethod.isEmptyStr(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')startIndex = 1;
		return new StringBuilder("get").append(fieldName.substring(startIndex, startIndex + 1).toUpperCase()).append(fieldName.substring(startIndex + 1)).toString();
	}*/

	/**
	 * 拼接某属性的 set方法
	 * 
	 * @param fieldName
	 * @return String
	 * /
	public String parSetName(String fieldName) {
		if (PublicMethod.isEmptyStr(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')startIndex = 1;
		return new StringBuilder("set").append(fieldName.substring(startIndex, startIndex + 1).toUpperCase()).append(fieldName.substring(startIndex + 1)).toString();
	}*/

	private void setClassInfo(Class<?> c, String filed, Object value) {
		if (PublicMethod.isEmptyStr(c, filed, value))
			return;
		if (map.containsKey(c)) {
			Map<String, Object> m = map.get(c);
			m.put(filed, value);
		} else {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put(filed, value);
			map.put(c, m);
		}

	}

	private Object getClassInfo(Class<?> c, String filed) {
		if (PublicMethod.isEmptyStr(c, filed))
			return null;

		if (map.containsKey(c)) {
			return map.get(c).get(filed);
		} else {
			return null;
		}

	}
	
	private Field getIdName(){
		Field f = (Field) getClassInfo(this.getClass(), FIXED_DEFINITION_ID);
		if (PublicMethod.isEmptyStr(f)) {
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(Id.class) != null) {
					f = field;
					setClassInfo(this.getClass(), FIXED_DEFINITION_ID, f);
					String columnName = field.getAnnotation(Column.class).name();
					if(PublicMethod.isEmptyStr(columnName)){
						columnName = getORMName(field.getName());
					}
					setClassInfo(this.getClass(), FIXED_DEFINITION_ID_ORM, columnName);
					
					break;
				}
			}
		}
		
		return f;
	}
	
	private CacheVo setIDValue(String value){
		if(!PublicMethod.isEmptyStr(value)){
			Field f = getIdName();
			logger.error(value+"-KKKKKKKKKKKKKKKKKKKKK-"+f.getName());
			if(f.getType().getName().equals(Long.class.getName())){
				setFieldValue(f,Long.parseLong(value));
			}else if(f.getType().getName().equals(Integer.class.getName())){
				setFieldValue(f,Integer.parseInt(value));
			}else{
				setFieldValue(f,value);
			}
		}
		
		return this;
	}



	@Override
	public String toString() {
		List<Field>  list = getCacheColumn();

		@SuppressWarnings("unchecked")
		Map<String,String> columnORM = (Map<String,String>)getClassInfo(this.getClass(), FIXED_DEFINITION_COLUMN_ORM);
		JsonObject jo = new JsonObject();
		for(Field field : list){
			try {
				//Object value = this.getClass().getMethod(parGetName(field.getName())).invoke(this);
				Object value = getFieldValue(field);
				if(value  == null)
					value = "";
				
				if(field.getType().getName().equals(java.sql.Date.class.getName())){
					jo.addProperty(columnORM.get(field.getName()), PublicMethod.formatDateStr((java.sql.Date)value, FIXED_DEFINITION_TIME_FORMAT));
				} else if(field.getType().getName().equals(java.util.Date.class.getName())){
					jo.addProperty(columnORM.get(field.getName()), PublicMethod.formatDateStr((java.util.Date)value, FIXED_DEFINITION_TIME_FORMAT));
				}else {
					jo.addProperty(columnORM.get(field.getName()), String.valueOf(value));
				}
			} catch (Exception e) {
				logger.error(field.getName()+"转换错误", e);
				e.printStackTrace();
			} 
		}
		return jo.toString();
	}
	
	
	public void saveCustomCache(Map<String,Object> map,String fieldName,String fieldValue,String value){
		if (!PublicMethod.isEmptyStr(redisAPIName)) {
			getCustomCacheMap(map, fieldName);
			new RedisAPI(redisAPIName).hSet(customCacheKey(map), new String[]{fieldValue}, new String[]{value});
		}
	}
	
	private Map<String,String> getCustomCacheMap(Map<String,Object> map,String fieldName){
		if (!PublicMethod.isEmptyStr(redisAPIName)) {
			String key = customCacheKey(map);
			Map<String,String> map2 = new RedisAPI(redisAPIName).hgetAll(key);
			if(map2 == null || map2.size() == 0){
				StringBuilder sb = new StringBuilder("select * from ").append(getTableName());
				getSBSQL(sb,map);
				List<Map<String,Object>> list = getJdbcDao().getList(sb.toString());
				if(list != null && list.size() > 0){
					map2 = new HashMap<String,String>();
					int size = list.size();
					String[] fieldValues = new String[size];
					String[] values = new String[size];
					getIdName();
					String idORM = getClassInfo(this.getClass(), FIXED_DEFINITION_ID_ORM)+"";
					for(int i = 0;i<size;i++){
						Map<String,Object> map3 = list.get(i);
						if(map3.containsKey(idORM)){
							values[i]=map3.get(idORM).toString();
						}
						if(map3.containsKey(fieldName)){
							fieldValues[i] = map3.get(fieldName).toString();
						}
						map2.put(fieldValues[i], values[i]);
					}
					new RedisAPI(redisAPIName).hSet(key,fieldValues, values);
					new RedisAPI(redisAPIName).expire(key, 60*60*24*50);
					
				}
			}
			return map2;
		}else{
			return null;
		}
	}
	
	public List<?> getCustomCache(Map<String,Object> map,String fieldName){
		List<CacheVo> list = new ArrayList<CacheVo>();
		if (!PublicMethod.isEmptyStr(redisAPIName)) {
			Map<String,String> map2 = getCustomCacheMap(map,fieldName);
			if(map2 != null && map2.size() > 0){
				for(Map.Entry<String, String> kv : map2.entrySet()){
					try {
						CacheVo vo = this.getClass().getConstructor(String.class).newInstance(redisAPIName);
						vo = vo.setIDValue(kv.getValue()).loadVo();
						list.add(vo);
					} catch (Exception e) {
						logger.error(kv.getKey()+"转换对象失败="+kv.getValue(), e);
					}
				}
			}
		}
		return list;
	}
	
	
	public String getSBSQL(StringBuilder sb,Map<String,Object> map){
		StringBuilder sb1 = new StringBuilder();
		if(map != null && map.size() > 0){
			
			if(map.containsKey(FIXED_DEFINITION_FIELD_1)){
				if(map.containsKey(FIXED_DEFINITION_VALUE_1)){
					if(Format.isNumeric(map.get(FIXED_DEFINITION_VALUE_1).toString())){
						sb1.append(map.get(FIXED_DEFINITION_FIELD_1)).append("=").append(map.get(FIXED_DEFINITION_VALUE_1)).append("   ");
					}else{
						sb1.append(map.get(FIXED_DEFINITION_FIELD_1)).append("='").append(map.get(FIXED_DEFINITION_VALUE_1)).append("'   ");
					}
					
				}
				if(map.containsKey(FIXED_DEFINITION_FIELD_2)){
					if(map.containsKey(FIXED_DEFINITION_VALUE_2)){
						if(Format.isNumeric(map.get(FIXED_DEFINITION_VALUE_2).toString())){
							sb1.append(map.get(FIXED_DEFINITION_FIELD_2)).append("=").append(map.get(FIXED_DEFINITION_VALUE_2)).append("   ");
						}else{
							sb1.append(map.get(FIXED_DEFINITION_FIELD_2)).append("='").append(map.get(FIXED_DEFINITION_VALUE_2)).append("'   ");
						}
					}
					if(map.containsKey(FIXED_DEFINITION_FIELD_3)){
						if(map.containsKey(FIXED_DEFINITION_VALUE_3)){
							if(Format.isNumeric(map.get(FIXED_DEFINITION_VALUE_3).toString())){
								sb1.append(map.get(FIXED_DEFINITION_FIELD_3)).append("=").append(map.get(FIXED_DEFINITION_VALUE_3)).append("   ");
							}else{
								sb1.append(map.get(FIXED_DEFINITION_FIELD_3)).append("='").append(map.get(FIXED_DEFINITION_VALUE_3)).append("'   ");
							}
						}
						if(map.containsKey(FIXED_DEFINITION_FIELD_4)){
							if(map.containsKey(FIXED_DEFINITION_VALUE_4)){
								if(Format.isNumeric(map.get(FIXED_DEFINITION_VALUE_4).toString())){
									sb1.append(map.get(FIXED_DEFINITION_FIELD_4)).append("=").append(map.get(FIXED_DEFINITION_VALUE_4)).append("   ");
								}else{
									sb1.append(map.get(FIXED_DEFINITION_FIELD_4)).append("='").append(map.get(FIXED_DEFINITION_VALUE_4)).append("'   ");
								}
							}
							if(map.containsKey(FIXED_DEFINITION_FIELD_5)){
								if(map.containsKey(FIXED_DEFINITION_VALUE_5)){
									if(Format.isNumeric(map.get(FIXED_DEFINITION_VALUE_5).toString())){
										sb1.append(map.get(FIXED_DEFINITION_FIELD_5)).append("=").append(map.get(FIXED_DEFINITION_VALUE_5)).append("   ");
									}else{
										sb1.append(map.get(FIXED_DEFINITION_FIELD_5)).append("='").append(map.get(FIXED_DEFINITION_VALUE_5)).append("'   ");
									}
								}
							}
						}
					}
				}
			}
		}
		if(sb1.toString().length() > 0){
			sb.append(" where ").append(sb1.toString().replaceAll("      ", " and "));
		}
		return sb.toString();
		
	}
	
	
	public String customCacheKey(Map<String,Object> map){
		StringBuilder sb = new StringBuilder();
		if(map != null && map.size() > 0){
			Object tableName = getTableName();
			sb.append(tableName).append(FIXED_DEFINITION_COLON).append(FIXED_DEFINITION_CUSTOM).append(FIXED_DEFINITION_COLON);
			if(map.containsKey(FIXED_DEFINITION_FIELD_1)){
				sb.append(map.get(FIXED_DEFINITION_FIELD_1).toString()).append(FIXED_DEFINITION_COLON);
				if(map.containsKey(FIXED_DEFINITION_VALUE_1))
					sb.append(map.get(FIXED_DEFINITION_VALUE_1).toString()).append(FIXED_DEFINITION_COLON);
				if(map.containsKey(FIXED_DEFINITION_FIELD_2)){
					sb.append(map.get(FIXED_DEFINITION_FIELD_2).toString()).append(FIXED_DEFINITION_COLON);
					if(map.containsKey(FIXED_DEFINITION_VALUE_2))
						sb.append(map.get(FIXED_DEFINITION_VALUE_2).toString()).append(FIXED_DEFINITION_COLON);
					if(map.containsKey(FIXED_DEFINITION_FIELD_3)){
						sb.append(map.get(FIXED_DEFINITION_FIELD_3).toString()).append(FIXED_DEFINITION_COLON);
						if(map.containsKey(FIXED_DEFINITION_VALUE_3))
							sb.append(map.get(FIXED_DEFINITION_VALUE_3).toString()).append(FIXED_DEFINITION_COLON);
						if(map.containsKey(FIXED_DEFINITION_FIELD_4)){
							sb.append(map.get(FIXED_DEFINITION_FIELD_4).toString()).append(FIXED_DEFINITION_COLON);
							if(map.containsKey(FIXED_DEFINITION_VALUE_4))
								sb.append(map.get(FIXED_DEFINITION_VALUE_4).toString()).append(FIXED_DEFINITION_COLON);
							if(map.containsKey(FIXED_DEFINITION_FIELD_5)){
								sb.append(map.get(FIXED_DEFINITION_FIELD_5).toString()).append(FIXED_DEFINITION_COLON);
								if(map.containsKey(FIXED_DEFINITION_VALUE_5))
									sb.append(map.get(FIXED_DEFINITION_VALUE_5).toString()).append(FIXED_DEFINITION_COLON);
							}
						}
					}
				}
			}
		}
		
		return sb.toString();
		
	}
	
	

}