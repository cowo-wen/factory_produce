package com.app.controller.v1.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.cnd.INCnd;
import com.app.dao.sql.cnd.LikeCnd;
import com.app.dao.sql.cnd.NotEQCnd;
import com.app.dao.sql.sort.AscSort;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.repertory.RepertoryGoodsBatchEntity;
import com.app.entity.repertory.RepertoryGoodsBillDetailEntity;
import com.app.entity.repertory.RepertoryGoodsBillEntity;
import com.app.entity.repertory.RepertoryGoodsComponentEntity;
import com.app.entity.repertory.RepertoryGoodsEntity;
import com.app.entity.sys.SysConfigEntity;
import com.app.entity.sys.SysRoleEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.entity.task.TaskLockComponentEntity;
import com.app.entity.task.TaskProduceEntity;
import com.app.entity.task.TaskReviewEntity;
import com.app.entity.task.TaskWorkerEntity;
import com.app.util.NetworkUtil;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
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
@RequestMapping("/v1/permission/task/produce")
@Scope("prototype")//设置成多例
public class ProduceAPI extends Result{
    public static Log logger = LogFactory.getLog(ProduceAPI.class);
    
    @Autowired  
    private HttpServletRequest request;
    
    /**
     * 查询任务列表
     * @param aoData
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/list")
    public String list(@RequestParam String aoData) {
    	
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	SQLWhere sql = new SQLWhere().orderBy(new DescSort(TaskProduceEntity.PRODUCE_ID));
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            else if (jsonObject.get(NAME).getAsString().equals(TaskProduceEntity.PRODUCE_NAME)){  
            	sql.and(new LikeCnd(TaskProduceEntity.PRODUCE_NAME,jsonObject.get(VALUE).getAsString()));
            }
    	}
    	
    	logger.error(aoData);
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	
    	entity.outPutOther(TaskProduceEntity.NAME,TaskProduceEntity.TYPE,TaskProduceEntity.CODE,TaskProduceEntity.DIRECTOR_NAME);
    	List<TaskProduceEntity> list = entity.getListVO(iDisplayStart, iDisplayLength, sql);
    	
    	
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
     * 查询工人列表
     * @param aoData
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/worker_list")
    public String worker_list(@RequestParam String aoData) {
    	
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            
    	}
    	
    	
    	SysConfigEntity config = new SysConfigEntity(jdbcDao).setGroupCode("worker_list").setGroupType("worker").queryCustomCacheVo(0, "worker");
    	StringBuilder sb= new StringBuilder();
    	if(config != null && !PublicMethod.isEmptyStr(config.getValue()) && config.getValid() == StaticBean.YES){
    		String[] ids = config.getValue().split(",");
    		if(ids.length > 0){
    			sb.append("select user_id from t_sys_user_role ").append(new SQLWhere(new INCnd("role_id","select role_id from t_sys_role "+new SQLWhere(new EQCnd(SysRoleEntity.VALID,StaticBean.YES)).and(new INCnd(SysRoleEntity.ROLE_CODE, ids)))));
    		}
    	}
    	
    	
    	SQLWhere sqlWhere = new SQLWhere(new EQCnd(SysUserEntity.VALID, StaticBean.YES));
    	if(sb.length() > 0){
    		sqlWhere.and(new INCnd(SysUserEntity.USER_ID, sb.toString()));
    	}
    	SysUserEntity user = new SysUserEntity(jdbcDao);
    	user.outPutField(SysUserEntity.USER_ID,SysUserEntity.USER_NAME,SysUserEntity.NUMBER);
    	List<SysUserEntity> list = user.getListVO(0, 10000, sqlWhere);
    	
    	
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", 0);
    	map.put("iTotalDisplayRecords", 0);
        return success(map);
    }
    
    
    
    
    /**
     * 查询需要的原料批次列表
     * @param aoData
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/material_list/{id}")
    public String materialList(@PathVariable("id") Long id,@RequestParam String aoData) {
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            
    	}
    	RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(jdbcDao);
    	SQLWhere sqlWhere = new SQLWhere(new INCnd(RepertoryGoodsBatchEntity.GOODS_ID, "select component_id from t_repertory_goods_component where goods_id in ( select goods_id from t_task_produce  where produce_id= "+id+")"));
    	
    	long count = batch.getCount(sqlWhere);
    	batch.outPutOther(RepertoryGoodsBatchEntity.NAME,RepertoryGoodsBatchEntity.TYPE,RepertoryGoodsBatchEntity.CODE);
    	List<RepertoryGoodsBatchEntity> list = batch.getListVO(iDisplayStart,iDisplayLength,sqlWhere.orderBy(new DescSort(RepertoryGoodsBatchEntity.GOODS_ID,RepertoryGoodsBatchEntity.GOODS_BATCH_ID)));
    	
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", count);
    	map.put("iTotalDisplayRecords", count);
        return success(map);
    	
    }
    
    
    
    
    
    /**
     * 查询补领数据
     * @param aoData
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/replenishvo/{id}")
    public String replenishVo(@PathVariable("id") Long id) {
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	entity.setProduceId(id).loadVo();
    	entity.getName();
    	int amount = 0;
    	if(entity.getStatus() == TaskProduceEntity.PRODUC_STATUS_REDO){
    		TaskReviewEntity review = new TaskReviewEntity(jdbcDao);
    		review = review.getMaxId(id);
    		if(review != null){
    			amount = review.getNumber();
    		}
    	}
    	
    	List<RepertoryGoodsBatchEntity> batchList2 = new ArrayList<RepertoryGoodsBatchEntity>();
    	
    	/**
		 * 查找所有组件
		 */
    	
