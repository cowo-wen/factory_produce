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
import com.app.dao.sql.cnd.INCnd;
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.cnd.NotEQCnd;
import com.app.dao.sql.cnd.NotINCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.repertory.RepertoryGoodsComponentEntity;
import com.app.entity.repertory.RepertoryGoodsEntity;
import com.app.util.PublicMethod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 功能说明：产品信息
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/v1/permission/repertory/goods")
@Scope("prototype")//设置成多例
public class GoodsAPI extends Result{
    public static Log logger = LogFactory.getLog(GoodsAPI.class);
    
  
    /**
     * 查询产品信息列表
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) throws Exception{
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(RepertoryGoodsEntity.GOODS_ID));
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))
                sEcho = jsonObject.get(VALUE).getAsInt();
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsEntity.NAME)){  
            	sql.and(new LikeCnd(RepertoryGoodsEntity.NAME,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsEntity.CODE)){
            	sql.and(new LikeCnd(RepertoryGoodsEntity.CODE,jsonObject.get(VALUE).getAsString()));
            }
    	}
    	
    	logger.error(aoData);
    	RepertoryGoodsEntity entity = new RepertoryGoodsEntity(jdbcDao);
    	
    	
    	List<RepertoryGoodsEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
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
     * 获取非成品的产品数据
     * @param aoData
     * @return
     * @throws Exception
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/no_finished_list")
    public String noFinishedList(@RequestParam String aoData) throws Exception{
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere(new INCnd(RepertoryGoodsEntity.TYPE, new Integer[]{RepertoryGoodsEntity.GOOD_TYPE_MATERIAL,RepertoryGoodsEntity.GOOD_TYPE_SEMI_PRODUCT})).orderBy(new DescSort(RepertoryGoodsEntity.TYPE,RepertoryGoodsEntity.GOODS_ID));
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))
                sEcho = jsonObject.get(VALUE).getAsInt();
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsEntity.NAME)){  
            	sql.and(new LikeCnd(RepertoryGoodsEntity.NAME,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsEntity.CODE)){
            	sql.and(new LikeCnd(RepertoryGoodsEntity.CODE,jsonObject.get(VALUE).getAsString()));
            }else if (jsonObject.get(NAME).getAsString().equals(RepertoryGoodsEntity.CODE)){
            	sql.and(new LikeCnd(RepertoryGoodsEntity.CODE,jsonObject.get(VALUE).getAsString()));
            }else if(jsonObject.get(NAME).getAsString().equals(RepertoryGoodsEntity.GOODS_ID)){
            	sql.and(new NotEQCnd(RepertoryGoodsEntity.GOODS_ID,jsonObject.get(VALUE).getAsLong())).and(new NotINCnd(RepertoryGoodsEntity.GOODS_ID,"select component_id from t_repertory_goods_component where goods_id="+jsonObject.get(VALUE).getAsLong()));
            }
    	}
    	
    	logger.error(aoData);
    	RepertoryGoodsEntity entity = new RepertoryGoodsEntity(jdbcDao);
    	List<RepertoryGoodsEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
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
    	RepertoryGoodsEntity entity = new RepertoryGoodsEntity(jdbcDao);
    	entity.setGoodsId(id).loadVo();
        return success(entity);
    }
    
    
    
    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	RepertoryGoodsEntity entity = new RepertoryGoodsEntity(jdbcDao);
    	try{
    		entity.setGoodsId(id).loadVo();
    		if(entity.getInventory() > 0 || entity.getLocking() > 0){
    			return error("库存大于0，不能为删除");
    		}
    		List<RepertoryGoodsComponentEntity>  list = new RepertoryGoodsComponentEntity(jdbcDao).setGoodsId(id).queryCustomCacheValue(0, null);
    		if(list != null && list.size() > 0){
    			for(RepertoryGoodsComponentEntity gc : list){
    				gc.delete();
    			}
    		}
    		entity.setGoodsId(id).delete();
    		return success("删除成功");
    	}catch(Exception e){
    		logger.error("删除失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
   
    
    
    
    /**
     * 修改产品信息
     * 此时是否需要考滤将成品修改为原料时，
     * 产品数据是否清除产品零件表的数据
     * @param aoData
     * @return
     * @throws Exception
     */
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) throws Exception{
    	RepertoryGoodsEntity entity = new RepertoryGoodsEntity(jdbcDao);
    	logger.error("-------"+aoData);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	jo.remove(RepertoryGoodsEntity.LOCKING);//去掉库存
    	jo.remove(RepertoryGoodsEntity.INVENTORY);//去掉锁定
    	jo.remove("inventory");//去掉库存
    	jo.remove("locking");//去掉锁定
    	entity.parse(jo);
    	
    	if(PublicMethod.isEmptyStr(entity.getName())){
    		return error("产品名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getCode())){
    		return error("产品编号不能为空");
    	}
    	
    	
    	try{
    		
    		entity.update();
        	return success("修改成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
    /**
     * 新增产品信息
     * @param aoData
     * @return
     * @throws Exception
     */
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) throws Exception{
    	RepertoryGoodsEntity entity = new RepertoryGoodsEntity(jdbcDao);
    	entity.parse(new JsonParser().parse(aoData).getAsJsonObject());
    	logger.error("aoData="+aoData);
    	if(PublicMethod.isEmptyStr(entity.getName())){
    		return error("产品名称不能为空");
    	}
    	
    	if(PublicMethod.isEmptyStr(entity.getCode())){
    		return error("产品编号不能为空");
    	}
    	entity.setInventory(0L);
    	entity.setLocking(0L);
    	try{
    		entity.insert();
        	return success("新增成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("新增失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
