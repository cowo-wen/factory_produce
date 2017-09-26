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

/**
 * 功能说明：缓存表
 * 
 * @author chenwen 2017-8-11
 */
@Service
public class CacheVo {
	public static Log logger = LogFactory.getLog(CacheVo.class);
	
	private final static Map<Class<?>, Map<String, Object>> map = new HashMap<Class<?>, Map<String, Object>>();
	
	 
	private static ApplicationContext applicationContext;//启动类set入，调用下面set方法

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }
	 
	
	
	public CacheVo() {
		super();
	}

	public CacheVo(String redisAPIName) {
		super();
		RedisAPIName = redisAPIName;
	}

	
	private String RedisAPIName = null;
	
	
	public CacheVo getVo(){
		Object tableName = getClassInfo(this.getClass(), "table");
		try{
			if(PublicMethod.isEmptyStr(tableName)){
				Table t = this.getClass().getAnnotation(Table.class);
				Method met = t.annotationType().getDeclaredMethod("name");
				tableName =met.invoke(t).toString();
				setClassInfo(this.getClass(), "table",tableName);
			}
			JdbcDao jdbcDao = null;
			synchronized (applicationContext) {
				jdbcDao = (JdbcDao)applicationContext.getBean("jdbcDao");
			}
			Map<String,Object> mapVo =jdbcDao.getVo(tableName.toString(), getIdKeyValue(), ((Field) getClassInfo(this.getClass(), "idField")).getAnnotation(Column.class).name());
			if(mapVo != null && mapVo.size() > 0){
	        	Field[] fields = this.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.getAnnotation(Column.class) != null) {
						try {
							String columnName = field.getAnnotation(Column.class).annotationType().getDeclaredMethod("name").toString();
							if(mapVo.containsKey(columnName)){
								this.getClass().getMethod(parSetName(field.getName())).invoke(this, new Object[]{field.getClass(),mapVo.get(columnName)});
							}
						} catch (Exception e) {
							logger.error("字段【"+field.getName()+"】赋值失败", e);
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
		if (!PublicMethod.isEmptyStr(RedisAPIName)) {
			String value = new Gson().toJson(this);
			new RedisAPI(RedisAPIName).put(getCacheIDKey(), value,60*60*24*30);//保存一个月
		}
	}
	
	public CacheVo loadVo(){
		if (PublicMethod.isEmptyStr(RedisAPIName)) {
			String value = new RedisAPI(RedisAPIName).get(getCacheIDKey());
			if(PublicMethod.isEmptyStr(value)){
				CacheVo vo = getVo();
				vo.insertInNosql();//插入缓存
				return vo;
			}else{
				return new Gson().fromJson(value, this.getClass());
			}
		}else{
			return getVo();
		}
	}
	
	public void deleteNoSql(){
		if (!PublicMethod.isEmptyStr(RedisAPIName))
		new RedisAPI(RedisAPIName).del(getCacheIDKey());
	}

	public String getCacheKey(String name, String value) {
		StringBuffer sb = new StringBuffer();
		try {
			Object tableName = getClassInfo(this.getClass(), "table");
			if(PublicMethod.isEmptyStr(tableName)){
				Table t = this.getClass().getAnnotation(Table.class);
				Method met = t.annotationType().getDeclaredMethod("name");
				tableName =met.invoke(t).toString();
				setClassInfo(this.getClass(), "table",tableName.toString());
			}
			sb.append(tableName).append(":").append(name).append(":").append(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public List<Field> getCacheColumn() {
		@SuppressWarnings("unchecked")
		List<Field> obj = (List<Field>) getClassInfo(this.getClass(), "column");
		if(obj == null){
			obj= new ArrayList<Field>();
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(Id.class) != null || field.getAnnotation(Column.class) != null) {
					obj.add(field);
				}
			}
			setClassInfo(this.getClass(),"column",obj);
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
			Table t = this.getClass().getAnnotation(Table.class);
			Method met = t.annotationType().getDeclaredMethod("name");
			sb.append(met.invoke(t)).append(":id:").append(getIdKeyValue());
		} catch (Exception e) {
			e.printStackTrace();
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
		Method f = (Method) getClassInfo(this.getClass(), "id");
		if (PublicMethod.isEmptyStr(f)) {
			Field[] fields = this.getClass().getDeclaredFields();
			// List<Field> result = new ArrayList<Field>();
			for (Field field : fields) {
				if (field.getAnnotation(Id.class) != null) {
					f = this.getClass().getMethod(parGetName(field.getName()));
					setClassInfo(this.getClass(), "id", f);
					setClassInfo(this.getClass(), "idField", field);
					break;
				}
			}
		}
		try {
			Object value = f.invoke(this);
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
	 */
	public String parGetName(String fieldName) {
		if (PublicMethod.isEmptyStr(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')startIndex = 1;
		return new StringBuilder("get").append(fieldName.substring(startIndex, startIndex + 1).toUpperCase()).append(fieldName.substring(startIndex + 1)).toString();
	}

	/**
	 * 拼接某属性的 set方法
	 * 
	 * @param fieldName
	 * @return String
	 */
	public String parSetName(String fieldName) {
		if (PublicMethod.isEmptyStr(fieldName)) {
			return null;
		}
		int startIndex = 0;
		if (fieldName.charAt(0) == '_')startIndex = 1;
		return new StringBuilder("set").append(fieldName.substring(startIndex, startIndex + 1).toUpperCase()).append(fieldName.substring(startIndex + 1)).toString();
	}

	public void setClassInfo(Class<?> c, String filed, Object value) {
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

}