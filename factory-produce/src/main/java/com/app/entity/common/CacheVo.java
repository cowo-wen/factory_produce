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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.app.dao.JdbcDao;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.xx.util.string.Format;

/**
 * 功能说明：缓存表
 * 
 * @author chenwen 2017-8-11
 */
@Service
public class CacheVo implements ApplicationContextAware{
	public static Log logger = LogFactory.getLog(CacheVo.class);
	
	private static final String ID="id";
	
	private static final String ZERO="0";
	
	private static final String FIXED_DEFINITION_TABLE="table";
	
	private static final String FIXED_DEFINITION_TABLE_CACHE="table_cache";
	
	private static final String FIXED_DEFINITION_CUSTOMCACHE="custom_cache_key";
	
	private static final String FIXED_DEFINITION_COLUMN_ORM="column_orm";
	
	private static final String FIXED_DEFINITION_OUTPUT="output";
	
	private static final String FIXED_DEFINITION_ID=ID;
	
	private static final String FIXED_DEFINITION_COLUMN="column";
	
	private static final String FIXED_DEFINITION_COLON=":";
	
	private static final String FIXED_DEFINITION_TIME_FORMAT="yyyy-MM-dd HH:mm:ss";
	
	private final static Map<Class<?>, Map<String, Object>> map = new HashMap<Class<?>, Map<String, Object>>();
	
	private transient Set<String> outPutSetOther = null;//显示其他的字段
	
	private transient Set<String> outPutIgnoreSet = null;//忽略显示的字段
	
	private transient Set<String> outPutFieldSet = null;//显示的字段
	
	 
	private static ApplicationContext applicationContext = null;//启动类set入，调用下面set方法
	
	//@Expose(deserialize=false,serialize=false)
	protected transient  String redisObj = null;
	
	//@Expose(deserialize=false,serialize=false)
	
	private transient  String[] outPutFieldsOther;
	
	protected transient JdbcDao jdbcDao;
	
	
	
	public static final String CREATE_TIME = "create_time";
	
	public static final String OPERATOR_TIME = "operator_time";
	
	
	
	private transient int constructionType = 0;//构造方法类型
	

	

	public synchronized void setApplicationContext(ApplicationContext context) {
    	if(applicationContext == null){
    		applicationContext = context;
    	}
    }
	
	public CacheVo() {
		super();
		this.redisObj = RedisAPI.REDIS_CORE_DATABASE;
	}
	
	public CacheVo(String redisObj) {
		super();
		if(PublicMethod.isEmptyStr(redisObj)){
			this.redisObj = RedisAPI.REDIS_CORE_DATABASE;
		}else{
			this.redisObj = redisObj;
		}
		constructionType = 1;
	}
	
	public CacheVo(JdbcDao jdbcDao) {
		super();
		this.jdbcDao = jdbcDao;
		this.redisObj = RedisAPI.REDIS_CORE_DATABASE;
		constructionType = 2;
	}
	
	public CacheVo(String redisObj,JdbcDao jdbcDao) {
		super();
		
		if(PublicMethod.isEmptyStr(redisObj)){
			this.redisObj = RedisAPI.REDIS_CORE_DATABASE;
		}else{
			this.redisObj = redisObj;
		}
		this.jdbcDao = jdbcDao;
		constructionType = 3;
	}

	
	
	/**
	 * 使用事务
	 * 调用该方法时，处理结束一定要调提交或回滚事务
	 */
	public void useTransaction() {
		getJdbcDao().useTransaction();
	}
	
	/**
	 * 提交事务
	 * @param status
	 */
	public void commit(){
		if(jdbcDao != null){
			jdbcDao.commit();
		}
	}
	
	/**
	 * 回滚事务
	 * @param status
	 */
	public void rollback(){
		if(jdbcDao != null){
			jdbcDao.rollback();
		}
	}

	//public void setJdbcDao(JdbcDao jdbcDao) {
	//	this.jdbcDao = jdbcDao;
	//}
	