		List<RepertoryGoodsComponentEntity> list  = new RepertoryGoodsComponentEntity(jdbcDao).setGoodsId(entity.getGoodsId()).queryCustomCacheValue(0);
		if(list.size() > 0 && amount > 0){
			for(RepertoryGoodsComponentEntity component : list){
				int number = amount * component.getNumber();
				RepertoryGoodsEntity goods_ = new RepertoryGoodsEntity(jdbcDao);
				goods_.setGoodsId(component.getComponentId()).loadVo();//查找组件的产品信息
				if(goods_.getInventory() - number < 0 ){
					RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(jdbcDao);
					batch.setGoodsId(goods_.getGoodsId());
					batch.setInventory(-1);//表示库存不足
					batch.setCode(goods_.getCode());
					batch.setName(goods_.getName());
					batch.getName();
					batchList2.add(batch);
					continue;
				}
				List<RepertoryGoodsBatchEntity> batchList = new RepertoryGoodsBatchEntity(jdbcDao).setGoodsId(component.getComponentId()).setValid(StaticBean.YES).queryCustomCacheValue(0);
				if(batchList.size() > 0 && number > 0){
					int value = number;
					for(RepertoryGoodsBatchEntity batch : batchList){
						batch.getName();
						if(batch.getInventory() > 0){
							if(batch.getInventory() - value >= 0 ){
								batch.setInventory(value);
								batchList2.add(batch);
								value = 0;
								break;
							}else{
								batchList2.add(batch);
								value -=batch.getInventory();
							}
						}
						
					}
					if(value > 0 ){//库存不足
						RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(jdbcDao);
						batch.getName();
						batch.setGoodsId(batchList.get(0).getGoodsId());
						batch.setInventory(-1);//表示库存不足
						batch.setName(batchList.get(0).getName());
						batch.setCode(batchList.get(0).getCode());
						
						batchList2.add(batch);
					}
					
				}
				
			}
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
    	map.put("data", entity);
    	map.put("batch_list", batchList2);
    	map.put("review_list", getCheckProduceData(id));//获取任务的审核数据
    	return success(map);
    }
    
    
    /**
     * 获取任务的审核数据
     * @param id
     * @return
     */
    private List<TaskReviewEntity> getCheckProduceData(Long id){
    	TaskReviewEntity review = new TaskReviewEntity(jdbcDao);
		review.outPutOther(TaskReviewEntity.USER_NAME);
		List<TaskReviewEntity> reviewList = review.getListVO(0, 1000, new SQLWhere(new EQCnd(TaskReviewEntity.PRODUCE_ID, id)).orderBy(new DescSort(TaskReviewEntity.REVIEW_ID)));
		return reviewList;
    }
    
