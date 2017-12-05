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
import com.app.entity.business.BusinessCheckLogEntity;
import com.app.entity.repertory.RepertoryGoodsBatchEntity;
import com.app.entity.repertory.RepertoryGoodsBillDetailEntity;
import com.app.entity.repertory.RepertoryGoodsBillEntity;
import com.app.entity.repertory.RepertoryGoodsEntity;
import com.app.entity.task.TaskLockComponentEntity;
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
    		}else if(entity.getCheckStatus() != StaticBean.WAIT){
    			return error("不能重复审核数据");
    		}
    	}else{
    		return error("主键不能为空");
    	}
    	
    	entity.parse(jo,1);
    	try{
    		
    		
    		Date date = new Date();
    		entity.setCheckUser(getLoginUser().getUserId());
    		entity.setCheckTime(PublicMethod.formatDateStr(date,"yyyy-MM-dd HH:mm:ss"));
    		if(entity.getCheckStatus() == StaticBean.NO){
				BusinessCheckLogEntity log = new BusinessCheckLogEntity();
				log.setRemark(entity.getCheckRemark());
				log.setType(BusinessCheckLogEntity.TYPE_REPERTORY_GOODS_BILL);
				log.setCheckStatus(entity.getCheckStatus());
				log.setDataId(entity.getGoodsBillId());
				log.setCheckTime(date);
				log.setCheckUser(getLoginUser().getUserId());
				log.setNumber(0L);
				log.insert();
			}else if(entity.getCheckStatus() != StaticBean.YES){
				return error("非法的审核状态");
			}
    		
    		
    		
    		
    		
    		entity.update(RepertoryGoodsBillEntity.CHECK_STATUS,RepertoryGoodsBillEntity.CHECK_USER,RepertoryGoodsBillEntity.CHECK_REMARK,RepertoryGoodsBillEntity.CHECK_TIME);
    		RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(this.jdbcDao);
    		detail.setGoodsBillId(entity.getGoodsBillId());
    		List<RepertoryGoodsBillDetailEntity> list = detail.queryCustomCacheValue(0);
    		if(list != null && list.size() > 0){
    			if(entity.getCheckStatus() == StaticBean.YES){//审核通过
    				switch(entity.getType()){
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_CHECK://盘点
    						for(RepertoryGoodsBillDetailEntity obj : list){
    							RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);//更新批次信息
    							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
    								batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
    								batch.setInventory(batch.getInventory()+obj.getNumber());//批次库存+-
    								batch.update();
    							}else{
    								batch.setInventory(obj.getNumber());//批次库存+-
    								batch.setGoodsId(obj.getGoodsId());
    								batch.setGoodsBatchCode(entity.getGoodsBatchCode());
    								batch.insert();
    							}
    							
    							
    						}
    						break;
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY://申领 批次锁定-
    						for(RepertoryGoodsBillDetailEntity obj : list){
    							RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
    							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
    								batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
    								batch.setLocking(batch.getLocking() - obj.getNumber());
    								batch.update();
    							}else{
    								throw new Exception("申领单的批次id不能为空");
    							}
    							
    						}
    						break;
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_BUY://入库
    						for(RepertoryGoodsBillDetailEntity obj : list){
    							RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
    							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
    								batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
    								batch.setInventory(batch.getInventory()+obj.getNumber());//批次库存+-
    								batch.update();
    								
    							}else{
    								batch.setInventory(obj.getNumber());
    								batch.setGoodsId(obj.getGoodsId());
    								batch.setGoodsBatchCode(entity.getGoodsBatchCode());
    								batch.insert();
    							}
    							
    						}
    						break;
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_PRODUCE://生产
    						for(RepertoryGoodsBillDetailEntity obj : list){
    							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
    								RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
    								batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
    								batch.setInventory(batch.getInventory()+obj.getNumber());
    								batch.update();
    							}else{
    								RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
    								batch.setGoodsBatchCode(entity.getGoodsBatchCode());
    								batch.setGoodsId(obj.getGoodsId());
    								batch.setInventory(obj.getNumber());
    								batch.insert();
    							}
    							
    						}
    						break;
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_SELL://出货
    						for(RepertoryGoodsBillDetailEntity obj : list){
    							RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
    							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
    								batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
    								batch.setInventory(batch.getInventory()-obj.getNumber());
    								batch.update();
    							}else{
    								throw new Exception("出货单的批次id不能为空");
    							}
    						}
    						break;
    				}
    				
    			}else if(entity.getCheckStatus() == StaticBean.NO){//审核不通过
    				switch(entity.getType()){
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_CHECK://盘点
    						break;
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY://申领 批次锁定-
    						for(RepertoryGoodsBillDetailEntity obj : list){
    							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
    								/**
    								 * 审核不通过的生产申领单 需将锁定的数据恢复，并且删除锁定的原料数据单
    								 */
    								if(!PublicMethod.isEmptyValue(obj.getLockComponentId())){
    									RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
    									batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
    									batch.setLocking(batch.getLocking() - obj.getNumber());
    									batch.setInventory(batch.getInventory()+obj.getNumber());
    									batch.update();//
    									TaskLockComponentEntity tlc = new TaskLockComponentEntity(jdbcDao);
    									tlc.setLockComponentId(obj.getLockComponentId()).delete();
    								}
    								
    							}else{
    								throw new Exception("申领单的批次id不能为空");
    							}
    							
    						}
    						break;
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_BUY://入库
    						
    						break;
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_PRODUCE://生产
    						
    						break;
    					case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_SELL://出货
    						
    						break;
    				}
    			}
    		}
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
	    		/**
	    		 * 已审核的生产任务创建的申领单不能修改
	    		 */
	    		if((entity.getType() ==RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY ||   entity.getType() ==RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_PRODUCE) && !PublicMethod.isEmptyValue(entity.getProduceId())){
	    			throw new Exception("该数据不能修改");
	    		}
	    		
	    		entity.parse(jo,1);
	    		entity.setCheckStatus(StaticBean.WAIT);
	    		entity.setCheckUser(null);
	    		entity.setCheckRemark("");
	    		entity.setCheckTime("");
	    		entity.update(RepertoryGoodsBillEntity.CHECK_REMARK,RepertoryGoodsBillEntity.CHECK_TIME,RepertoryGoodsBillEntity.CHECK_USER,RepertoryGoodsBillEntity.CHECK_STATUS,RepertoryGoodsBillEntity.GOODS_BATCH_CODE,RepertoryGoodsBillEntity.REMARK,RepertoryGoodsBillEntity.TITLE);
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
	    				if(entity.getType() != RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_CHECK && detail.getNumber() <= 0){
							return error("数量不能小于或等于0");
						}
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
    					if(entity.getType() != RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_CHECK && detail.getNumber() <= 0){
							return error("数量不能小于或等于0");
						}
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
