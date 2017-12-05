package com.app.controller.v1.event;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.bean.SysUserDetails;
import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.cnd.NotEQCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.event.EventSanctionEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：系统配置
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/event/sanction")
@Scope("prototype")
public class SanctionAPI extends Result{
    public static Log logger = LogFactory.getLog(SanctionAPI.class);
    
  
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) {
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(EventSanctionEntity.EVENT_SANCTION_ID));
    	int iDisplayStart = 0;// 起始  
    	int iDisplayLength = 10;// size 
    	int sEcho = 0;
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(EventSanctionEntity.NAME)){  
            	sql.and(new LikeCnd(EventSanctionEntity.NAME,jsonObject.get(VALUE).getAsString()));
            }
    	}
    	
    	EventSanctionEntity entity = new EventSanctionEntity(jdbcDao);
    	entity.outPutOther(EventSanctionEntity.USER_NAME,EventSanctionEntity.CHECK_NAME,EventSanctionEntity.OPERATOR_NAME);
    	List<EventSanctionEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = entity.getCount(sql);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", count);
    	map.put("iTotalDisplayRecords", count);
        return success(map);
    }
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/user")
    public String user(@RequestParam String aoData) {
    	SQLWhere sql = new SQLWhere(new EQCnd(SysUserEntity.VALID, StaticBean.YES)).and(new NotEQCnd(SysUserEntity.TYPE, SysUserEntity.USER_ADMIN)).orderBy(new DescSort(SysUserEntity.USER_ID));
    	int iDisplayStart = 0;// 起始  
    	int iDisplayLength = 1000;// size 
    	int sEcho = 0;
    	if(!PublicMethod.isEmptyStr(aoData)){
    		JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
        	for(JsonElement je : jo){
        		JsonObject jsonObject = je.getAsJsonObject();
        		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                    sEcho = jsonObject.get(VALUE).getAsInt();  
                else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                    iDisplayStart = jsonObject.get(VALUE).getAsInt();  
                else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                    iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
                else if (jsonObject.get(NAME).getAsString().equals(SysUserEntity.USER_NAME)){  
                	sql.and(new LikeCnd(SysUserEntity.USER_NAME,jsonObject.get(VALUE).getAsString()));
                }else if (jsonObject.get(NAME).getAsString().equals(SysUserEntity.MOBILE)){  
                	sql.and(new LikeCnd(SysUserEntity.MOBILE,jsonObject.get(VALUE).getAsString()));
                }else if (jsonObject.get(NAME).getAsString().equals(SysUserEntity.NUMBER)){  
                	sql.and(new LikeCnd(SysUserEntity.NUMBER,jsonObject.get(VALUE).getAsString()));
                }
        	}
    	}
    	
    	
    	SysUserEntity entity = new SysUserEntity(jdbcDao);
    	List<SysUserEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", 0);
    	map.put("iTotalDisplayRecords", 0);
        return success(map);
    }
    
    
    /**
     * 获取单个对象
     * @param id
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/vo/{id}")
    public String vo(@PathVariable("id") Long id) {
    	EventSanctionEntity entity = new EventSanctionEntity(jdbcDao);
    	entity.setEventSanctionId(id);
    	entity.outPutOther(EventSanctionEntity.USER_NAME,EventSanctionEntity.CHECK_NAME,EventSanctionEntity.OPERATOR_NAME);
    	entity.loadVo();
        return success(entity);
    }
    
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	EventSanctionEntity entity = new EventSanctionEntity(jdbcDao);
    	entity.setEventSanctionId(id).loadVo();
    	
		try {
			if(entity.getCheckStatus() == StaticBean.YES){
				return error("审核通过的数据不能删除");
			}
			SysUserDetails user = getLoginUser();
			if(user.getUserId() != entity.getOperatorUserId() && user.getType() != SysUserEntity.USER_ADMIN){
				return error("没有权限删除数据");
			}
			entity.delete();
			return success("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return error("删除失败:"+e.getMessage());
		}
            
    	
    	
    }
    
    /**
     * 审核数据
     * @param aoData
     * @return
     */
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/check")
    public String check(@RequestParam String aoData) {
    	EventSanctionEntity entity = new EventSanctionEntity(jdbcDao);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	if(jo.has(EventSanctionEntity.EVENT_SANCTION_ID)){
    		entity.setEventSanctionId(jo.get(EventSanctionEntity.EVENT_SANCTION_ID).getAsLong()).loadVo();
    		if(entity.getCheckStatus() == StaticBean.NO || entity.getCheckStatus() == StaticBean.YES){
    			return error("已审核的数据不能操作");
    		}
    	}else{
    		return error("主键不能为空");
    	}
    	
    	entity.parse(jo,1);
    	try{
    		
    		if(entity.getCheckStatus() != StaticBean.YES && entity.getCheckStatus() != StaticBean.NO){
    			return error("审核结果值不对");
    		}
    		
    		entity.setCheckUserId(getLoginUser().getUserId());
    		entity.setCheckTime(new Date());
    		entity.update(EventSanctionEntity.CHECK_REMARK,EventSanctionEntity.CHECK_USER_ID,EventSanctionEntity.CHECK_TIME,EventSanctionEntity.CHECK_STATUS);
        	return success("修改成功",entity.getEventSanctionId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) {
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	try{
    		EventSanctionEntity entity = new EventSanctionEntity(jdbcDao);
    		if(jo.has(EventSanctionEntity.EVENT_SANCTION_ID) && jo.get(EventSanctionEntity.EVENT_SANCTION_ID).getAsLong() > 0){
    			entity.setEventSanctionId(jo.get(EventSanctionEntity.EVENT_SANCTION_ID).getAsLong()).loadVo();
    			if(entity.getCheckStatus() == StaticBean.YES){
    				return error("审核通过的数据不能修改");
    			}
    			
    			SysUserDetails user = getLoginUser();
    			if(user.getUserId() != entity.getOperatorUserId() && user.getType() != SysUserEntity.USER_ADMIN){
    				return error("没有权限修改数据");
    			}
    			
    			entity.parse(jo);
    		}else{
        		return error("主键不有为空");
        	}
    		
    		if(PublicMethod.isEmptyValue(entity.getUserId()) || entity.getUserId() < 0){
        		return error("人员不能为空");
        	}
    		
    		if(PublicMethod.isEmptyStr(entity.getName())){
        		return error("名称不能为空");
        	}
        	
    		if(entity.getType() != EventSanctionEntity.SANCTION_TYPE_PENALTY && entity.getType() != EventSanctionEntity.SANCTION_TYPE_REWARD){
        		return error("奖罚类型错误");
        	}
    		
        	if(PublicMethod.isEmptyValue(entity.getMoney()) || entity.getMoney() < 0){
        		return error("奖励必需为大于0的数");
        	}
        	entity.setCheckStatus(StaticBean.WAIT);
        	entity.setCheckUserId(null);
        	entity.setCheckTime(null);
    		entity.setCheckRemark("");
    		entity.update(EventSanctionEntity.NAME,EventSanctionEntity.TYPE,EventSanctionEntity.USER_ID,
    				EventSanctionEntity.MONEY,EventSanctionEntity.REMARK,EventSanctionEntity.CHECK_STATUS,
    				EventSanctionEntity.CHECK_REMARK,EventSanctionEntity.CHECK_TIME,EventSanctionEntity.CHECK_USER_ID);
        	return success("修改成功",entity.getEventSanctionId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    	
    }
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) {
    	EventSanctionEntity entity = new EventSanctionEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	if(PublicMethod.isEmptyStr(entity.getName())){
    		return error("名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getMoney()) || entity.getMoney() < 0){
    		return error("奖励必需为大于0的数");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getUserId()) || entity.getUserId() < 0){
    		return error("人员不能为空");
    	}
    	
    	if(entity.getType() != EventSanctionEntity.SANCTION_TYPE_PENALTY && entity.getType() != EventSanctionEntity.SANCTION_TYPE_REWARD){
    		return error("奖罚类型错误");
    	}
    	try{
    		entity.setCheckStatus(StaticBean.WAIT);
    		entity.setOperatorUserId(getLoginUser().getUserId());
    		entity.insert();
        	return success("新增成功",entity.getEventSanctionId());
    	}catch(Exception e){
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