    /**
     * 获取任务的工人数据
     * @param id
     * @return
     */
    private List<TaskWorkerEntity> getWorkerProduceData(Long id){
    	List<TaskWorkerEntity> list = new TaskWorkerEntity(jdbcDao).setProduceId(id).queryCustomCacheValue(0);
    	for(TaskWorkerEntity worker : list){
    		SysUserEntity user = new SysUserEntity(jdbcDao);
    		user.setUserId(worker.getUserId()).loadVo();
    		worker.setUserName(user.getUserName());
    		worker.setUserCode(user.getNumber());
    	}
    	return list;
    }
    
    /**
     * 获取任务的消耗原料数据
     * @param id
     * @return
     */
    private List<TaskLockComponentEntity> getMaterialProduceData(Long id){
    	TaskLockComponentEntity lockComponent = new TaskLockComponentEntity(jdbcDao);
    	lockComponent.outPutOther(TaskLockComponentEntity.GOODS_BATCH_CODE,TaskLockComponentEntity.CODE,TaskLockComponentEntity.NAME,TaskLockComponentEntity.TYPE,TaskLockComponentEntity.GOODS_ID);
    	List<TaskLockComponentEntity> list = lockComponent.setProduceId(id).queryCustomCacheValue(0);
    	
    	return list;
    }
    
