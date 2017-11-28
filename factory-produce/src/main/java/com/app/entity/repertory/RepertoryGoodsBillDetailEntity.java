/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.repertory;

import java.io.Serializable;
import java.util.Date;

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
import com.app.util.PublicMethod;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：帐单产品明细表
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_repertory_goods_bill_detail")
@TableCache(isCache=true)
public class RepertoryGoodsBillDetailEntity extends CacheVo  implements Serializable
{
    
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3759499270605892298L;
	
	
    
    public static final String BILL_DETAIL_ID = "bill_detail_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @CustomCache(hashKey=true,sort=1)
    private Long billDetailId;
    
    
    public static final String GOODS_BILL_ID = "goods_bill_id";
    @Column
    @CustomCache( sort = 0)
    private Long goodsBillId;
    
    /**
     * 产品id
     */
    public static final String GOODS_ID = "goods_id";
    @Column
    private Long goodsId;
    
    
    /**
	 * 批次id
	 */
	public static final String GOODS_BATCH_ID = "goods_batch_id";
    @Column
    private Long goodsBatchId;
    
    /**
     * 数量
     */
    public static final String NUMBER = "number";
    @Column
    private Integer number;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

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
    public static final String NAME = "name";
    @Transient
    @Expose(deserialize = true)
    private String name;

    /**
	 * 批次代码
	 */
	public static final String GOODS_BATCH_CODE = "goods_batch_code";
	@Transient
	@Expose(deserialize = true)
    private String goodsBatchCode;

    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String CODE = "code";
    @Transient
    @Expose(deserialize = true)
    private String code;

	
    
    
	public RepertoryGoodsBillDetailEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
		// TODO Auto-generated constructor stub
	}

	public Date getCreateTime() {
		return createTime;
	}

	public RepertoryGoodsBillDetailEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public RepertoryGoodsBillDetailEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

	public Long getBillDetailId() {
		return billDetailId;
	}

	public void setBillDetailId(Long billDetailId) {
		this.billDetailId = billDetailId;
	}

	public Long getGoodsBillId() {
		return goodsBillId;
	}

	public RepertoryGoodsBillDetailEntity setGoodsBillId(Long goodsBillId) {
		this.goodsBillId = goodsBillId;
		return this;
	}

	public Long getGoodsBatchId() {
		return goodsBatchId;
	}

	public void setGoodsBatchId(Long goodsBatchId) {
		this.goodsBatchId = goodsBatchId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getGoodsBatchCode() {
		if(this.goodsBatchId > 0){
			RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
			batch.setGoodsBatchId(this.goodsBatchId).loadVo();
			this.goodsBatchCode = batch.getGoodsBatchCode();
		}
		return goodsBatchCode;
	}

	public void setGoodsBatchCode(String goodsBatchCode) {
		
		this.goodsBatchCode = goodsBatchCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	public long applyInsert() throws Exception{
		return super.insert();
	}
	

	@Override
	public long insert() throws Exception {
		if(type == null){
			throw new Exception("类型不能为空");
		}
		
		switch(type){
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_CHECK://盘点
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY://申领 批次锁定-
				RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
				if(PublicMethod.isEmptyValue(goodsBatchId)){
					throw new Exception("申领表的批次为空");
				}
				batch.setGoodsBatchId(goodsBatchId).loadVo();
				batch.setInventory(batch.getInventory()-number);
				batch.setLocking(batch.getLocking() + number);
				batch.update();
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_BUY://入库
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_PRODUCE://生产
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_SELL://出货
				break;
			default:
				throw new Exception("非法参数");
		}
		return super.insert();
	}

	@Override
	public int delete() throws Exception {
		loadVo();
		if(type == null){
			throw new Exception("类型不能为空");
		}
		
		switch(type){
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_CHECK://盘点
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY://申领 批次锁定-
				RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
				batch.setGoodsBatchId(goodsBatchId).loadVo();
				batch.setInventory(batch.getInventory()+number);
				batch.setLocking(batch.getLocking() - number);
				batch.update();
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_BUY://入库
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_PRODUCE://生产
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_SELL://出货
				break;
			default:
				throw new Exception("非法参数");
		}
		
		return super.delete();
	}

	@Override
	public int update(String... fieldName) throws Exception {
		if(type == null){
			throw new Exception("类型不能为空");
		}
		RepertoryGoodsBillDetailEntity detail = new RepertoryGoodsBillDetailEntity(this.jdbcDao);
		detail.setBillDetailId(billDetailId);
		detail.loadVo();
		if(PublicMethod.isEmptyValue(detail.getGoodsBillId())){
			throw new Exception("不存在的数据");
		}
		switch(type){
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_CHECK://盘点
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_APPLY://申领 批次锁定-
				RepertoryGoodsBatchEntity batch = new RepertoryGoodsBatchEntity(this.jdbcDao);
				batch.setGoodsBatchId(detail.goodsBatchId).loadVo();
				batch.setInventory(batch.getInventory()-number+detail.number);
				batch.setLocking(batch.getLocking() + number-detail.number);
				batch.update();
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_BUY://入库
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_PRODUCE://生产
				break;
			case RepertoryGoodsBillEntity.GOODS_DETAIL_TYPE_SELL://出货
				break;
			default:
				throw new Exception("非法参数");
		}
		
		return super.update(NUMBER);
	}


    
    
}