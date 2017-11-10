package com.app.controller.v1.repertory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.bean.SysUserDetails;
import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.repertory.RepertoryGoodsBatchEntity;
import com.app.entity.repertory.RepertoryGoodsBillDetailEntity;
import com.app.entity.repertory.RepertoryGoodsBillEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：账单管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/repertory/bill")
public class BillAPI extends Result{
    public static Log logger = LogFactory.getLog(BillAPI.class);
    
  
    /**
     * 查询列表
     * @param aoData
     * @return
     * @throws Exception
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) throws Exception{
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(RepertoryGoodsBillEntity.GOODS_BILL_ID));
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsBillEntity.GOODS_BATCH_CODE)){  
            	sql.and(new LikeCnd(RepertoryGoodsBillEntity.GOODS_BATCH_CODE,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsBillEntity.TITLE)){
            	sql.and(new LikeCnd(RepertoryGoodsBillEntity.TITLE,jsonObject.get(VALUE).getAsString()));
            	
            }
    	}
    	
    	logger.error(aoData);
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity();
    	
    	entity.outPut(RepertoryGoodsBillEntity.CHECK_USER_NAME,RepertoryGoodsBillEntity.LIABLE_USER_NAME);
    	List<RepertoryGoodsBillEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
    	long count = entity.getCount(sql);
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", count);
    	map.put("iTotalDisplayRecords", count);
        return success(map);
    }
    
   

    /**
     * 获取单个对象
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/vo/{id}")
    public String vo(@PathVariable("id") Long id) throws Exception{
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity();
    	entity.setGoodsBillId(id).loadVo();
        return success(entity);
    }

    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity();
    	try{
    		entity.setGoodsBillId(id).delete();
    		return success("删除成功");
    	}catch(Exception e){
    		logger.error("删除失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
   
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/check")
    public String check(@RequestParam String aoData) throws Exception{
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity();
    	
    	logger.error("-------"+aoData);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	if(jo.has(RepertoryGoodsBillEntity.GOODS_BILL_ID)){
    		entity.setGoodsBillId(jo.get(RepertoryGoodsBillEntity.GOODS_BILL_ID).getAsLong()).loadVo();
    		if(PublicMethod.isEmptyStr(entity.getGoodsBatchCode())){
    			return error("不存在的数据");
    		}else if(entity.getCheckStatus() == StaticBean.YES){
    			return success("审核通过的数据不能修改");
    		}
    		
    		SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    		entity.setCheckUser(userDetails.getUserId());
    		entity.setCheckTime(PublicMethod.formatDateStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
    	}else{
    		return error("主键不能为空");
    	}
    	
    	entity.parse(jo,1);
    	try{
    		
    		entity.update(RepertoryGoodsBillEntity.CHECK_STATUS,RepertoryGoodsBillEntity.CHECK_USER,RepertoryGoodsBillEntity.CHECK_REMARK,RepertoryGoodsBillEntity.CHECK_TIME);
        	return success("修改成功",entity.getGoodsBillId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) throws Exception{
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity();
    	
    	logger.error("-------"+aoData);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	if(jo.has(RepertoryGoodsBillEntity.GOODS_BILL_ID)){
    		entity.setGoodsBillId(jo.get(RepertoryGoodsBillEntity.GOODS_BILL_ID).getAsLong()).loadVo();
    		if(PublicMethod.isEmptyStr(entity.getGoodsBatchCode())){
    			return error("不存在的数据");
    		}else if(entity.getCheckStatus() == StaticBean.YES){
    			return success("审核通过的数据不能修改");
    		}
    		
    		SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    		logger.error("-------4---------type="+userDetails.getType());
    		if(entity.getLiableUser() != userDetails.getUserId() && userDetails.getType() != SysUserEntity.USER_ADMIN){
    			return success("没有权限修改");
    		}
    	}else{
    		return error("主键不能为空");
    	}
    	
    	entity.parse(jo,1);
    	try{
    		
    		entity.update();
        	return success("修改成功",entity.getGoodsBillId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) throws Exception{
    	logger.error("-------3---------aoData="+aoData);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity();
    	entity.parse(jo);
    	try{
    		SysUserDetails userDetails = (SysUserDetails) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
    		entity.setLiableUser(userDetails.getUserId());
    		
    		Long id = entity.insert();
    		if(jo.has("goods_batch_list") && jo.get("goods_batch_list").isJsonArray()){
    			JsonArray ja = jo.get("goods_batch_list").getAsJsonArray();
    			for(JsonElement je : ja){
    				JsonObject jsonObject = je.getAsJsonObject();
    				if(!jsonObject.has("name") || PublicMethod.isEmptyStr(jsonObject.get("name").getAsString())){
    					return error(jsonObject.get("alias").getAsString()+"参数为空");
    				}
    				if(jsonObject.has("value")){
    					String value = jsonObject.get("value").getAsString();
    					if(!PublicMethod.isEmptyStr(value) && !value.equals("0")){
    						
    						RepertoryGoodsBatchEntity batch= new RepertoryGoodsBatchEntity();
    						batch.setGoodsBatchId(jsonObject.get("name").getAsLong()).loadVo();
    						if(PublicMethod.isEmptyValue(batch.getGoodsId())){
    							return error(jsonObject.get("alias").getAsString()+"非法的批次信息");
    						}
    						RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity();
    						detail.setGoodsId(batch.getGoodsId());
    						detail.setGoodsBatchId(batch.getGoodsBatchId());
    						detail.setGoodsBillId(id);
    						try{
    							detail.setNumber(Integer.parseInt(value));
    						}catch(Exception e){
    							return error(jsonObject.get("alias").getAsString()+"数量必需为整形数字形式");
    						}
    						detail.setType(entity.getType());
    						detail.insert();
    					}
    					
    					
    				}
    				
    			}
    		}
    		
    		
        	return success("新增成功",entity.getGoodsBillId());
    	}catch(Exception e){
    		logger.error("新增失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