    /**
     * 补充
     * @param aoData
     * @return
     */
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/replenish")
    public String replenish(@RequestParam String aoData) {
    	try{
	    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
	    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
	    	entity.parse(jo);
	    	
	    	
	    	long userId = getLoginUser().getUserId();
    		if((","+entity.getDirector()+",").indexOf(String.valueOf(","+userId+",")) == -1){
    			return error("没有权限申请");
    		}
	    	
	    	
	    	if(jo.has("batch_list") && !jo.get("batch_list").isJsonNull()){
	    		JsonArray ja = jo.get("batch_list").getAsJsonArray();
	    		if(ja.size() > 0){
	    			
	    			
	    			RepertoryGoodsBillEntity bill = new RepertoryGoodsBillEntity(jdbcDao);
	    			bill.setGoodsBatchCode(entity.getGoodsBatchCode()+"-"+PublicMethod.formatDateStr(new Date(), "yyyyMMddHHmmss"));
	    			
	    			bill.setProduceId(entity.getProduceId());
	    			bill.setLiableUser(userId);
	    			//bill.setGoodsBatchCode(entity.getGoodsBatchCode());
	    			bill.setType(RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY);
	    			bill.setTitle(entity.getProduceName()+"补领");
	    			long id = bill.insert();
	    			for(JsonElement je : ja){
	    				JsonObject object  = je.getAsJsonObject();
	    				TaskLockComponentEntity lockComponent = new TaskLockComponentEntity(jdbcDao);
	    				lockComponent.setGoodsBatchId(object.get(TaskLockComponentEntity.GOODS_BATCH_ID).getAsLong());
	    				lockComponent.setProduceId(entity.getProduceId());
	    				lockComponent.setNumber(object.get("value").getAsInt());
	    				long lcId = lockComponent.insert();
	    				RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(jdbcDao);
	    				batch.setGoodsBatchId(lockComponent.getGoodsBatchId()).loadVo();
	    				RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(jdbcDao);
	    				detail.setGoodsBatchId(lockComponent.getGoodsBatchId());
	    				detail.setGoodsId(batch.getGoodsId());
	    				detail.setNumber(object.get("value").getAsInt());
	    				detail.setType(bill.getType());
	    				detail.setGoodsBillId(id);
	    				detail.setLockComponentId(lcId);
	    				detail.insert();
	    			
	    			}
	    			
	    		}
	    		
	    	}
	    	entity.setStatus(TaskProduceEntity.PRODUC_STATUS_WORKING);
	    	entity.update(TaskProduceEntity.STATUS);
        	return success("补领成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("补领失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    /**
     * 查询产品列表
     * @param aoData
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/goods_list")
    public String goods_list(@RequestParam String aoData) {
    	
    	JsonArray jo = new JsonParser().parse(aoData).getAsJsonArray();
    	for(JsonElement je : jo){
    		JsonObject jsonObject = je.getAsJsonObject();
    		if (jsonObject.get(NAME).getAsString().equals(S_ECHO))  
                sEcho = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_START))  
                iDisplayStart = jsonObject.get(VALUE).getAsInt();  
            else if (jsonObject.get(NAME).getAsString().equals(I_DISPLAY_LENGTH))  
                iDisplayLength = jsonObject.get(VALUE).getAsInt(); 
            
    	}
    	
    	
    	RepertoryGoodsEntity goods = new RepertoryGoodsEntity(jdbcDao);
    	goods.outPutField(RepertoryGoodsEntity.GOODS_ID,RepertoryGoodsEntity.CODE,RepertoryGoodsEntity.NAME,RepertoryGoodsEntity.PRODUCE_PRICE);
    	List<RepertoryGoodsEntity> list =  goods.getListVO(new SQLWhere(new NotEQCnd(RepertoryGoodsEntity.TYPE,RepertoryGoodsEntity.GOOD_TYPE_MATERIAL)).orderBy(new AscSort(RepertoryGoodsEntity.TYPE,RepertoryGoodsEntity.GOODS_ID)));
    	
    	
    	
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("status", 200);
    	map.put("data", list);
    	map.put("sEcho", ++sEcho);
    	map.put("iTotalRecords", 0);
    	map.put("iTotalDisplayRecords", 0);
        return success(map);
    }
    
    
    
    
    
    /**
     * 拒绝任务
     * @param id
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST },value="/refuse/{id}")
    public String refuse(@PathVariable("id") Long id,@RequestParam String application_code) {
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	
    	entity.setProduceId(id).loadVo();
    	long userId = getLoginUser().getUserId();
		if((","+entity.getDirector()+",").indexOf(String.valueOf(","+userId+",")) == -1){
			return error("没有权限拒绝");
		}
		try{
			entity.setStatus(TaskProduceEntity.PRODUC_STATUS_REFUSE);//更新状态为拒绝
    		entity.update(TaskProduceEntity.STATUS);
			insertLog(application_code,id,NetworkUtil.getIpAddress(request),"");//插入日志
			return success("成功");
		}catch(Exception e){
			return error("失败:"+e.getMessage());
		}
		
    	
    	
    }
    
    /**
     * 提交完成
     * @param id
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST },value="/complete/{id}")
    public String complete(@PathVariable("id") Long id,@RequestParam String application_code) {
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	
    	entity.setProduceId(id).loadVo();
    	try{
    		if(entity.getStatus() != TaskProduceEntity.PRODUC_STATUS_WORKING && 
    				entity.getStatus() != TaskProduceEntity.PRODUC_STATUS_REDO){
    			return error("该任务现在不能提交完成");
    		}
    		long userId = getLoginUser().getUserId();
    		if((","+entity.getDirector()+",").indexOf(String.valueOf(","+userId+",")) == -1){
    			return error("没有权限提交完成");
    		}
    		insertLog(application_code,id,NetworkUtil.getIpAddress(request),"");//插入日志
    		entity.setStatus(TaskProduceEntity.PRODUC_STATUS_CHECK_PENDING);//更新状态正在进行
    		entity.update(TaskProduceEntity.STATUS);
    		return success("提交成功");
    	}catch(Exception e){
    		return error("提交失败");
    	}
        
    }
    
    /**
     * 审核数据
     * @param aoData
     * @return
     */
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/check")
    public String check(@RequestParam String aoData) {
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	if(jo.has(TaskProduceEntity.PRODUCE_ID)){
    		entity.setProduceId(jo.get(TaskProduceEntity.PRODUCE_ID).getAsLong()).loadVo();
    		if(PublicMethod.isEmptyStr(entity.getGoodsBatchCode())){
    			return error("不存在的数据");
    		}else if(entity.getStatus() != TaskProduceEntity.PRODUC_STATUS_CHECK_PENDING){
    			return error("该数据未到审核流程");
    		}
    		
    	}else{
    		return error("主键不能为空");
    	}
    	
    	entity.parse(jo,1);
    	try{
    		TaskReviewEntity review = new TaskReviewEntity(jdbcDao);
    		review.setProduceId(entity.getProduceId());//设置产品id
    		review.setUserId(getLoginUser().getUserId());//设置用户
    		if(jo.has(TaskReviewEntity.REMARK)){
    			review.setRemark(jo.get(TaskReviewEntity.REMARK).getAsString());//设置备注
    		}
    		if(entity.getStatus() == TaskProduceEntity.PRODUC_STATUS_FINISH && entity.getProduceType() == 1){//处理完成
    			review.setNumber(0);//该处地方需要提交入库逻辑
    			RepertoryGoodsBillEntity goodsBill = new RepertoryGoodsBillEntity(jdbcDao);
    			goodsBill.setType(RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_PRODUCE);
    			goodsBill.setGoodsBatchCode(entity.getGoodsBatchCode());
    			goodsBill.setLiableUser(review.getUserId());
    			goodsBill.setProduceId(entity.getProduceId());
    			goodsBill.setTitle(entity.getName()+"生产批次"+entity.getGoodsBatchCode()+"生产入库");
    			long id = goodsBill.insert();
    			RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(jdbcDao);
    			detail.setGoodsId(entity.getGoodsId());
    			detail.setNumber(entity.getAmount());
    			detail.setGoodsBillId(id);
    			detail.setType(goodsBill.getType());//生产单
    			detail.insert();
    			
    			
    		}else if(entity.getStatus() == TaskProduceEntity.PRODUC_STATUS_REDO){//返工
    			if(jo.has(TaskReviewEntity.NUMBER) && !jo.get(TaskReviewEntity.NUMBER).isJsonNull()){
    				if(jo.get(TaskReviewEntity.NUMBER).getAsInt() <= 0){
    					return error("返工数量不能小于或等于0");
    				}else if(jo.get(TaskReviewEntity.NUMBER).getAsInt() > entity.getAmount()){
    					return error("返工数量不能大于生产量");
    				}
    				review.setNumber(jo.get(TaskReviewEntity.NUMBER).getAsInt());//设置不合格数
    			}else{
    				return error("返工数量不能为空");
    			}
    		}else{
    			return error("处理结果不对");
    		}
    		review.setStatus(entity.getStatus());
    		review.insert();
    		entity.update(TaskProduceEntity.STATUS);
        	return success("修改成功",entity.getProduceId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
    /**
     * 申领
     * @param id
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST },value="/apply_for/{id}")
    public String applyFor(@PathVariable("id") Long id) {
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	
    	entity.setProduceId(id).loadVo();
    	try{
    		if(entity.getStatus() != TaskProduceEntity.PRODUC_STATUS_PREP){
    			return error("该任务已过申领流程");
    		}
    		
    		long userId = getLoginUser().getUserId();
    		if((","+entity.getDirector()+",").indexOf(String.valueOf(","+userId+",")) == -1){
    			return error("没有权限申领");
    		}
    		
    		if(entity.getBeginTime().getTime() > System.currentTimeMillis()){
    			return error("任务未开始,不能领料");
    		}
    		List<TaskLockComponentEntity> list = getMaterialProduceData(id);
    		if(list != null && list.size() > 0){
    			RepertoryGoodsBillEntity goodsBill = new RepertoryGoodsBillEntity(jdbcDao);
        		goodsBill.setProduceId(id);
        		goodsBill.setType(RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY);
        		goodsBill.setGoodsBatchCode(entity.getGoodsBatchCode());
        		goodsBill.setCheckStatus(StaticBean.WAIT);
        		goodsBill.setLiableUser(userId);
        		goodsBill.setTitle(entity.getProduceName());
        		Long billId = goodsBill.insert();
        		for(TaskLockComponentEntity component : list){
        			RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(jdbcDao);
        			detail.setGoodsBillId(billId);
        			detail.setGoodsBatchId(component.getGoodsBatchId());
        			detail.setGoodsId(component.getGoodsId());
        			detail.setNumber(component.getNumber());
        			detail.setLockComponentId(component.getLockComponentId());
        			detail.applyInsert();
        		}
    		}
    		
    		entity.setStatus(TaskProduceEntity.PRODUC_STATUS_WORKING);//更新状态正在进行
    		entity.setStartTime(new Date());
    		entity.update(TaskProduceEntity.STATUS,TaskProduceEntity.START_TIME);
    		return success("提交申领成功");
    	}catch(Exception e){
    		return error("提交申领失败");
    	}
        
    }
    
    /**
     * 获取单个对象
     * @param id
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/vo/{id}")
    public String vo(@PathVariable("id") Long id) {
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	
    	entity.setProduceId(id).loadVo();
    	entity.getName();
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("data", entity);
    	map.put("worker", getWorkerProduceData(id));
        return success(map);
    }
    
    /**
     * 获取详细数据
     * @param id
     * @return
     * @
     */
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },value="/view/{id}")
    public String view(@PathVariable("id") Long id) {
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	
    	entity.setProduceId(id).loadVo();
    	entity.getName();
    	Map<String,Object> map = new HashMap<String,Object>();
    	map.put("data", entity);
    	map.put("worker_list", getWorkerProduceData(id));
    	map.put("material_list", getMaterialProduceData(id));
    	map.put("review_list", getCheckProduceData(id));//获取任务的审核数据
        return success(map);
    }


    
    /**
     * 删除任务
     * @param id
     * @return
     */
    @RequestMapping(method=RequestMethod.DELETE,value="/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	try{
    		entity.setProduceId(id).delete();
    		return success("删除成功");
    	}catch(Exception e){
    		logger.error("删除失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
   
    
    
    /**
     * 更新任务
     * @param aoData
     * @return
     */
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/update")
    public String update(@RequestParam String aoData) {
    	try{
	    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
	    	logger.error("-------"+aoData);
	    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
	    	//jo.addProperty("begin_time", jo.get("begin_time").getAsString()+" 00:00:00");
	    	//jo.addProperty("end_time", jo.get("end_time").getAsString()+" 23:59:59");
	    	entity.parse(jo);
	    	
	    	if(entity.getStatus() != TaskProduceEntity.PRODUC_STATUS_PREP && entity.getStatus() != TaskProduceEntity.PRODUC_STATUS_REFUSE){
	    		return error("任务已进行生产，不能修改");
	    	}
	    	
	    	if(PublicMethod.isEmptyStr(entity.getGoodsBatchCode())){
	    		return error("批次号不能为空");
	    	}
	    	
	    	if(PublicMethod.isEmptyValue(entity.getGoodsId())){
	    		return error("产品不能为空");
	    	}
	    	
	    	if(PublicMethod.isEmptyValue(entity.getBeginTime(),entity.getEndTime())){
	    		return error("开始日期与结束日期不能为空");
	    	}
	    	
	    	
	    	
	    	if(PublicMethod.isEmptyValue(entity.getAmount(),entity.getWages())){
	    		return error("产量及工钱不能为空");
	    	}else if(entity.getAmount() <=0 || entity.getWages() <= 0){
	    		return error("产量及工钱不能小于或等于0");
	    	}
	    	
	    	List<TaskWorkerEntity> list = new TaskWorkerEntity(jdbcDao).setProduceId(entity.getProduceId()).queryCustomCacheValue(0);
	    	
	    	Map<Long,TaskWorkerEntity> map = new HashMap<Long,TaskWorkerEntity>();
	    	for(TaskWorkerEntity worker : list){
	    		map.put(worker.getUserId(), worker);
	    	}
	    	if(jo.has("worker_list") && !jo.get("worker_list").isJsonNull()){
	    		JsonArray ja = jo.get("worker_list").getAsJsonArray();
	    		if(ja.size() > 0){
	    			int current = 0;
	    			for(JsonElement je : ja){
	    				JsonObject object  = je.getAsJsonObject();
	    				TaskWorkerEntity worker = new TaskWorkerEntity(jdbcDao);
	    				worker.setProduceId(entity.getProduceId());
	    				worker.setUserId(object.get("user_id").getAsLong());
	    				worker.setValid(StaticBean.YES);
	    				worker.setNumber(object.get("value").getAsInt());
	    				if(map.containsKey(worker.getUserId())){
	    					if(map.get(worker.getUserId()).getNumber() != worker.getNumber()){
	    						map.get(worker.getUserId()).setNumber(worker.getNumber()).update(TaskWorkerEntity.NUMBER);
	    					}
	    					map.remove(worker.getUserId());
	    				}else{
	    					worker.insert();
	    				}
	    				
	    				current += worker.getNumber();
	    			}
	    			if(current != entity.getAmount()){
	    				return error("工人生产量总和不等于生产量");
	    			}
	    		}else{
	    			return error("未选择生产工人");
	    		}
	    	}else{
	    		return error("工人不能为空");
	    	}
	    	if(map.size() > 0){
	    		for(Map.Entry<Long, TaskWorkerEntity> kv : map.entrySet()){
	    			kv.getValue().delete();
	    		}
	    	}
    		entity.update();
        	return success("修改成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("修改失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    /**
     * 新增任务
     * @param aoData
     * @return
     */
    
    @RequestMapping(method={ RequestMethod.POST, RequestMethod.PUT },value="/add")
    public String add(@RequestParam String aoData) {
    	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
    	TaskProduceEntity entity = new TaskProduceEntity(jdbcDao);
    	//jo.addProperty("begin_time", jo.get("begin_time").getAsString()+" 00:00:00");
    	//jo.addProperty("end_time", jo.get("end_time").getAsString()+" 23:59:59");
    	entity.parse(jo);
    	
    	
    	if(PublicMethod.isEmptyStr(entity.getGoodsBatchCode())){
    		return error("批次号不能为空");
    	}
    	if(entity.getProduceType() == 1){
    		if(PublicMethod.isEmptyValue(entity.getGoodsId())){
        		return error("产品不能为空");
        	}
    	}else if(entity.getProduceType() == 2){
    		entity.setGoodsId(0L);
    	}else{
    		return error("未知的任务类型");
    	}
    	
    	
    	if(PublicMethod.isEmptyValue(entity.getBeginTime(),entity.getEndTime())){
    		return error("开始日期与结束日期不能为空");
    	}
    	
    	
    	
    	if(PublicMethod.isEmptyValue(entity.getAmount(),entity.getWages())){
    		return error("产量及工钱不能为空");
    	}else if(entity.getAmount() <=0 || entity.getWages() <= 0){
    		return error("产量及工钱不能小于或等于0");
    	}
    	
    	
    	
    	try{
    		
    		if(jo.has("worker_ids") && !jo.get("worker_ids").isJsonNull()){
    			entity.setDirector(jo.get("worker_ids").getAsString());
    		}else{
    			return error("请选择生产工人");
    		}
    		entity.setStatus(TaskProduceEntity.PRODUC_STATUS_PREP);
    		entity.setOperatorUserId(getLoginUser().getUserId());
    		entity.insert();
        	return success("新增成功",entity.getGoodsId());
    	}catch(Exception e){
    		logger.error("新增失败", e);
    		return error(e.getMessage());
    	}
    	
    }
    
    
}