	private synchronized JdbcDao getJdbcDao(){
		if(jdbcDao == null){
			jdbcDao = (JdbcDao)applicationContext.getBean("jdbcDao");
		}
		return jdbcDao;
	}
	
	
	@SuppressWarnings("unchecked")
	private <T extends CacheVo> T newCacheVo(){
		T vo = null;
		try {
			if(constructionType == 0){
				vo = (T) this.getClass().newInstance();
			}else if(constructionType == 1){
				vo = (T) this.getClass().getConstructor(String.class).newInstance(redisObj);
			}else if(constructionType == 2){
				vo = (T) this.getClass().getConstructor(JdbcDao.class).newInstance(jdbcDao);
			}else if(constructionType == 3){
				vo = (T) this.getClass().getConstructor(String.class,JdbcDao.class).newInstance(redisObj,jdbcDao);
			}
			vo.outPutOther(outPutSetOther);
			vo.outPutField(outPutFieldSet);
			vo.outPutIgnore(outPutIgnoreSet);
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
			
			Map<Field,String> outPutMap = new HashMap<Field,String>();
			for (Field field : fields) {
				String columnName = "";
				if (field.getAnnotation(Column.class) != null) {
					columnName = field.getAnnotation(Column.class).name();
					if(PublicMethod.isEmptyStr(columnName)){
						columnName = getORMName(field.getName());
					}
					columnORM.put(field.getName(), columnName);//数据库字段名称
					obj.add(field);//属性字段
				}else{
					if (field.getAnnotation(Expose.class) != null){
						boolean bool = field.getAnnotation(Expose.class).deserialize();
						if(bool){
							String propertyName = field.getName();
							String methodEnd = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
							outPutMap.put(field, "get" + methodEnd);
						}
					}
					
					continue;
				}
				
				
				if (field.getAnnotation(Id.class) != null) {
					setClassInfo(this.getClass(), FIXED_DEFINITION_ID, field);//主键字段
				}
				
				
				
				if(field.getAnnotation(CustomCache.class) != null){//取自定义缓存key
					CustomCache cc = field.getAnnotation(CustomCache.class);
					int[] gorups = cc.gorup();
					if(gorups != null && gorups.length > 0){
						for(int i = 0,len = gorups.length;i<len;i++){
							int gorup = gorups[i];
							int sort = cc.sort()[0];
							if(cc.sort().length > i)sort = cc.sort()[i];
							boolean hashKey = cc.hashKey()[0];
							if(cc.hashKey().length > i)hashKey = cc.hashKey()[i];
							CustomCacheBean bean = null;
							if(customCacheMap.containsKey(gorup)){
								bean = customCacheMap.get(gorup);
							}else{
								bean = new CustomCacheBean(gorup);
							}
							
							if(hashKey){
								bean.setHashSetKey(field);
							}else{
								bean.setMap(sort, field);
							}
							customCacheMap.put(gorup, bean);
						}
						
					}
					
				}
			}
			setClassInfo(this.getClass(),FIXED_DEFINITION_OUTPUT,outPutMap);
			setClassInfo(this.getClass(),FIXED_DEFINITION_CUSTOMCACHE,customCacheMap);//取得
			setClassInfo(this.getClass(),FIXED_DEFINITION_COLUMN_ORM,columnORM);
			setClassInfo(this.getClass(),FIXED_DEFINITION_COLUMN,obj);
			return obj;
		}
		return obj;
	}
	
	/**
	 * 获取自定义输出数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private synchronized Map<Field,String> getOutPutMap() {
		Map<Field,String> obj = (Map<Field,String>) getClassInfo(this.getClass(), FIXED_DEFINITION_OUTPUT);
		if(obj == null){
			getColumnField();
			obj = (Map<Field,String>) getClassInfo(this.getClass(), FIXED_DEFINITION_OUTPUT);
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
	
	public List<Map<String, Object>> getListVO(String sql,int page ,int row,SQLWhere where){
		row = row > 10000 || row <= 0 ? 10000:row;
		long count = page;
		StringBuilder querySql = new StringBuilder("select * from (").append(sql).append(") as t ").append(where.toString()).append(" limit ").append(count).append(",").append(row);
		logger.error(querySql.toString());
		return getJdbcDao().getList(querySql.toString());
	}
	
	public List<Map<String, Object>> getListMap(String sql){
		return getJdbcDao().getList(sql);
	}
	
	/**
	 * 查询 统计
	 * @param where
	 * @return
	 */
	public long getCount(SQLWhere where){
		Object tableName = getTableName();
		StringBuilder sql = new StringBuilder("select count(*) num from ").append(tableName.toString()).append(where.toString());
		List<Map<String, Object>>  list = getJdbcDao().getList(sql.toString());
		if(list != null && list.size() > 0){
			return Long.parseLong(list.get(0).get("num").toString());
		}
		return 0;
	}
	
