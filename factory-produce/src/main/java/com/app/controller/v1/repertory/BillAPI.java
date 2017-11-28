package com.app.controller.v1.repertory;

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

import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.repertory.RepertoryGoodsBatchEntity;
import com.app.entity.repertory.RepertoryGoodsBillDetailEntity;
import com.app.entity.repertory.RepertoryGoodsBillEntity;
import com.app.entity.repertory.RepertoryGoodsEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xx.util.string.Format;

/**
 * 功能说明：账单管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/repertory/bill")
@Scope("prototype")//设置成多例
public class BillAPI extends Result{
    public static Log logger = LogFactory.getLog(BillAPI.class);
    
  
    /**
     * 查询列表
     * @param aoData
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) {
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
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity(jdbcDao);
    	
    	entity.outPutOther(RepertoryGoodsBillEntity.CHECK_USER_NAME,RepertoryGoodsBillEntity.LIABLE_USER_NAME);
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
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/vo/{id}")
    public String vo(@PathVariable("id") Long id) {
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity(jdbcDao);
    	entity.setGoodsBillId(id).loadVo();
        return success(entity);
    }

    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity(jdbcDao);
    	try{
    		entity.setGoodsBillId(id).loadVo();
    		if(!isCheckUserSelf(entity.getLiableUser())){
    			return error("没有权限删除");
    		}
    		
    		if(!PublicMethod.isEmptyValue(entity.getProduceId())){
    			return error("该数据不能删除");
    		}
    		entity.delete();
    		return success("删除成功");
    	}catch(Exception e){
    		logger.error("删除失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
   
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/check")
    public String check(@RequestParam String aoData) {
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity(jdbcDao);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	if(jo.has(RepertoryGoodsBillEntity.GOODS_BILL_ID)){
    		entity.setGoodsBillId(jo.get(RepertoryGoodsBillEntity.GOODS_BILL_ID).getAsLong()).loadVo();
    		if(PublicMethod.isEmptyStr(entity.getGoodsBatchCode())){
    			return error("不存在的数据");
    		}else if(entity.getCheckStatus() == StaticBean.YES){
    			return error("审核通过的数据不能修改");
    		}
    		
    		entity.setCheckUser(getLoginUser().getUserId());
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
    public String update(@RequestParam String aoData) {
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity(jdbcDao);
    	try{
	    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
	    	if(jo.has(RepertoryGoodsBillEntity.GOODS_BILL_ID)){
	    		entity.setGoodsBillId(jo.get(RepertoryGoodsBillEntity.GOODS_BILL_ID).getAsLong()).loadVo();
	    		if(PublicMethod.isEmptyStr(entity.getGoodsBatchCode())){
	    			return error("不存在的数据");
	    		}else if(entity.getCheckStatus() == StaticBean.YES){
	    			return error("审核通过的数据不能修改");
	    		}
	    		if(!isCheckUserSelf(entity.getLiableUser())){
	    			return error("没有权限修改");
	    		}
	    		entity.parse(jo,1);
	    		entity.update(RepertoryGoodsBillEntity.GOODS_BATCH_CODE,RepertoryGoodsBillEntity.REMARK,RepertoryGoodsBillEntity.TITLE);
	    		RepertoryGoodsBillDetailEntity billDetail = new RepertoryGoodsBillDetailEntity(jdbcDao);
	    		List<RepertoryGoodsBillDetailEntity> delList = billDetail.setGoodsBillId(entity.getGoodsBillId()).queryCustomCacheValue(0);
	    		for(RepertoryGoodsBillDetailEntity d : delList){
	    			d.setType(entity.getType());
	    			d.delete();
	    		}
	    		
	    		if(jo.has("goods_batch_list") && jo.get("goods_batch_list").isJsonArray()){
	    			JsonArray ja = jo.get("goods_batch_list").getAsJsonArray();
	    			for(JsonElement je : ja){
	    				RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(jdbcDao);
	    				JsonObject jsonObject = je.getAsJsonObject();
	    				String code = null;
	    				if(!jsonObject.has("code") || jsonObject.get("code").isJsonNull()){
	    					return error("非法参数为空");
	    				}else{
	    					jsonObject.get("code").getAsString();
	    				}
	    				
	    				if(jsonObject.has("value") && !jsonObject.get("value").isJsonNull() ){
	    					String value = jsonObject.get("value").getAsString();
	    					if(!Format.isNumeric(value) || value.equals("0")){
	    						return error("非法参数:数量为不等于0的数字");
	    					}
	    					
	    					detail.setNumber(Integer.parseInt(value));
	    				}else{
	    					return error("非法参数为空:缺少数量字段");
	    				}
	    				
	    				
	    				if(!jsonObject.has("goods_id") || jsonObject.get("goods_id").isJsonNull() || PublicMethod.isEmptyStr(jsonObject.get("goods_id").getAsString())){
	    					return error(code+"数据列非法参数:goods_id不能为空");
	    				}else{
	    					if(!Format.isNumeric(jsonObject.get("goods_id").getAsString())){
	    						return error(code+"数据列非法参数:goods_id必需为大于0的整数");
	    					}
	    					
	    					if(jsonObject.get("goods_id").getAsLong() <= 0){
	    						return error(code+"数据列非法参数:goods_id必需为大于0的整数");
	    					}
	    					
	    					RepertoryGoodsEntity goods = new RepertoryGoodsEntity(jdbcDao);
	    					goods.setGoodsId(jsonObject.get("goods_id").getAsLong()).loadVo();
	    					if(PublicMethod.isEmptyStr(goods.getCode())){
	    						return error(code+"数据列非法参数:无效的产品信息");
	    					}else{
	    						detail.setGoodsId(goods.getGoodsId());
	    					}
	    				}
	    				if(jsonObject.has("batch_id") && !jsonObject.get("batch_id").isJsonNull()){
	    					if(!Format.isNumeric(jsonObject.get("batch_id").getAsString())){
	    						return error(code+"数据列非法参数:batch_id必需为数字");
	    					}
	    					if(!PublicMethod.isEmptyValue(jsonObject.get("batch_id").getAsLong())){
	    						RepertoryGoodsBatchEntity batch= new RepertoryGoodsBatchEntity(jdbcDao);
	    						batch.setGoodsBatchId(jsonObject.get("batch_id").getAsLong()).loadVo();
	    						if(PublicMethod.isEmptyValue(batch.getGoodsId())){
	    							return error(code+"数据列非法参数:无效的批次信息");
	    						}else{
	    							detail.setGoodsBatchId(batch.getGoodsBatchId());
	    						}
	    					}
	    				}
	    				detail.setType(entity.getType());
	    				detail.setGoodsBillId(entity.getGoodsBillId());
	    				detail.insert();
	    			}
	    		}
	    	}else{
	    		return error("主键不能为空");
	    	}
	    	
    	
    	
        	return success("修改成功",entity.getGoodsBillId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) {
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	RepertoryGoodsBillEntity entity = new RepertoryGoodsBillEntity(jdbcDao);
    	entity.parse(jo);
    	try{
    		entity.setLiableUser(getLoginUser().getUserId());
    		
    		Long id = entity.insert();
    		if(jo.has("goods_batch_list") && jo.get("goods_batch_list").isJsonArray()){
    			JsonArray ja = jo.get("goods_batch_list").getAsJsonArray();
    			for(JsonElement je : ja){
    				RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(jdbcDao);
    				JsonObject jsonObject = je.getAsJsonObject();
    				String code = null;
    				if(!jsonObject.has("code") || jsonObject.get("code").isJsonNull()){
    					return error("非法参数为空");
    				}else{
    					jsonObject.get("code").getAsString();
    				}
    				if(jsonObject.has("value") && !jsonObject.get("value").isJsonNull() ){
    					String value = jsonObject.get("value").getAsString();
    					if(value.startsWith("-")){
    						if(!Format.isNumeric(value.substring(1)) || value.equals("-0")){
        						return error("非法参数:数量为不等于0的数字");
        					}
    					}else{
    						if(!Format.isNumeric(value) || value.equals("0")){
        						return error("非法参数:数量为不等于0的数字");
        					}
    					}
    					detail.setNumber(Integer.parseInt(value));
    				}else{
    					return error("非法参数为空:缺少数量字段");
    				}
    				
    				if(!jsonObject.has("goods_id") || jsonObject.get("goods_id").isJsonNull() || PublicMethod.isEmptyStr(jsonObject.get("goods_id").getAsString())){
    					return error(code+"数据列非法参数:goods_id不能为空");
    				}else{
    					if(!Format.isNumeric(jsonObject.get("goods_id").getAsString())){
    						return error(code+"数据列非法参数:goods_id必需为大于0的整数");
    					}
    					if(jsonObject.get("goods_id").getAsLong() <= 0){
    						return error(code+"数据列非法参数:goods_id必需为大于0的整数");
    					}
    					RepertoryGoodsEntity goods = new RepertoryGoodsEntity(jdbcDao);
    					goods.setGoodsId(jsonObject.get("goods_id").getAsLong()).loadVo();
    					if(PublicMethod.isEmptyStr(goods.getCode())){
    						return error(code+"数据列非法参数:无效的产品信息");
    					}else{
    						detail.setGoodsId(goods.getGoodsId());
    					}
    				}
    				if(jsonObject.has("batch_id") && !jsonObject.get("batch_id").isJsonNull()){
    					if(!Format.isNumeric(jsonObject.get("batch_id").getAsString())){
    						return error(code+"数据列非法参数:batch_id必需为数字");
    					}
    					if(!PublicMethod.isEmptyValue(jsonObject.get("batch_id").getAsLong())){
    						RepertoryGoodsBatchEntity batch= new RepertoryGoodsBatchEntity(jdbcDao);
    						batch.setGoodsBatchId(jsonObject.get("batch_id").getAsLong()).loadVo();
    						if(PublicMethod.isEmptyValue(batch.getGoodsId())){
    							return error(code+"数据列非法参数:无效的批次信息");
    						}else{
    							detail.setGoodsBatchId(batch.getGoodsBatchId());
    						}
    					}
    				}
    				detail.setType(entity.getType());
    				detail.setGoodsBillId(id);
    				detail.insert();
    			}
    		}
    		
        	return success("新增成功",entity.getGoodsBillId());
    	}catch(Exception e){
    		logger.error("新增失败", e);
    		
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
