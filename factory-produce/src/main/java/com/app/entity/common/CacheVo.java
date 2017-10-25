/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
	
	private static final String FIXED_DEFINITION_TABLE_CACHE="table_cache";
	
	//private static final String FIXED_DEFINITION_NAME="name";
	
	private static final String FIXED_DEFINITION_CUSTOMCACHE="custom_cache_key";
	
	private static final String FIXED_DEFINITION_COLUMN_ORM="column_orm";
	
	private static final String FIXED_DEFINITION_ID="id";
	
	private static final String FIXED_DEFINITION_COLUMN="column";
	
	private static final String FIXED_DEFINITION_COLON=":";
	
	private static final String FIXED_DEFINITION_TIME_FORMAT="yyyy-MM-dd HH:mm:ss";
	
	private final static Map<Class<?>, Map<String, Object>> map = new HashMap<Class<?>, Map<String, Object>>();
	
	 
	private static ApplicationContext applicationContext = null;//启动类set入，调用下面set方法
	
	private String redisObj = null;
	
	private String daoName;

    public synchronized static void setApplicationContext(ApplicationContext context) {
    	if(applicationContext == null){
    		applicationContext = context;
    	}
    }
	
	public CacheVo() {
		super();
		this.daoName = "jdbcDao";
		this.redisObj = RedisAPI.REDIS_CORE_DATABASE;
	}
	
	public CacheVo(String redisObj,String daoSoure) {
		super();
		
		if(PublicMethod.isEmptyStr(redisObj)){
			this.redisObj = RedisAPI.REDIS_CORE_DATABASE;
		}else{
			this.redisObj = redisObj;
		}
		
		if(PublicMethod.isEmptyStr(daoSoure)){
			daoName = "jdbcDao";
		}else{
			daoName = daoSoure;
		}
	}

	public CacheVo(String redisObj) {
		super();
		this.daoName = "jdbcDao";
		if(PublicMethod.isEmptyStr(redisObj)){
			this.redisObj = RedisAPI.REDIS_CORE_DATABASE;
		}else{
			this.redisObj = redisObj;
		}
	}

	public synchronized JdbcDao getJdbcDao(){
		if(PublicMethod.isEmptyStr(daoName)) daoName = "jdbcDao";
		return (JdbcDao)applicationContext.getBean(daoName);
	}
	
	
	@SuppressWarnings("unchecked")
	private <T extends CacheVo> T newCacheVo(){
		T vo = null;
		try {
			
			vo = (T) this.getClass().getConstructor(String.class).newInstance(redisObj);
		} catch (Exception e) {
			logger.error("反映创建对象失败="+this.getClass(), e);
		} 
		return vo;
	}
	
	/**
	 * 对当前实体赋字段值
	 * @param field
	 * @param object
	 */
	public synchronized void setFieldValue(Field field,Object object){
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
	
	/**
	 * 获取当前实体的字段值
	 * @param field
	 * @return
	 */
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
	
	private synchronized void setClassInfo(Class<?> c, String filed, Object value) {
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

	private synchronized Object getClassInfo(Class<?> c, String filed) {
		if (PublicMethod.isEmptyStr(c, filed))
			return null;

		if (map.containsKey(c)) {
			return map.get(c).get(filed);
		} else {
			return null;
		}

	}
	
	/**
	 * 获取数据表的表名称
	 * @return String
	 */
	public synchronized Object getTableName(){
		Object tableName = getClassInfo(this.getClass(), FIXED_DEFINITION_TABLE);
		try{
			if(PublicMethod.isEmptyStr(tableName)){
				Table t = this.getClass().getAnnotation(Table.class);
				//Method met = t.annotationType().getDeclaredMethod(FIXED_DEFINITION_NAME);
				//tableName =met.invoke(t).toString();
				tableName = t.name();
				setClassInfo(this.getClass(), FIXED_DEFINITION_TABLE,t.name());//数据表名称
				TableCache tableCache = this.getClass().getAnnotation(TableCache.class);
				if(tableCache != null){
					setClassInfo(this.getClass(), FIXED_DEFINITION_TABLE_CACHE,tableCache.isCache());//是否使用缓存
				}else{
					setClassInfo(this.getClass(), FIXED_DEFINITION_TABLE_CACHE,false);//是否使用缓存
				}
				
			}
		}catch(Exception e){
			logger.error("查找数据表名称失败", e);
			return null;
		}
		return tableName;
	}
	
	
	/**
	 * 判断实体表是否使有缓存
	 * @return boolean 
	 */
	public synchronized boolean isChache(){
		Boolean object = (Boolean)getClassInfo(this.getClass(), FIXED_DEFINITION_TABLE_CACHE);
		try{
			if(PublicMethod.isEmptyStr(object)){
				TableCache tableCache = this.getClass().getAnnotation(TableCache.class);
				if(tableCache != null){
					object = tableCache.isCache();
					setClassInfo(this.getClass(), FIXED_DEFINITION_TABLE_CACHE,object);//是否使用缓存
				}else{
					setClassInfo(this.getClass(), FIXED_DEFINITION_TABLE_CACHE,false);//是否使用缓存
				}
				
			}
		}catch(Exception e){
			logger.error("查找数据表是否使用缓存失败", e);
			return false;
		}
		return object;
	}
	
	
	/** 
	 * 获取持久化字段字段
	 * @return List<Field>
	 */
	@SuppressWarnings("unchecked")
	private synchronized List<Field> getColumnField() {
		List<Field> obj = (List<Field>) getClassInfo(this.getClass(), FIXED_DEFINITION_COLUMN);
		if(obj == null){
			Map<String,String> columnORM = new HashMap<String,String>();
			obj= new ArrayList<Field>();
			Field[] fields = this.getClass().getDeclaredFields();
			Map<Integer,CustomCacheBean> customCacheMap = new HashMap<Integer,CustomCacheBean>();
			for (Field field : fields) {
				String columnName = "";
				if (field.getAnnotation(Column.class) != null) {
					columnName = field.getAnnotation(Column.class).name();
					if(PublicMethod.isEmptyStr(columnName)){
						columnName = getORMName(field.getName());
					}
					columnORM.put(field.getName(), columnName);//数据库字段名称
					obj.add(field);//属性字段
				}
				
				
				if (field.getAnnotation(Id.class) != null) {
					setClassInfo(this.getClass(), FIXED_DEFINITION_ID, field);//主键字段
				}
				
				
				
				if(field.getAnnotation(CustomCache.class) != null){//取自定义缓存key
					int gorup = field.getAnnotation(CustomCache.class).gorup();
					int sort = field.getAnnotation(CustomCache.class).sort();
					boolean hashKey = field.getAnnotation(CustomCache.class).hashKey();
					CustomCacheBean bean = null;
					if(customCacheMap.containsKey(gorup)){
						bean = customCacheMap.get(gorup);
					}else{
						bean = new CustomCacheBean(gorup);
					}
					bean.setMap(sort, field);
					if(hashKey){
						bean.setHashSetKey(field);
					}
					customCacheMap.put(gorup, bean);
				}
			}
			setClassInfo(this.getClass(),FIXED_DEFINITION_CUSTOMCACHE,customCacheMap);//取得
			setClassInfo(this.getClass(),FIXED_DEFINITION_COLUMN_ORM,columnORM);
			setClassInfo(this.getClass(),FIXED_DEFINITION_COLUMN,obj);
			return obj;
		}
		return obj;
	}
	
	/**
	 * 获取自定义的缓存组成字段数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private synchronized Map<Integer,CustomCacheBean> getCustomCacheMap() {
		Map<Integer,CustomCacheBean> obj = (Map<Integer,CustomCacheBean>) getClassInfo(this.getClass(), FIXED_DEFINITION_CUSTOMCACHE);
		if(obj == null){
			getColumnField();
			obj = (Map<Integer,CustomCacheBean>) getClassInfo(this.getClass(), FIXED_DEFINITION_CUSTOMCACHE);
		}
		
		return obj;
	}
	
	/**
	 * 获取数据库的字段名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private synchronized Map<String,String> getCustomORM() {
		Map<String,String> obj = (Map<String,String>) getClassInfo(this.getClass(), FIXED_DEFINITION_COLUMN_ORM);
		if(obj == null){
			getColumnField();
			obj = (Map<String,String>) getClassInfo(this.getClass(), FIXED_DEFINITION_COLUMN_ORM);
		}
		
		return obj;
	}
	
	
	/**
	 * 获取主键 字段
	 * @return Field
	 */
	private Field getPKField(){
		Field f = (Field) getClassInfo(this.getClass(), FIXED_DEFINITION_ID);
		if (PublicMethod.isEmptyStr(f)) {
			getColumnField();
			f = (Field) getClassInfo(this.getClass(), FIXED_DEFINITION_ID);
		}
		return f;
	}
	
	
	/**
	 * 赋值 主键
	 * @param value
	 * @return
	 */
	protected CacheVo setPKValue(String value){
		if(!PublicMethod.isEmptyStr(value)){
			Field f = getPKField();
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
	
	
	
	
	
	
	/**
	 * 获取实体类的 主键值
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getIdValue() throws Exception {
		
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
			
			Object value = getFieldValue(getPKField());//f.invoke(this);
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
	 * 获取实体类的缓存 键值
	 * 
	 * @return
	 */
	public String getEntityCacheKeyName() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(getTableName()).append(FIXED_DEFINITION_COLON).append(FIXED_DEFINITION_ID).append(FIXED_DEFINITION_COLON).append(getIdValue());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取实体类的缓存键值异常", e);
		}
		return sb.toString();
	}
	
	
	/**
	 * 工具方法
	 * 将方法名变为_的字段名 
	 * @param name
	 * @return 
	 */
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
	
	
	/**
	 * 根据pk从数据库获取数据
	 * @return
	 */
	public CacheVo getVoFromDB(){
		Object tableName = getTableName();
		try{
			List<Field>  list = getColumnField();
    		Map<String,String> columnORM = getCustomORM();
			Map<String,Object> mapVo =getJdbcDao().getVo(tableName.toString(), getIdValue(), columnORM.get(getPKField().getName()));
			if(mapVo != null && mapVo.size() > 0){
				for (Field field : list) {
					if(mapVo.containsKey(columnORM.get(field.getName()))){
						setFieldValue(field,mapVo.get(columnORM.get(field.getName())));
					}
				}
				return this;
	        }else{
	        	return null;
	        }
		}catch(Exception e){
			logger.error("查找数据库对象失败", e);
			return null;
		}
    }
	public <T extends CacheVo> List<T> getListVO(SQLWhere where){
		return getListVO(0 ,10000, where);
	}
	
	public <T extends CacheVo> List<T> getListVO(int page ,int row,SQLWhere where){
		row = row > 10000 || row <= 0 ? 10000:row;
		long count = page;//== 0 ? 0:(page-1)*row;
		StringBuilder sql = new StringBuilder("select * from ").append( getTableName().toString()).append(where.toString()).append(" limit ").append(count).append(",").append(row);
		logger.error(sql.toString());
		return mapperVO(getJdbcDao().getList(sql.toString()));
	}
	
	/**
	 * 查询 统计
	 * @param where
	 * @return
	 */
	public long getCount(SQLWhere where){
		Object tableName = getClassInfo(this.getClass(), FIXED_DEFINITION_TABLE);
		StringBuilder sql = new StringBuilder("select count(*) num from ").append(tableName.toString()).append(where.toString());
		List<Map<String, Object>>  list = getJdbcDao().getList(sql.toString());
		if(list != null && list.size() > 0){
			//Map<String, Object> map = list.get(0);
			return Long.parseLong(list.get(0).get("num").toString());
		}
		return 0;
	}
	
	private void setValueToVo(Field field,JsonObject jo,String name){
		String typeName = field.getType().getName();
		JsonElement je = jo.get(name);
		if(field.getType().equals(Integer.class) || typeName.equalsIgnoreCase("int")){
			if(!PublicMethod.isEmptyStr(je.getAsString()) && Format.isNumeric(je.getAsString())){
				setFieldValue(field,je.getAsInt());
			}
		}else if(field.getType().equals(Long.class) || typeName.equalsIgnoreCase("long")){
			if(!PublicMethod.isEmptyStr(je.getAsString()) &&  Format.isNumeric(je.getAsString())){
				setFieldValue(field,jo.get(name).getAsLong());
			}
		}else if(field.getType().equals(Boolean.class) || typeName.equalsIgnoreCase("boolean")){
			if(!PublicMethod.isEmptyStr(je.getAsString())){
				setFieldValue(field,je.getAsBoolean());
			}
			
		}else if(field.getType().equals(Double.class) || typeName.equalsIgnoreCase("double")){
			if(!PublicMethod.isEmptyStr(je.getAsString())){
				setFieldValue(field,je.getAsDouble());
			}
			
		}else if(field.getType().equals(Float.class) || typeName.equalsIgnoreCase("float")){
			if(!PublicMethod.isEmptyStr(je.getAsString())){
				setFieldValue(field,je.getAsFloat());
			}
			
		}else if(field.getType().equals(Character.class) || typeName.equalsIgnoreCase("char")){
			setFieldValue(field,je.getAsCharacter());
		}else if(field.getType().equals(java.util.Date.class)){
			java.util.Date date = null;
			if(!PublicMethod.isEmptyStr(je.getAsString())){
				if(jo.has(name+"FormatDate")){
					date = PublicMethod.stringToDate(je.getAsString(), jo.get(name+"FormatDate").getAsString());
				}else{
					date = PublicMethod.stringToDate(je.getAsString(),FIXED_DEFINITION_TIME_FORMAT);
				}
				setFieldValue(field,date);
			}
			
		}else if(field.getType().equals(java.sql.Date.class)){
			if(!PublicMethod.isEmptyStr(je.getAsString())){
				java.util.Date date = null;
				if(jo.has(name+"FormatDate")){
					date = PublicMethod.stringToDate(je.getAsString(), jo.get(name+"FormatDate").getAsString());
				}else{
					date = PublicMethod.stringToDate(je.getAsString(),FIXED_DEFINITION_TIME_FORMAT);
				}
				setFieldValue(field,new java.sql.Date(date.getTime()));
			}
		}else{
			setFieldValue(field,je.getAsString());
		}
	}
	
	/**
	 * 赋值
	 * @param jo
	 */
	private CacheVo setVo(JsonObject jo){
		List<Field> list =  getColumnField();
		Map<String,String>  map = getCustomORM();
		for(Field field : list){
			try{
				String name = "";
				if(jo.has(name = field.getName())){
					setValueToVo( field, jo, name);
				}else if(jo.has(name = map.get(field.getName()))){
					setValueToVo( field, jo, name);
				}
				
				
			}catch(Exception e){
				logger.error("赋值失败", e);
			}
		}
		return this;
	}
	
	
	
	/**
	 * 保存自定义缓存值 自定义key，id
	 */
	protected void saveCustomCacheValue(){
		if(isChache()){
			Map<Integer,CustomCacheBean> customCacheMap = getCustomCacheMap();
			if(customCacheMap != null ){
				for(Map.Entry<Integer, CustomCacheBean> mapBean : customCacheMap.entrySet()){
					String key = mapBean.getValue().toString(this);
					try {
						new RedisAPI(redisObj).hSet(key, new String[]{mapBean.getValue().getHashSetKey(this)}, new String[]{getIdValue().toString()});
						new RedisAPI(redisObj).expire(key, 60*60*24*30);
					} catch (Exception e) {
						logger.error("保存自定义缓存", e);
					}
				}
			}
		}
	}
	
	/**
	 * 将实体持久化到缓存
	 */
	public void insertNosql() {
		if (isChache()) {
			String value = new GsonBuilder().setDateFormat(FIXED_DEFINITION_TIME_FORMAT).create().toJson(this);
			new RedisAPI(redisObj).put(getEntityCacheKeyName(), value,60*60*24*30);//保存一个月
			//saveCustomCacheValue();//保存自定义缓存数据
		}
	}
	
	/**
	 * 加载单个实体对象，没有则返回null
	 * @return
	 */
	public CacheVo loadVo(){
		
		if (isChache()) {//使用缓存
			String result = new RedisAPI(redisObj).get(getEntityCacheKeyName());
			if(PublicMethod.isEmptyStr(result)){
				CacheVo vo = getVoFromDB();//返回的是this
				if(vo != null){
					vo.insertNosql();//插入缓存
					return vo;
				}else{
					return null;
				}
				
			}else{
				return setVo(new JsonParser().parse(result).getAsJsonObject());//返回的是this
				//return  new Gson().fromJson(result, this);
			}
		}else{//不使用缓存
			return getVoFromDB();//返回的是this
		}
	}
	
	/**
	 * json对象转实体
	 * @param jo
	 * @return
	 */
	public CacheVo parse(JsonObject jo){
		//JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
		String idName = getPKField().getName();
		if(jo.has(idName)  ){
			String idValue = jo.get(idName).getAsString();
			if(!PublicMethod.isEmptyStr(idValue) && Format.isNumeric(idValue)){
				setPKValue(jo.get(idName).getAsString());
				loadVo();
			}
		}
		setVo(jo);
		return this;
	}
	
	
	
	/**
	 * 将数据库的map对象转为实体对像
	 * @param listMap
	 * @return
	 */
	public <T extends CacheVo> List<T> mapperVO(List<Map<String,Object>> listMap){
		List<T> listVO = new ArrayList<T>();
		if(listMap != null && listMap.size() > 0){
			List<Field>  list = getColumnField();
			Map<String,String> columnORM = getCustomORM();
			for(Map<String,Object> mapVo : listMap){
				if(mapVo != null && mapVo.size() > 0){
					try {
						T vo = newCacheVo();
						for (Field field : list) {
							
							if(mapVo.containsKey(columnORM.get(field.getName()))){
								vo.setFieldValue(field,mapVo.get(columnORM.get(field.getName())));
							}else{
								vo.setFieldValue(field," ");
							}
						}
						listVO.add(vo);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("转换对象失败="+this.getClass(), e);
					}
		        }
			}
		}
		return listVO;
	}
	
	
	
	
	
	/**
	 * 删除自定义缓存值 自定义key，id
	 * /
	private void deleteCustomCacheValue(){
		if(isChache()){
			Map<Integer,CustomCacheBean> customCacheMap = getCustomCacheMap();
			if(customCacheMap != null ){
				for(Map.Entry<Integer, CustomCacheBean> mapBean : customCacheMap.entrySet()){
					String key = mapBean.getValue().toString(this);
					try {
						new RedisAPI(redisObj).hDel(key, mapBean.getValue().getHashSetKey(this));
					} catch (Exception e) {
						logger.error("删除自定义缓存", e);
					}
				}
			}
		}
	}*/
	
	/**
	 * 删除所有自定义缓存值
	 */
	private void deleteCustomCacheAll(){
		if(isChache()){
			Map<Integer,CustomCacheBean> customCacheMap = getCustomCacheMap();
			if(customCacheMap != null ){
				for(Map.Entry<Integer, CustomCacheBean> mapBean : customCacheMap.entrySet()){
					String key = mapBean.getValue().toString(this);
					try {
						new RedisAPI(redisObj).del(key);
					} catch (Exception e) {
						logger.error("删除自定义缓存", e);
					}
				}
			}
		}
	}
	
	
	
	/**
	 * 查找自定义缓存数据
	 * @param group 自定义缓存组
	 * @param value 对应的hashset的key
	 * @return
	 */
	public List<List<CacheVo>> queryAllCustomCacheValue(){
		List<List<CacheVo>> listVO = new ArrayList<List<CacheVo>>();
		Map<Integer,CustomCacheBean> customCacheMap = getCustomCacheMap();
		for(Map.Entry<Integer, CustomCacheBean> kv : customCacheMap.entrySet()){
			CustomCacheBean bean = kv.getValue();
			List<CacheVo> list = queryCustomCacheValue(kv.getKey(),bean.getHashSetKey(this));
			if(list != null){
				listVO.add(list);
			}
		}
		return listVO;
		
	}
	
	/**
	 * 查找自定义缓存数据
	 * @param group 自定义缓存组
	 * @param value 对应的hashset的key
	 * @return
	 */
	public <T extends CacheVo> List<T> queryCustomCacheValue(int group,String value){
		List<T> listVO = new ArrayList<T>();
		Map<Integer,CustomCacheBean> customCacheMap = getCustomCacheMap();
		if(customCacheMap.containsKey(group)){
			if (!isChache()){//是否存在缓存
				CustomCacheBean bean = customCacheMap.get(group);
				SQLWhere sqlWhere = new SQLWhere();
				Map<String,String> columnORM = getCustomORM();
				for(Field field : bean.getField())
					sqlWhere.and(new EQCnd(columnORM.get(field.getName()), getFieldValue(field)));
				List<T> list = getListVO(0,10000,sqlWhere);
				for(T vo : list){
					if(!PublicMethod.isEmptyStr(value)){
						if(bean.getHashSetKey(vo).toString().equals(value)){
							listVO.add(vo);
							break;
						}
					}else{
						listVO.add(vo);
					}
				}
				return listVO;
			}else{
				CustomCacheBean bean = customCacheMap.get(group);
				Map<String, String> mapValue = new RedisAPI(redisObj).hgetAll(bean.toString(this));
				if(mapValue == null || mapValue.size() == 0){
					SQLWhere sqlWhere = new SQLWhere();
					Map<String,String> columnORM = getCustomORM();
					for(Field field : bean.getField())
						sqlWhere.and(new EQCnd(columnORM.get(field.getName()), getFieldValue(field)));
					List<T> list = getListVO(0,10000,sqlWhere);
					for(T vo : list){
						vo.saveCustomCacheValue();//保存自定义缓存值
						if(!PublicMethod.isEmptyStr(value)){
							if(bean.getHashSetKey(vo).toString().equals(value)){
								listVO.add(vo);
							}
						}else{
							listVO.add(vo);
						}
					}
					return listVO;
				}else{
					if(PublicMethod.isEmptyStr(value)){
						for(Map.Entry<String, String> kv : mapValue.entrySet()){
							try {
								T vo = newCacheVo();
								vo.setPKValue(kv.getValue());
								vo.loadVo();
								listVO.add(vo);
							} catch (Exception e) {
								logger.error("查询自定义缓存", e);
								e.printStackTrace();
							}
						}
						return listVO;
					}else{
						if(mapValue.containsKey(value)){
							T vo = newCacheVo();
							vo.setPKValue(mapValue.get(value));
							vo.loadVo();
							listVO.add( vo);
							return listVO;
						}
					}
				}
			}
		}
		return null;
		
	}
	
	/**
	 * 查找自定义缓存数据
	 * @param group 自定义缓存组
	 * @param value 对应的hashset的key
	 * @return
	 */
	public <T extends CacheVo> T queryCustomCacheVo(int group,String value){
		List<T> list = queryCustomCacheValue( group, value);
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	/**
	 * 查找自定义缓存数据
	 * @param group 自定义缓存组
	 * @return
	 */
	public CacheVo queryCustomCacheVo(int group){
		return queryCustomCacheVo(group,null);
	}
	
	/**
	 * 查找自定义缓存数据
	 * @param value 对应的hashset的key
	 * @return
	 */
	public <T extends CacheVo> T queryCustomCacheVo(String value){
		return queryCustomCacheVo(0,value);
		
	}
	
	/**
	 * 查找自定义缓存数据
	 * @return
	 */
	public <T extends CacheVo> T queryCustomCacheVo(){
		return queryCustomCacheVo(null);
		
	}
	
	
	
	
	
	
	/**
	 * 删除缓存的实体数据
	 */
	public void deleteNoSql(){
		if (isChache()){
			try{
				CacheVo vo = newCacheVo();
				vo.setPKValue(getIdValue().toString());
				vo.loadVo();
				vo.deleteCustomCacheAll();
				//vo.deleteCustomCacheValue();//删除自定义
				new RedisAPI(redisObj).del(vo.getEntityCacheKeyName());//删除实体
			}catch(Exception e){
				logger.error("删除自定义缓存出错", e);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	

	

	

	

	
	
	
	
	



	@Override
	public String toString() {
		List<Field>  list = getColumnField();

		Map<String,String> columnORM = getCustomORM();
		JsonObject jo = new JsonObject();
		for(Field field : list){
			try {
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
	
	
	
	/**
	 * 删除自定义缓存
	 * @return
	 */
	public long insert() throws Exception{
		JsonObject jo = new JsonParser().parse(toString()).getAsJsonObject();
		CacheVo vo = newCacheVo();
		vo.parse(jo);
		List<List<CacheVo>> list = vo.queryAllCustomCacheValue();
		if(list.size() > 0){
			for(List<CacheVo> listVO : list){
				if(listVO != null && listVO.size() > 0){
					throw new Exception("存在相同的唯一值");
				}
			}
		}
		StringBuilder sql = new StringBuilder(" INSERT INTO ");
		sql.append(getTableName()).append(" ( ");
		List<Field> listField = getColumnField();
		int len = listField.size();
		List<Object> listParam = new ArrayList<Object>();
		Map<String,String> columnORM =getCustomORM();
		StringBuilder columnSql = new StringBuilder();
		StringBuilder signSql = new StringBuilder();
		boolean bool = false;
		Date date = new Date();
		for(int i = 0;i<len;i++){
			Field field = listField.get(i);
			String column = columnORM.get(field.getName());
			if(!field.getName().equals(getPKField().getName())){
				
				if(bool){
					columnSql.append(" , ").append(column);
					if(column.equals("create_time") || column.equals("operator_time")){
						signSql.append(" ,'").append(PublicMethod.formatDateStr(date, "yyyy-MM-dd HH:mm:ss")).append("'");
					}else{
						signSql.append(" ,").append(getColumnValue(field));
					}
					
				}else{
					bool = true;
					columnSql.append(column);
					if(column.equals("create_time") || column.equals("operator_time")){
						signSql.append("'").append(PublicMethod.formatDateStr(date, "yyyy-MM-dd HH:mm:ss")).append("'");
					}else{
						signSql.append(getColumnValue(field));
					}
				}
				
				if(column.equals("create_time") || column.equals("operator_time")){
					listParam.add(date);
				}else{
					listParam.add(getFieldValue(field));
				}
				
			}
		}
		sql.append(columnSql).append(" ) VALUES (").append(signSql).append(" )");
		logger.error("--------------insertSql="+sql.toString());
		long id = getJdbcDao().insert(sql.toString(), listParam.toArray());
		logger.error("---------------id="+id);
		setPKValue(String.valueOf(id));
		insertNosql();//保存缓存数据
		vo.deleteCustomCacheAll();//删除自定义缓存
		return id;
	}
	
	private String getColumnValue(Field field){
		String typeName = field.getType().getName();
		Object value = getFieldValue(field);
		if(field.getType().equals(Integer.class) || typeName.equalsIgnoreCase("int")){
			if(value == null){
				return "0";
			}else{
				return value+"";
			}
			
		}else if(field.getType().equals(Long.class) || typeName.equalsIgnoreCase("long")){
			if(value == null){
				return "0";
			}else{
				return value+"";
			}
		}else if(field.getType().equals(Boolean.class) || typeName.equalsIgnoreCase("boolean")){
			if(value == null){
				return "false";
			}else{
				return value+"";
			}
			
		}else if(field.getType().equals(Double.class) || typeName.equalsIgnoreCase("double")){
			if(value == null){
				return "0";
			}else{
				return value+"";
			}
			
		}else if(field.getType().equals(Float.class) || typeName.equalsIgnoreCase("float")){
			if(value == null){
				return "0";
			}else{
				return value+"";
			}
			
		}else if(field.getType().equals(Character.class) || typeName.equalsIgnoreCase("char")){
			if(value == null){
				return "''";
			}else{
				return "'"+value+"'";
			}
			
		}else if(field.getType().equals(java.util.Date.class)){
			if(value == null){
				return "''";
			}
			return "'"+PublicMethod.formatDateStr((java.util.Date)value, "yyyy-MM-dd HH:mm:ss")+"'";
			
		}else if(field.getType().equals(java.sql.Date.class)){
			if(value == null){
				return "''";
			}
			return "'"+PublicMethod.formatDateStr((java.sql.Date)value, "yyyy-MM-dd HH:mm:ss")+"'";
		}else{
			if(value == null){
				return "''";
			}
			return "'"+value+"'";
		}
	}
	
	/**
	 * 删除自定义缓存
	 * @return
	 */
	public int delete() throws Exception{
		loadVo();
		StringBuilder sql = new StringBuilder("delete FROM ").append(getTableName());
		sql.append(" where ").append(getCustomORM().get(getPKField().getName())).append(" = ").append(getIdValue().toString());
		int index = getJdbcDao().update(sql.toString(),null);
		deleteNoSql();
		deleteCustomCacheAll();//删除自定义缓存
		return index;
	}
	
	/**
	 * 删除，包括子记录
	 * @return
	 */
	public int  deleteLinkChild(String parentName) throws Exception{
		
		List<CacheVo> list = getListVO(0, 1000, new SQLWhere(new EQCnd(parentName, getIdValue())));
    	if(list != null && list.size() > 0){
    		for(CacheVo vo : list){
        		try {
        			StringBuilder sql = new StringBuilder("delete FROM ").append(getTableName());
        			sql.append(" where ").append(getCustomORM().get(getPKField().getName())).append(" = ").append(getIdValue().toString());
        			getJdbcDao().update(sql.toString(),null);
        			deleteNoSql();
        			deleteCustomCacheAll();//删除自定义缓存
        			return vo.deleteLinkChild(parentName);
    			} catch (Exception e) {
    				logger.error("删除关联应用失败", e);
    			}
        	}
    	}
		return delete();
	}
	
	
	
	
	
	/**
	 * 删除自定义缓存
	 * 更新数据
	 * 
	 */
	public int update(String ...fieldName) throws Exception{
		JsonObject jo = new JsonParser().parse(toString()).getAsJsonObject();
		CacheVo vo = newCacheVo();
		vo.parse(jo);
		List<List<CacheVo>> list = vo.queryAllCustomCacheValue();
		if(list.size() > 0){
			for(List<CacheVo> listVO : list){
				if(listVO != null ){
					if(listVO.size() == 1){
						if(!listVO.get(0).getIdValue().toString().equals(getIdValue().toString())){
							throw new Exception("存在相同的唯一值");
						}
					}if(listVO.size() > 1){
						throw new Exception("存在相同的唯一值");
					}
				}
			}
		}
		StringBuilder sql = new StringBuilder(" update ");
		sql.append(getTableName()).append(" set ");
		List<Field> listField = getColumnField();
		int len = listField.size();
		//Object [] param = new Object[len+1];
		List<Object> listParam = new ArrayList<Object>();
		Map<String,String> columnORM =getCustomORM();
		StringBuilder columnSql = new StringBuilder();
		boolean bool = false;
		if(fieldName != null && fieldName.length > 0){
			List<String> fieldList = Arrays.asList(fieldName);
			for(int i = 0;i<len;i++){
				Field field = listField.get(i);
				String column = columnORM.get(field.getName());
				if(fieldList.contains(column) || fieldList.contains(field.getName())){
					if(bool){
						columnSql.append(",").append(column).append(" = ? ");
					}else{
						bool = true;
						columnSql.append(column).append(" = ? ");
					}
					//param[i] = getFieldValue(field);
					listParam.add(getFieldValue(field));
				}
			}
		}else{
			for(int i = 0;i<len;i++){
				Field field = listField.get(i);
				String column = columnORM.get(field.getName());
				if(column.equals("create_time")){
					continue;
				}else if(column.equals("operator_time")){
					if(bool){
						columnSql.append(",").append(column).append(" = ? ");
					}else{
						bool = true;
						columnSql.append(column).append(" = ? ");
					}
					listParam.add(new Date());
				}else{
					if(bool){
						columnSql.append(",").append(column).append(" = ? ");
					}else{
						bool = true;
						columnSql.append(column).append(" = ? ");
					}
					listParam.add(getFieldValue(field));
					//param[i] = getFieldValue(field);
				}
			}
			sql.append(columnSql);
		}
		sql.append( " where ").append(columnORM.get(getPKField().getName())).append(" = ?");
		//param[len] = getFieldValue(getPKField());
		listParam.add(getFieldValue(getPKField()));
		int index = getJdbcDao().update(sql.toString(), listParam.toArray());
		insertNosql();//保存缓存数据
		//deleteCustomCacheAll();//删除自定义缓存
		vo.deleteCustomCacheAll();
		return index;
	}

}