	public long getCount(String sql , SQLWhere where){
		//Object tableName = getClassInfo(this.getClass(), FIXED_DEFINITION_TABLE);
		StringBuilder querySql = new StringBuilder("select count(*) num from (").append(sql).append(" ) as t").append(where.toString());
		logger.error(querySql.toString());
		List<Map<String, Object>>  list = getJdbcDao().getList(querySql.toString());
		if(list != null && list.size() > 0){
			return Long.parseLong(list.get(0).get("num").toString());
		}
		return 0;
	}
	
	private void setValueToVo(Field field,JsonObject jo,String name){
		String typeName = field.getType().getName();
		JsonElement je = jo.get(name);
		if(field.getType().equals(Integer.class) || typeName.equalsIgnoreCase("int")){
			if(!PublicMethod.isEmptyStr(je.getAsString()) ){
				String value = je.getAsString();
				if(value.startsWith("-")){
					if(Format.isNumeric(value.substring(1))){
						setFieldValue(field,je.getAsInt());
					}
				}else{
					if(Format.isNumeric(value)){
						setFieldValue(field,je.getAsInt());
					}
				}
			}
		}else if(field.getType().equals(Long.class) || typeName.equalsIgnoreCase("long")){
			String value = je.getAsString();
			if(value.startsWith("-")){
				if(Format.isNumeric(value.substring(1))){
					setFieldValue(field,jo.get(name).getAsLong());
				}
			}else{
				if(Format.isNumeric(value)){
					setFieldValue(field,jo.get(name).getAsLong());
				}
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
				if(jo.has(name+"_format_date")){
					date = PublicMethod.stringToDate(je.getAsString(), jo.get(name+"_format_date").getAsString());
				}else{
					date = PublicMethod.stringToDate(je.getAsString(),FIXED_DEFINITION_TIME_FORMAT);
				}
				setFieldValue(field,date);
			}
			
		}else if(field.getType().equals(java.sql.Date.class)){
			if(!PublicMethod.isEmptyStr(je.getAsString())){
				java.util.Date date = null;
				if(jo.has(name+"_format_date")){
					date = PublicMethod.stringToDate(je.getAsString(), jo.get(name+"_format_date").getAsString());
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
		//Date date = PublicMethod.stringToDate("1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
		for(Field field : list){
			try{
				String name = "";
				if(jo.has(name = field.getName())){
					setValueToVo( field, jo, name);
				}else if(jo.has(name = map.get(field.getName()))){
					setValueToVo( field, jo, name);
				}else{
					//if(field.getType().equals(Date.class)){
					//	setFieldValue(field,date);
					//}
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
	protected void saveCustomCacheValue(int type){
		if(isChache()){
			Map<Integer,CustomCacheBean> customCacheMap = getCustomCacheMap();
			if(customCacheMap != null ){
				if(type == 0){
					for(Map.Entry<Integer, CustomCacheBean> mapBean : customCacheMap.entrySet()){
						String key = mapBean.getValue().toString(this);
						try {
							new RedisAPI(redisObj).hSet(key, new String[]{mapBean.getValue().getHashSetKey(this)}, new String[]{ZERO});
							new RedisAPI(redisObj).expire(key, 60*60*24*30);
						} catch (Exception e) {
							logger.error("保存自定义缓存", e);
						}
					}
				}else{
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
	}
	
	/**
	 * 将实体持久化到缓存
	 */
	public void insertNosql(int type) {
		if (isChache()) {
			String value = ZERO;
			if(type != 0){
				logger.error("插入缓存数据:"+toString());
				//value = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setDateFormat(FIXED_DEFINITION_TIME_FORMAT).create().toJson(this);
				value = toString();
			}
			new RedisAPI(redisObj).put(getEntityCacheKeyName(), value,60*60*24*30);//保存一个月
			//saveCustomCacheValue();//保存自定义缓存数据
		}
	}
	
	/**
	 * 加载单个实体对象，没有则返回null
	 * @return
	 */
	public CacheVo loadVo(){
		CacheVo vo = null;
		try{
			if (isChache()) {//使用缓存
				String result = new RedisAPI(redisObj).get(getEntityCacheKeyName());
				if(PublicMethod.isEmptyStr(result)){
					vo = getVoFromDB();//返回的是this
					if(vo != null){
						vo.insertNosql(1);//插入缓存
						return vo;
					}else{
						insertNosql(0);//插入缓存
						return null;
					}
					
				}else{
					if(result.equals(ZERO)){
						return null;
					}else{
						return vo = setVo(new JsonParser().parse(result).getAsJsonObject());//返回的是this
					}
				}
			}else{//不使用缓存
				return vo = getVoFromDB();//返回的是this
			}
		}finally{
			if(vo != null){
				Map<Field,String> out = vo.getOutPutMap();
				if(vo.outPutSetOther != null && vo.outPutSetOther.size() > 0 && out != null && out.size() > 0){
					for(Map.Entry<Field, String> kv : out.entrySet()){
						if(outPutSetOther.contains(kv.getKey().getName()) || vo.outPutSetOther.contains(getORMName(kv.getKey().getName()))){
							try {
								getClass().getMethod(kv.getValue()).invoke(this);
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("输出其他字段", e);
							}
						}
					}
					
				}
			}
			
		}
		
		
	}
	
	/**
	 * json对象转实体
	 * @param jo
	 * @return
	 */
	public CacheVo parse(JsonObject jo){
		//JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
		return parse( jo, 0);
	}
	
	/**
	 * json对象转实体
	 * @param jo
	 * @return
	 */
	public CacheVo parse(JsonObject jo,int type){
		//JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
		if(type==0){
			String idName = getPKField().getName();
			if(jo.has(idName)){
				String idValue = jo.get(idName).getAsString();
				if(!PublicMethod.isEmptyStr(idValue) && Format.isNumeric(idValue)){
					setPKValue(idValue);
					loadVo();
				}
			}else{
				Map<String,String> map =getCustomORM();
				if(map.containsKey(idName)){
					if(jo.has(map.get(idName))){
						String idValue = jo.get(map.get(idName)).getAsString();
						if(!PublicMethod.isEmptyStr(idValue) && Format.isNumeric(idValue)){
							setPKValue(idValue);
							loadVo();
						}
					}
				}
			}
		}
		
		//logger.error("------修改前："+toString());
		setVo(jo);
		//logger.error("------修改后："+toString());
		return this;
	}
	
	
	/**
	 * 输出其他字段
	 * @param fieldName
	 */
	public void outPutOther(String ...fieldName){
		if(fieldName != null && fieldName.length > 0){
			this.outPutFieldsOther = fieldName;
			if(outPutSetOther == null){
				outPutSetOther = new HashSet<String>();
			}
			for(String name : fieldName){
				if(!PublicMethod.isEmptyStr(name)){
					outPutSetOther.add(name);
				}
				
			}
		}
		
	}
	
	/**
	 * 输出其他字段
	 * @param fieldName
	 */
	protected void outPutOther(Set<String> outPutSetOther){
		this.outPutSetOther = outPutSetOther;
		
	}
	
	/**
	 * 忽略输出字段，如果存在输出常规字段，即该方法无效
	 * @param fieldName
	 */
	public void outPutIgnore(String ...fieldName){
		if(fieldName != null && fieldName.length > 0){
			if(outPutIgnoreSet == null){
				outPutIgnoreSet = new HashSet<String>();
			}
			for(String name : fieldName){
				if(!PublicMethod.isEmptyStr(name)){
					outPutIgnoreSet.add(name);
				}
				
			}
		}		
	}
	
	/**
	 * 忽略输出字段，如果存在输出常规字段，即该方法无效
	 * @param fieldName
	 */
	protected void outPutIgnore(Set<String> outPutIgnoreSet){
		this.outPutIgnoreSet = outPutIgnoreSet;
		
	}
	/**
	 * 输出常规字段，该显示方式会导致忽略输出字段无效
	 * @param fieldName
	 */
	public void outPutField(String ...fieldName){
		if(fieldName != null && fieldName.length > 0){
			if(outPutFieldSet == null){
				outPutFieldSet = new HashSet<String>();
			}
			for(String name : fieldName){
				if(!PublicMethod.isEmptyStr(name)){
					outPutFieldSet.add(name);
				}
				
			}
		}
		
		
		
	}
	
	/**
	 * 输出常规字段，该显示方式会导致忽略输出字段无效
	 * @param fieldName
	 */
	protected void outPutField(Set<String> outPutFieldSet){
		this.outPutFieldSet = outPutFieldSet;
		
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
			Map<Field,String> out = getOutPutMap();
			//Date date = new Date();
			for(Map<String,Object> mapVo : listMap){
				if(mapVo != null && mapVo.size() > 0){
					try {
						T vo = newCacheVo();
						vo.outPutOther(this.outPutFieldsOther);
						for (Field field : list) {
							
							
							/*
							if(field.getType().equals(java.util.Date.class)){
								if(mapVo.containsKey(columnORM.get(field.getName()))){
									Object obj = mapVo.get(columnORM.get(field.getName()));
									if(obj == null){
										vo.setFieldValue(field,date);
									}else{
										vo.setFieldValue(field,obj);
									}
									
								}
							}*/
							
							
							
							if(outPutFieldSet != null && outPutFieldSet.size() > 0){
								if(!outPutFieldSet.contains(field.getName()) && !outPutFieldSet.contains(columnORM.get(field.getName()))){
									continue;
								}
							}
							
							if(outPutIgnoreSet != null && outPutIgnoreSet.size() > 0){//忽略输出的字段
								if(outPutIgnoreSet.contains(field.getName()) || outPutIgnoreSet.contains(columnORM.get(field.getName()))){
									continue;
								}
							}
							
							//if(!field.getType().equals(java.util.Date.class)){
								if(mapVo.containsKey(columnORM.get(field.getName()))){
									Object obj = mapVo.get(columnORM.get(field.getName()));
									if(obj != null){
										vo.setFieldValue(field,obj);
									}
									
								}
							//}
							
						}
						
						if(outPutSetOther != null && outPutSetOther.size() > 0 && out != null && out.size() > 0){
							for(Map.Entry<Field, String> kv : out.entrySet()){
								if(outPutSetOther.contains(kv.getKey().getName()) || outPutSetOther.contains(getORMName(kv.getKey().getName()))){
									vo.getClass().getMethod(kv.getValue()).invoke(vo);
								}
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
	public <T extends CacheVo> List<List<T>> queryAllCustomCacheValue(){
		List<List<T>> listVO = new ArrayList<List<T>>();
		Map<Integer,CustomCacheBean> customCacheMap = getCustomCacheMap();
		for(Map.Entry<Integer, CustomCacheBean> kv : customCacheMap.entrySet()){
			CustomCacheBean bean = kv.getValue();
			List<T> list = queryCustomCacheValue(kv.getKey(),bean.getHashSetKey(this));
			if(list != null){
				listVO.add(list);
			}
		}
		return listVO;
		
	}
	
	/**
	 * 查找自定义缓存数据
	 * @param group 自定义缓存组
	 * @return
	 */
	public <T extends CacheVo> List<T> queryCustomCacheValue(int group){
		return queryCustomCacheValue(group,null);
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
					if(list != null && list.size() > 0){
						for(T vo : list){
							vo.saveCustomCacheValue(1);//保存自定义缓存值
							if(!PublicMethod.isEmptyStr(value)){
								if(bean.getHashSetKey(vo).toString().equals(value)){
									listVO.add(vo);
								}
							}else{
								listVO.add(vo);
							}
						}
					}else{
						new RedisAPI(redisObj).hSet(bean.toString(this), new String[]{ID}, new String[]{ZERO});
						new RedisAPI(redisObj).expire(bean.toString(this), 60*60*24*30);
					}
					
					return listVO;
				}else{
					if(mapValue.containsKey(ID) && mapValue.get(ID).equals(ZERO)){
						return listVO;
					}
					if(PublicMethod.isEmptyStr(value)){
						for(Map.Entry<String, String> kv : mapValue.entrySet()){
							try {
								T vo = newCacheVo();
								vo.setPKValue(kv.getValue());
								vo.loadVo();
								if(vo != null){
									listVO.add(vo);
								}
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
							if(vo != null){
								listVO.add(vo);
							}
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
	public <T extends CacheVo> T queryCustomCacheVo(int group){
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
				if(value  == null){
					value = "";
					continue;
				}
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
		
		Map<Field,String> out = getOutPutMap();
		if(outPutSetOther != null && outPutSetOther.size() > 0 && out != null && out.size() > 0){
			for(Map.Entry<Field, String> kv : out.entrySet()){
				if(outPutSetOther.contains(kv.getKey().getName())){
					try {
						Object obj = this.getClass().getMethod(kv.getValue()).invoke(this);
						jo.addProperty(kv.getKey().getName(), String.valueOf(obj));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
			
		}
		logger.error("toString="+jo.toString());
		return jo.toString();
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public long insert() throws Exception{
		JsonObject jo = new JsonParser().parse(toString()).getAsJsonObject();
		CacheVo vo = newCacheVo();
		vo.parse(jo,1);
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
					if(column.equals(CREATE_TIME) || column.equals(OPERATOR_TIME)){
						signSql.append(" ,'").append(PublicMethod.formatDateStr(date)).append("'");
					}else{
						signSql.append(" ,").append(getColumnValue(field));
					}
					
				}else{
					bool = true;
					columnSql.append(column);
					if(column.equals(CREATE_TIME) || column.equals(OPERATOR_TIME)){
						signSql.append("'").append(PublicMethod.formatDateStr(date)).append("'");
					}else{
						signSql.append(getColumnValue(field));
					}
				}
				
				if(column.equals(CREATE_TIME) || column.equals(OPERATOR_TIME)){
					setFieldValue(field, date);
					listParam.add(date);
				}else{
					logger.error(field.getName()+"============"+getFieldValue(field));
					listParam.add(getFieldValue(field));
				}
				
			}
		}
		sql.append(columnSql).append(" ) VALUES (").append(signSql).append(" )");
		logger.error("--------------insertSql="+sql.toString());
		long id = getJdbcDao().insert(sql.toString(), listParam.toArray());
		setPKValue(String.valueOf(id));
		if(getJdbcDao().isAutoCommit()){
			insertNosql(1);//保存缓存数据
		}
		vo.deleteCustomCacheAll();//删除自定义缓存
		return id;
	}
	
	private String getColumnValue(Field field){
		String typeName = field.getType().getName();
		Object value = getFieldValue(field);
		if(field.getType().equals(Integer.class) || typeName.equalsIgnoreCase("int")){
			if(value == null){
				return ZERO;
			}else{
				return value+"";
			}
			
		}else if(field.getType().equals(Long.class) || typeName.equalsIgnoreCase("long")){
			if(value == null){
				return ZERO;
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
				return ZERO;
			}else{
				return value+"";
			}
			
		}else if(field.getType().equals(Float.class) || typeName.equalsIgnoreCase("float")){
			if(value == null){
				return ZERO;
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
				return "null";
			}
			return "'"+PublicMethod.formatDateStr((java.util.Date)value, "yyyy-MM-dd HH:mm:ss")+"'";
			
		}else if(field.getType().equals(java.sql.Date.class)){
			if(value == null){
				return "null";
			}
			return "'"+PublicMethod.formatDateStr((java.sql.Date)value, "yyyy-MM-dd HH:mm:ss")+"'";
		}else{
			if(value == null){
				return "''";
			}
			return "'"+value.toString().replaceAll("'", "\\\\\'").replaceAll("\"", "\\\\\"")+"'";
		}
	}
	
	/**
	 * 删除自定义缓存
	 * @return
	 */
	public int delete() throws Exception{
		loadVo();
		deleteNoSql();
		deleteCustomCacheAll();//删除自定义缓存
		StringBuilder sql = new StringBuilder("delete FROM ").append(getTableName());
		sql.append(" where ").append(getCustomORM().get(getPKField().getName())).append(" = ").append(getIdValue().toString());
		int index = getJdbcDao().update(sql.toString(),null);
		
		return index;
	}
	
	/**
	 * 删除，包括子记录
	 * @return
	 */
	public int  deleteLinkChild(String parentName) throws Exception{
		
		List<CacheVo> list = getListVO(0, 10000, new SQLWhere(new EQCnd(parentName, getIdValue())));
    	if(list != null && list.size() > 0){
    		for(CacheVo vo : list){
        		try {
        			StringBuilder sql = new StringBuilder("DELETE FROM ").append(getTableName());
        			sql.append(" WHERE ").append(getCustomORM().get(getPKField().getName())).append(" = ").append(getIdValue().toString());
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
		StringBuilder sql = new StringBuilder(" UPDATE ");
		sql.append(getTableName()).append(" SET ");
		List<Field> listField = getColumnField();
		int len = listField.size();
		//Object [] param = new Object[len+1];
		List<Object> listParam = new ArrayList<Object>();
		Map<String,String> columnORM =getCustomORM();
		StringBuilder columnSql = new StringBuilder();
		boolean bool = false;
		if(fieldName != null && fieldName.length > 0){
			List<String> fieldList = Arrays.asList(fieldName);
			Date date = new Date();
			listParam.add(date);
			//if(autoCommit){
				columnSql.append("operator_time").append(" = ? ");
			//}else{
			//	columnSql.append("operator_time").append(" =  ").append(PublicMethod.formatDateStr(date, "'yyyy-MM-dd HH:mm:ss'"));
			//}
			
			
			for(int i = 0;i<len;i++){
				Field field = listField.get(i);
				String column = columnORM.get(field.getName());
				if(fieldList.contains(column) || fieldList.contains(field.getName())){
					//if(autoCommit){
						columnSql.append(",").append(column).append(" = ? ");
					//}else{
					//	columnSql.append(",").append(column).append(" =  ").append(getColumnValue(field));
					//}
					
					listParam.add(getFieldValue(field));
				}
			}
			sql.append(columnSql);
		}else{
			for(int i = 0;i<len;i++){
				Field field = listField.get(i);
				String column = columnORM.get(field.getName());
				if(column.equals("create_time")){
					continue;
				}else if(column.equals("operator_time")){
					Date date = new Date();
					setFieldValue(field, date);
					listParam.add(date);
					if(bool){
						//if(autoCommit){
							columnSql.append(",").append(column).append(" = ? ");
						//}else{
						//	columnSql.append(",").append(column).append(" =  ").append(PublicMethod.formatDateStr(date, "'yyyy-MM-dd HH:mm:ss'"));
						//}
						
					}else{
						bool = true;
						//if(autoCommit){
							columnSql.append(column).append(" = ? ");
						//}else{
						//	columnSql.append(column).append(" =  ").append(PublicMethod.formatDateStr(date, "'yyyy-MM-dd HH:mm:ss'"));
						//}
						
					}
					
				}else{
					if(bool){
						//if(autoCommit){
							columnSql.append(",").append(column).append(" = ? ");
						//}else{
						//	columnSql.append(",").append(column).append(" =  ").append(getColumnValue(field));
						//}
						
					}else{
						bool = true;
						//if(autoCommit){
							columnSql.append(column).append(" = ? ");
						//}else{
						//	columnSql.append(column).append(" =  ").append(getColumnValue(field));
						//}
						
					}
					listParam.add(getFieldValue(field));
					//param[i] = getFieldValue(field);
				}
			}
			sql.append(columnSql);
		}
		//if(autoCommit){
			sql.append( " WHERE ").append(columnORM.get(getPKField().getName())).append(" = ?");
		//}else{
		//	sql.append( " WHERE ").append(columnORM.get(getPKField().getName())).append(" = ").append(getFieldValue(getPKField()));
		//}
		
		//param[len] = getFieldValue(getPKField());
		listParam.add(getFieldValue(getPKField()));
		int index = getJdbcDao().update(sql.toString(), listParam.toArray());
		if(getJdbcDao().isAutoCommit()){
			insertNosql(1);//保存缓存数据
		}else{
			deleteNoSql();
		}
		
		//deleteCustomCacheAll();//删除自定义缓存
		vo.deleteCustomCacheAll();
		return index;
	}

}