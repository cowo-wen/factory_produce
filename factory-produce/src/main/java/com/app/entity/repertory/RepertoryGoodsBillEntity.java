/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.repertory;

import java.io.Serializable;
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
import com.app.entity.business.BusinessCheckLogEntity;
import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.app.util.StaticBean;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：产品帐单
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_repertory_goods_bill")
@TableCache(isCache=true)
public class RepertoryGoodsBillEntity extends CacheVo  implements Serializable
{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -7587404743759862803L;
	/**
     * 盘点
     */
    public static final int GOODS_DETAIL_TYPE_CHECK = 0;
    /**
     * 出货
     */
    public static final int GOODS_DETAIL_TYPE_SELL = -1;
    /**
     * 入货
     */
    public static final int GOODS_DETAIL_TYPE_BUY = 1;
    
    /**
     * 生产单
     */
    public static final int GOODS_DETAIL_TYPE_PRODUCE = 2;
    
    /**
     * 申领单
     */
    public static final int GOODS_DETAIL_TYPE_APPLY = -2;
	
    
    public static final String GOODS_BILL_ID = "goods_bill_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long goodsBillId;
    
    /**
   	 * 批次代码
   	 */
   	public static final String GOODS_BATCH_CODE = "goods_batch_code";
    @Column
    @CustomCache(sort=0)
    private String goodsBatchCode;
    
    
    /**
   	 * 备注
   	 */
   	public static final String REMARK = "remark";
    @Column
    private String remark;
    
    /**
     * 审核状态 1已审核，3未审核，2审核不通过
     */
    public static final String CHECK_STATUS = "check_status";
    @Column
    private Integer checkStatus;
    
    
    /**
     * 审核人
     */
    public static final String CHECK_USER = "check_user";
    @Column
    private Long checkUser;
    
    /**
     * 责任人
     */
    public static final String LIABLE_USER = "liable_user";
    @Column
    private Long liableUser;
    
    /**
     * 审核时间
     */
    public static final String CHECK_TIME = "check_time";
    @Column
    private String checkTime;
    
    /**
     * 审核备注
     */
    public static final String CHECK_REMARK = "check_remark";
    @Column
    private String checkRemark;
    
    /**
     * 产品类型：盘点，出货，入货
     */
    public static final String TYPE = "type";
    @Column
    @CustomCache(sort=1)
    private Integer type;
    
    public static final String PRODUCE_ID = "produce_id";
    @Column
    private Long produceId;
    
    /**
     * 单据名称
     */
    public static final String TITLE = "title";
    @Column
    private String title;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

	
    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String CHECK_USER_NAME = "check_user_name";
    @Transient
    @Expose(deserialize = true)
    private String checkUserName;
    
    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String LIABLE_USER_NAME = "liable_user_name";
    @Transient
    @Expose(deserialize = true)
    private String liableUserName;

	

