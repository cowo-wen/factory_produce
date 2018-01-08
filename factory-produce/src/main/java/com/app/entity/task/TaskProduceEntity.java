/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.app.dao.JdbcDao;
import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;
import com.app.entity.repertory.RepertoryGoodsBatchEntity;
import com.app.entity.repertory.RepertoryGoodsComponentEntity;
import com.app.entity.repertory.RepertoryGoodsEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.app.util.WeixinMessageContainer;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.xx.util.string.Format;

/**
 * 功能说明：任务生产表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_task_produce")
@TableCache(isCache=true)
public class TaskProduceEntity extends CacheVo  implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7986852625158168654L;
	
	/**
	 * 等待开工
	 */
	public static final int PRODUC_STATUS_PREP = 3;
	
	/**
	 * 正在进行
	 */
	public static final int PRODUC_STATUS_WORKING = 5;
	
	/**
	 * 等待审核
	 */
	public static final int PRODUC_STATUS_CHECK_PENDING = 4;
	
	/**
	 * 完成
	 */
	public static final int PRODUC_STATUS_FINISH = 1;
	
	/**
	 * 返工
	 */
	public static final int PRODUC_STATUS_REDO = 2;
	
	/**
	 * 拒绝
	 */
	public static final int PRODUC_STATUS_REFUSE=6;
	
	
	/**
	 * 任务类型 生产
	 */
	public static final int TASK_TYPE_PRODUCE = 1;
	
	/**
	 * 任务类型 普通
	 */
	public static final int TASK_TYPE_GENERAL = 2;
	
	

	public static final String PRODUCE_ID = "produce_id";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long produceId;
	
	/**
	 * 批次代码
	 */
	public static final String GOODS_BATCH_CODE = "goods_batch_code";
    @Column
    @CustomCache(sort = 1)
    private String goodsBatchCode; 
    
	/**
	 * 产品id
	 */
	public static final String GOODS_ID = "goods_id";
	@CustomCache(sort = 0)
	@Column
	private Long goodsId;
    
	/**
	 * 生产数量
	 */
	public static final String AMOUNT = "amount";
    @Column
    private Integer amount;

    /**
     * 任务名称
     */
    public static final String PRODUCE_NAME = "produce_name";
    @Column
    private String produceName;
    
    /**
     * 任务类型 1为生产任务，2为普通任务
     */
    public static final String PRODUCE_TYPE = "produce_type";
    @CustomCache(sort = 0)
    @Column
    private Integer produceType;
    
    
    /**
     * 返工次数
     */
    public static final String REDO_NUMBER = "redo_number";
    @Column
    private Integer redoNumber;
    
    /**
     * 申领次数
     */
    public static final String APPLY_FOR_NUMBER = "apply_for_number";
    @Column
    private Integer applyForNumber;
    
    /**
     * 备注
     */
    public static final String REMARK = "remark";
    @Column
    private String remark;
    
    /**
     * 开始时间
     */
    public static final String BEGIN_TIME = "begin_time";
    @Column
    private Date beginTime;
    
    
    /**
     * 结束时间
     */
    public static final String END_TIME = "end_time";
    @Column
    private Date endTime;
    
    
    /**
     * 实际开始时间
     */
    public static final String START_TIME = "start_time";
    @Column
    private Date startTime;
    
    
    /**
     * 任务状态
     */
    public static final String STATUS = "status";
    @Column
    private Integer status;
    
    
    /**
     * 负责人
     */
    public static final String DIRECTOR = "director";
    @Column
    private String director;
    
    /**
     * 工钱
     */
    public static final String WAGES = "wages";
    @Column
    private Double wages;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;
    
    @Column
    private Long operatorUserId;

    
    
    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String NAME = "name";
    @Transient
    @Expose(deserialize = true)
    private String name;

    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String TYPE = "type";
    @Transient
    @Expose(deserialize = true)
    private Integer type;

    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String CODE = "code";
    @Transient
    @Expose(deserialize = true)
    private String code;
   
    /**
     * 负责人
     */
    public static final String DIRECTOR_NAME = "director_name";
    @Transient
    @Expose(deserialize = true)
    private String directorName;

	public TaskProduceEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
	}

	public Long getProduceId() {
		return produceId;
	}

	public TaskProduceEntity setProduceId(Long produceId) {
		this.produceId = produceId;
		return this;
	}

	public Integer getAmount() {
		return amount;
	}

	public TaskProduceEntity setAmount(Integer amount) {
		this.amount = amount;
		return this;
	}

	public String getProduceName() {
		return produceName;
	}

	public TaskProduceEntity setProduceName(String produceName) {
		this.produceName = produceName;
		return this;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public TaskProduceEntity setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
		return this;
	}

	public Date getEndTime() {
		return endTime;
	}

	public TaskProduceEntity setEndTime(Date endTime) {
		this.endTime = endTime;
		return this;
	}

	public Date getStartTime() {
		return startTime;
	}

	public TaskProduceEntity setStartTime(Date startTime) {
		this.startTime = startTime;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public TaskProduceEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public Double getWages() {
		return wages;
	}

	public TaskProduceEntity setWages(Double wages) {
		this.wages = wages;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public TaskProduceEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public TaskProduceEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public TaskProduceEntity setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
		return this;
	}

	public String getGoodsBatchCode() {
		return goodsBatchCode;
	}

	public TaskProduceEntity setGoodsBatchCode(String goodsBatchCode) {
		this.goodsBatchCode = goodsBatchCode;
		return this;
	}

	public String getName() {
		if(this.goodsId > 0){
			RepertoryGoodsEntity goods = new RepertoryGoodsEntity(this.jdbcDao);
			goods.setGoodsId(this.goodsId).loadVo();
			this.name = goods.getName();
			this.type = goods.getType();
			this.code = goods.getCode();
		}
		return name;
	}

	public TaskProduceEntity setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getType() {
		return type;
	}

	public TaskProduceEntity setType(Integer type) {
		this.type = type;
		return this;
	}

	public String getCode() {
		return code;
	}

	public TaskProduceEntity setCode(String code) {
		this.code = code;
		return this;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

	public Integer getRedoNumber() {
		return redoNumber;
	}

	public void setRedoNumber(Integer redoNumber) {
		this.redoNumber = redoNumber;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}
	
	public Integer getProduceType() {
		return produceType;
	}


	public TaskProduceEntity setProduceType(Integer produceType) {
		this.produceType = produceType;
		return this;
	}
	
	public Long getOperatorUserId() {
		return operatorUserId;
	}

	public TaskProduceEntity setOperatorUserId(Long operatorUserId) {
		this.operatorUserId = operatorUserId;
		
		return this;
	}
	
	
	
	

	public Integer getApplyForNumber() {
		return applyForNumber;
	}

	public TaskProduceEntity setApplyForNumber(Integer applyForNumber) {
		this.applyForNumber = applyForNumber;
		return this;
	}

	public String getDirectorName() {
		this.directorName ="";
		if(!PublicMethod.isEmptyStr(this.director)){
			String [] userId = this.director.split(",");
			for(String id : userId){
				SysUserEntity user = new SysUserEntity(jdbcDao);
				user.setUserId(Long.parseLong(id)).loadVo();
				if(!PublicMethod.isEmptyStr(user.getUserName())){
					this.directorName += user.getUserName()+" ";
				}
				
			}
		}
		return this.directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}

	@Override
	public long insert() throws Exception {
		if(PublicMethod.isEmptyStr(director)){
			throw new Exception("负责人不能为空");
		}
		
		if(this.produceType == TASK_TYPE_PRODUCE){
			RepertoryGoodsEntity goods = new RepertoryGoodsEntity(jdbcDao);
			goods.setGoodsId(this.goodsId).loadVo();
			if(PublicMethod.isEmptyStr(goods.getName())){
				throw new Exception("未设定的产品信息");
			}
		}else if(this.produceType != TASK_TYPE_GENERAL){
			throw new Exception("未知的任务类型");
		}
		this.redoNumber = 0;
		this.applyForNumber = 0;
		long id = super.insert();
		String[] userIds = director.split(",");
		if(userIds == null || userIds.length == 0){
			throw new Exception("未选择工人数据");
		}
		int num = amount/userIds.length;
		int num2 = amount%userIds.length;
		List<TaskWorkerEntity> listWorker = new ArrayList<TaskWorkerEntity>();
		StringBuilder userName = new StringBuilder();
		//StringBuilder proportion = new StringBuilder();
		for(String userId : userIds){
			if(Format.isNumeric(userId)){
				SysUserEntity user = new SysUserEntity(jdbcDao);
				user.setUserId(Long.parseLong(userId)).loadVo();
				if(PublicMethod.isEmptyStr(user.getUserName())){
					throw new Exception("不存在的工人数据");
				}
				userName.append(user.getUserName()).append(" ");
				TaskWorkerEntity worker = new TaskWorkerEntity(jdbcDao);
				worker.setUserId(user.getUserId());
				worker.setProduceId(id);
				worker.setValid(StaticBean.YES);
				worker.setNumber(num+num2);
				
				worker.insert();
				listWorker.add(worker);
				if(num2 > 0) num2 = 0;
			}else{
				throw new Exception("非法的工人数据");
			}
		}
		
		if(this.produceType == TASK_TYPE_PRODUCE){
			/**
			 * 查找所有组件
			 */
			List<RepertoryGoodsComponentEntity> list  = new RepertoryGoodsComponentEntity(jdbcDao).setGoodsId(goodsId).queryCustomCacheValue(0);
			if(list.size() > 0){
				for(RepertoryGoodsComponentEntity component : list){
					int number = amount * component.getNumber();
					RepertoryGoodsEntity goods_ = new RepertoryGoodsEntity(jdbcDao);
					goods_.setGoodsId(component.getComponentId()).loadVo();//查找组件的产品信息
					if(goods_.getInventory() - number < 0 ){
						throw new Exception("配件库存不足");
					}
					List<RepertoryGoodsBatchEntity> batchList = new RepertoryGoodsBatchEntity(jdbcDao).setGoodsId(component.getComponentId()).setValid(StaticBean.YES).queryCustomCacheValue(0);
					if(batchList.size() > 0 && number > 0){
						int value = number;
						for(RepertoryGoodsBatchEntity batch : batchList){
							if(batch.getInventory() > 0){
								TaskLockComponentEntity lockComponent = new TaskLockComponentEntity(jdbcDao);
								lockComponent.setGoodsBatchId(batch.getGoodsBatchId());
								lockComponent.setProduceId(id);
								lockComponent.setApplyForNumber(this.applyForNumber+1);//设置申领次数为1
								if(batch.getInventory() - value >= 0 ){
									lockComponent.setNumber(value);
									lockComponent.insert();
									batch.setInventory(batch.getInventory() - value);
									batch.setLocking(batch.getLocking() + value);
									batch.update(RepertoryGoodsBatchEntity.INVENTORY,RepertoryGoodsBatchEntity.LOCKING);
									value = 0;
									break;
								}else{
									value -= batch.getInventory();
									lockComponent.setNumber(batch.getInventory());
									lockComponent.insert();
									batch.setLocking(batch.getInventory());
									batch.setInventory(0);
									batch.update(RepertoryGoodsBatchEntity.INVENTORY,RepertoryGoodsBatchEntity.LOCKING);
								}
							}
							
						}
						if(value > 0 ){
							throw new Exception(goods_.getCode()+"库存不足或库存数据错误");
						}
						
					}else{
						throw new Exception(goods_.getCode()+"没有有效的批次信息");
					}
					
					
				}
			}
		}
		if(listWorker.size() > 0){//发送微信数据
			for(TaskWorkerEntity worker : listWorker){
				sendWechatMessage(worker,userName.toString(),null,null,null);
			}
		}
		return id;
	}
	
	
	/**
	 * 发送微信消息
	 * @param worker 工人对象
	 * @param userName 负责员工姓名
	 * @param remark
	 * @param content
	 */
	public void sendWechatMessage(TaskWorkerEntity worker,String userName,String remark,String content,String title){
		StringBuilder proportion = new StringBuilder();
		proportion.append(worker.getUserName()).append("负责:").append(worker.getNumber()).append(";");
		JsonObject jo = new JsonObject();
		jo.addProperty("user_id", worker.getUserId());
		if(PublicMethod.isEmptyStr(userName)){
			jo.addProperty("user_name", worker.getUserName());
		}else{
			jo.addProperty("user_name", userName);
		}
		jo.addProperty("date", PublicMethod.formatDateStr(new Date(), "MM-dd HH:mm"));
		jo.addProperty("remark", this.remark);
		if(PublicMethod.isEmptyStr(title)){
			jo.addProperty("first", produceName);
		}else{
			jo.addProperty("first", title);
		}
		
		if(PublicMethod.isEmptyStr(content)){
			if(produceType == 1){
				jo.addProperty("content", "生产任务;["+getName()+"]生产量"+this.amount+";工钱"+this.wages+";其中"+proportion.toString()+"请于"+PublicMethod.formatDateStr(this.beginTime, "MM月dd日HH:mm")+"至"+PublicMethod.formatDateStr(this.endTime, "MM月dd日HH:mm")+"完成");
			}else{
				jo.addProperty("content", "普通任务;生产量"+this.amount+";工钱"+this.wages+";其中"+proportion.toString()+"请于"+PublicMethod.formatDateStr(this.beginTime, "MM月dd日HH:mm")+"至"+PublicMethod.formatDateStr(this.endTime, "MM月dd日HH:mm")+"完成");
			}
		}else{
			jo.addProperty("content", content);
		}
		
		if(remark != null){
			jo.addProperty("remark", remark);
		}else{
			jo.addProperty("remark", this.remark);
		}
        WeixinMessageContainer.pushMessage(StaticBean.WEIXIN_MESSAGE_TYPE_TASK_PRODUCE_MESSAGE,worker.getUserId(),jo);
	}
	

	@Override
	public int delete() throws Exception {
		this.loadVo();
		if(this.status != PRODUC_STATUS_PREP){
			throw new Exception("已进行的任务不能删除");
		}
		
		List<TaskWorkerEntity> list = new TaskWorkerEntity(jdbcDao).setProduceId(this.produceId).queryCustomCacheValue(0);
		for(TaskWorkerEntity worker : list){
			worker.delete();
		}
		
		TaskLockComponentEntity lockComponent = new TaskLockComponentEntity(jdbcDao);
		List<TaskLockComponentEntity> componentList = lockComponent.setProduceId(this.produceId).queryCustomCacheValue(0);
		for(TaskLockComponentEntity component : componentList){
			RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(jdbcDao);
			batch.setGoodsBatchId(component.getGoodsBatchId()).loadVo();
			batch.setLocking(batch.getLocking() - component.getNumber());
			batch.setInventory(batch.getInventory() + component.getNumber());
			batch.update(RepertoryGoodsBatchEntity.INVENTORY,RepertoryGoodsBatchEntity.LOCKING);
			component.delete();
		}
		return super.delete();
	}

	@Override
	public int update(String... fieldName) throws Exception {
		return super.update(fieldName);
	}

	

	

	
    
    
    
}