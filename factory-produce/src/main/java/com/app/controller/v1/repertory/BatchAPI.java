package com.app.controller.v1.repertory;

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
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.repertory.RepertoryGoodsBatchEntity;
import com.app.entity.repertory.RepertoryGoodsBillEntity;
import com.app.entity.repertory.RepertoryGoodsEntity;
import com.app.util.PublicMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：产品批次管理
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/repertory/batch")
@Scope("prototype")//设置成多例
public class BatchAPI extends Result{
    public static Log logger = LogFactory.getLog(BatchAPI.class);
    
    
    
    /**
     * 查询列表
     * @param aoData
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) {
    	
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(RepertoryGoodsBatchEntity.GOODS_BATCH_ID));
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsBatchEntity.GOODS_BATCH_CODE)){  
            	sql.and(new EQCnd(RepertoryGoodsBatchEntity.GOODS_BATCH_CODE,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsEntity.CODE)){
            	RepertoryGoodsEntity goods = new RepertoryGoodsEntity(jdbcDao);
            	goods = goods.setCode(jsonObject.get(VALUE).getAsString()).queryCustomCacheVo(0);
            	if(!PublicMethod.isEmptyValue(goods.getGoodsId())){
            		sql.and(new EQCnd(RepertoryGoodsBatchEntity.GOODS_ID,goods.getGoodsId()));
            	}else{
            		sql.and(new EQCnd(RepertoryGoodsBatchEntity.GOODS_ID,0));
            	}
            	
            }
    	}
    	
    	logger.error(aoData);
    	RepertoryGoodsBatchEntity entity = new RepertoryGoodsBatchEntity(jdbcDao);
    	
    	entity.outPutOther(RepertoryGoodsBatchEntity.NAME,RepertoryGoodsBatchEntity.TYPE,RepertoryGoodsBatchEntity.CODE);
    	List<RepertoryGoodsBatchEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
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
     * 查询列表
     * @param aoData
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list_and_goods")
    public String listAndGoods(@RequestParam String aoData) {
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere whereSql = new SQLWhere();
    	String querySql = "select goods_id,name,code,type,goods_batch_id,goods_batch_code,inventory,locking from (select b.goods_id,name,code,type,goods_batch_id,goods_batch_code,b.inventory,b.locking from t_repertory_goods_batch b left join t_repertory_goods g on g.goods_id = b.goods_id) as b UNION ALL select goods_id,name,code,type,goods_batch_id ,goods_batch_code,inventory,locking from (select goods_id,name,code, type,0 goods_batch_id,''  goods_batch_code,inventory,locking from t_repertory_goods) as g ";
    	
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsBatchEntity.GOODS_BATCH_CODE)){  
            	whereSql.and(new EQCnd(RepertoryGoodsBatchEntity.GOODS_BATCH_CODE,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsEntity.CODE)){
            	RepertoryGoodsEntity goods = new RepertoryGoodsEntity(jdbcDao);
            	goods = goods.setCode(jsonObject.get(VALUE).getAsString()).queryCustomCacheVo(0);
            	if(!PublicMethod.isEmptyValue(goods.getGoodsId())){
            		whereSql.and(new EQCnd(RepertoryGoodsBatchEntity.GOODS_ID,goods.getGoodsId()));
            	}else{
            		whereSql.and(new EQCnd(RepertoryGoodsBatchEntity.GOODS_ID,0));
            	}
            	
            }else if (jsonObject.get(NAME).getAsString().equals("type")){
            	int value = jsonObject.get(VALUE).getAsInt();
            	if(value == RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY || value == RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_SELL){
            		querySql = "select b.goods_id,name,code,type,goods_batch_id,goods_batch_code,b.inventory,b.locking from t_repertory_goods_batch b left join t_repertory_goods g on g.goods_id = b.goods_id  ";
                	
            	}
            }
    	}
    	
    	logger.error(aoData);
    	RepertoryGoodsBatchEntity entity = new RepertoryGoodsBatchEntity(jdbcDao);
    	
    	
    	long count = entity.getCount(querySql,whereSql);
    	
    	List<Map<String, Object>> list = new RepertoryGoodsBatchEntity(jdbcDao).getListVO(querySql,iDisplayStart, iDisplayLength, whereSql.orderBy(new DescSort(RepertoryGoodsBatchEntity.GOODS_ID,RepertoryGoodsBatchEntity.GOODS_BATCH_ID)));
    	
    	
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
    	RepertoryGoodsBatchEntity entity = new RepertoryGoodsBatchEntity(jdbcDao);
    	entity.setGoodsBatchId(id).loadVo();
        return success(entity);
    }

    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	RepertoryGoodsBatchEntity entity = new RepertoryGoodsBatchEntity(jdbcDao);
    	try{
    		entity.setGoodsBatchId(id).delete();
    		return success("删除成功");
    	}catch(Exception e){
    		logger.error("删除失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
   
    
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) {
    	RepertoryGoodsBatchEntity entity = new RepertoryGoodsBatchEntity(jdbcDao);
    	logger.error("-------"+aoData);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	
    	entity.parse(jo);
    	
    	
    	
    	try{
    		entity.update();
        	return success("修改成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) {
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	RepertoryGoodsBatchEntity entity = new RepertoryGoodsBatchEntity(jdbcDao);
    	entity.parse(jo);
    	if(jo.has(RepertoryGoodsEntity.CODE)){
    		RepertoryGoodsEntity goods = new RepertoryGoodsEntity(jdbcDao);
    		goods = goods.setCode(jo.get(RepertoryGoodsEntity.CODE).getAsString()).queryCustomCacheVo();
    		if(goods == null ){
    			return error("不存在的产品编码");
    		}
    		entity.setGoodsId(goods.getGoodsId());
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getGoodsBatchCode())){
    		return error("批次号不能为空");
    	}
    	
    	if(PublicMethod.isEmptyValue(entity.getGoodsId())){
    		return error("产品信息不能为空");
    	}
    	
    	try{
    		entity.insert();
        	return success("新增成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("新增失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