	public RepertoryGoodsBillEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
		// TODO Auto-generated constructor stub
	}

	public Date getCreateTime() {
		return createTime;
	}

	public RepertoryGoodsBillEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public RepertoryGoodsBillEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

	public Long getGoodsBillId() {
		return goodsBillId;
	}

	public RepertoryGoodsBillEntity setGoodsBillId(Long goodsBillId) {
		this.goodsBillId = goodsBillId;
		return this;
	}

	public String getGoodsBatchCode() {
		return goodsBatchCode;
	}

	public void setGoodsBatchCode(String goodsBatchCode) {
		this.goodsBatchCode = goodsBatchCode;
	}

	public Integer getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Long getCheckUser() {
		return checkUser;
	}

	public void setCheckUser(Long checkUser) {
		this.checkUser = checkUser;
	}

	public Long getLiableUser() {
		return liableUser;
	}

	public void setLiableUser(Long liableUser) {
		this.liableUser = liableUser;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	
	public String getCheckRemark() {
		return checkRemark;
	}

	public void setCheckRemark(String checkRemark) {
		this.checkRemark = checkRemark;
	}

	public String getCheckUserName() {
		if(!PublicMethod.isEmptyValue(checkUser)){
			SysUserEntity user = new SysUserEntity(this.jdbcDao);
			user.setUserId(checkUser).loadVo();
			checkUserName = user.getUserName();
		}
		return checkUserName;
	}

	public void setCheckUserName(String checkUserName) {
		this.checkUserName = checkUserName;
	}

	public String getLiableUserName() {
		if(!PublicMethod.isEmptyValue(liableUser)){
			SysUserEntity user = new SysUserEntity(this.jdbcDao);
			user.setUserId(liableUser).loadVo();
			liableUserName = user.getUserName();
		}
		return liableUserName;
	}

	public void setLiableUserName(String liableUserName) {
		this.liableUserName = liableUserName;
	}

	@Override
	public long insert() throws Exception {
		
		
		if(PublicMethod.isEmptyStr(title)){
			throw new Exception("名称不能为空");
		}
		
		if(PublicMethod.isEmptyStr(goodsBatchCode)){
			throw new Exception("批次号不能为空");
		}
		checkStatus = StaticBean.WAIT;
		return super.insert();
	}

	@Override
	public int delete() throws Exception {
		RepertoryGoodsBillEntity bill = new RepertoryGoodsBillEntity(this.jdbcDao);
		bill.setGoodsBillId(this.goodsBillId);
		bill.loadVo();
		if(bill.checkStatus == StaticBean.YES){
			throw new Exception("审核通过的数据不能删除");
		}
		
		RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(this.jdbcDao);
		detail.setGoodsBillId(this.goodsBillId);
		List<RepertoryGoodsBillDetailEntity> list = detail.queryCustomCacheValue(0);
		if(list != null && list.size() > 0){
			for(RepertoryGoodsBillDetailEntity obj : list){
				obj.setType(bill.getType());
				obj.delete();
			}
		}
		return super.delete();
	}

	@Override
	public int update(String... fieldName) throws Exception {
		RepertoryGoodsBillEntity bill = new RepertoryGoodsBillEntity(this.jdbcDao);
		bill.setGoodsBillId(this.goodsBillId);
		bill.loadVo();
		if(bill.checkStatus == StaticBean.YES){
			throw new Exception("审核通过的数据不能修改");
		}
		
		if(bill.checkStatus != checkStatus){
			Date date = new Date();
			if(checkStatus == StaticBean.YES){
				checkTime = PublicMethod.formatDateStr(date);
			}else if(checkStatus == StaticBean.NO){
				BusinessCheckLogEntity log = new BusinessCheckLogEntity();
				log.setRemark(checkRemark);
				log.setType(BusinessCheckLogEntity.TYPE_REPERTORY_GOODS_BILL);
				log.setCheckStatus(checkStatus);
				log.setDataId(goodsBillId);
				log.setCheckTime(date);
				log.setCheckUser(checkUser);
				log.setNumber(0L);
				log.insert();
				checkTime = PublicMethod.formatDateStr(date);
			}
		}else if(bill.checkStatus == StaticBean.NO){
			checkStatus = StaticBean.WAIT;
		}
		int id = super.update(fieldName);
		if(this.checkStatus == StaticBean.YES){//审核通过
			RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(this.jdbcDao);
			detail.setGoodsBillId(this.goodsBillId);
			List<RepertoryGoodsBillDetailEntity> list = detail.queryCustomCacheValue(0);
			if(list != null && list.size() > 0){
				switch(type){
					case GOODS_DETAIL_TYPE_CHECK://盘点
						for(RepertoryGoodsBillDetailEntity obj : list){
							RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);//更新批次信息
							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
								batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
								batch.setInventory(batch.getInventory()+obj.getNumber());//批次库存+-
								batch.update();
							}else{
								batch.setInventory(obj.getNumber());//批次库存+-
								batch.setGoodsId(obj.getGoodsId());
								batch.setGoodsBatchCode(goodsBatchCode);
								batch.insert();
							}
							
							
						}
						break;
					case GOODS_DETAIL_TYPE_APPLY://申领 批次锁定-
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
					case GOODS_DETAIL_TYPE_BUY://入库
						for(RepertoryGoodsBillDetailEntity obj : list){
							RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
								batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
								batch.setInventory(batch.getInventory()+obj.getNumber());//批次库存+-
								batch.update();
								
							}else{
								batch.setInventory(obj.getNumber());
								batch.setGoodsId(obj.getGoodsId());
								batch.setGoodsBatchCode(goodsBatchCode);
								batch.insert();
							}
							
						}
						break;
					case GOODS_DETAIL_TYPE_PRODUCE://生产
						for(RepertoryGoodsBillDetailEntity obj : list){
							if(!PublicMethod.isEmptyValue(obj.getGoodsBatchId())){
								RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
								batch.setGoodsBatchId(obj.getGoodsBatchId()).loadVo();
								batch.setInventory(batch.getInventory()+obj.getNumber());
								batch.update();
							}else{
								RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
								batch.setGoodsBatchCode(goodsBatchCode);
								batch.setGoodsId(obj.getGoodsId());
								batch.setInventory(obj.getNumber());
								batch.insert();
							}
							
						}
						break;
					case GOODS_DETAIL_TYPE_SELL://出货
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
			}
			
			
		}
		return id;
	}

	public Long getProduceId() {
		return produceId;
	}

	public void setProduceId(Long produceId) {
		this.produceId = produceId;
	}

    
    
}