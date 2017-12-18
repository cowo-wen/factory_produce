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
import com.app.util.StaticBean;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：产品批次
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_repertory_goods_batch")
@TableCache(isCache=true)
public class RepertoryGoodsBatchEntity extends CacheVo  implements Comparable<RepertoryGoodsBatchEntity>,Serializable
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -218914755335728462L;


	/**
	 * 批次id
	 */
	public static final String GOODS_BATCH_ID = "goods_batch_id";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long goodsBatchId;
    
	/**
	 * 批次代码
	 */
	public static final String GOODS_BATCH_CODE = "goods_batch_code";
    @Column
    @CustomCache(sort = 0,hashKey={true,false},group={0,1})
    private String goodsBatchCode;
    
   
    /**
     * 产品id
     */
    public static final String GOODS_ID = "goods_id";
    @Column
    @CustomCache(sort = 0)
    private Long goodsId;
    
    
    /**
     * 是否有效，1表示有效，2表示无效  当number为1时即无效
     */
    public static final String VALID = "valid";
    @Column
    @CustomCache(sort = 1,group={0,1})
    private Integer valid;
    
    /**
     * 数量
     */
    public static final String INVENTORY = "inventory";
    @Column
    private Integer inventory = 0;
    
    /**
     * 锁定
     */
    public static final String LOCKING = "locking";
    @Column
    private Integer locking = 0;
    
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;
    
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
   
    
   



	public RepertoryGoodsBatchEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
	}



	public Long getGoodsBatchId() {
		return goodsBatchId;
	}



	public RepertoryGoodsBatchEntity setGoodsBatchId(Long goodsBatchId) {
		this.goodsBatchId = goodsBatchId;
		return this;
	}



	public String getGoodsBatchCode() {
		return goodsBatchCode;
	}



	public RepertoryGoodsBatchEntity setGoodsBatchCode(String goodsBatchCode) {
		this.goodsBatchCode = goodsBatchCode;
		return this;
	}



	public Integer getValid() {
		return valid;
	}



	public RepertoryGoodsBatchEntity setValid(Integer valid) {
		this.valid = valid;
		return this;
	}





	public Integer getInventory() {
		return inventory;
	}



	public RepertoryGoodsBatchEntity setInventory(Integer inventory) {
		this.inventory = inventory;
		return this;
	}



	public Integer getLocking() {
		return locking;
	}



	public RepertoryGoodsBatchEntity setLocking(Integer locking) {
		this.locking = locking;
		return this;
	}



	public RepertoryGoodsBatchEntity setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
		return this;
	}

	

	public Long getGoodsId() {
		return goodsId;
	}

	

	public Date getCreateTime() {
		return createTime;
	}

	public RepertoryGoodsBatchEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public RepertoryGoodsBatchEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
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



	public void setName(String name) {
		this.name = name;
	}



	public Integer getType() {
		return type;
	}



	public void setType(Integer type) {
		this.type = type;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}
    
	
	

	@Override
	public int delete() throws Exception {
		RepertoryGoodsBatchEntity batch =new RepertoryGoodsBatchEntity(this.jdbcDao);
		batch.setGoodsBatchId(goodsBatchId).loadVo();
		if(batch.inventory != 0 || batch.locking != 0){
			throw new Exception("库存不为空，不能删除");
		}
		return super.delete();
	}

	@Override
	public int update(String... fieldName) throws Exception {
		
		if(PublicMethod.isEmptyValue(goodsBatchId)){
			throw new Exception("缺省主键");
		}
		
		if(PublicMethod.isEmptyValue(this.goodsId)){
			throw new Exception("产品信息参数不能为空");
		}
		RepertoryGoodsEntity entity =new RepertoryGoodsEntity(this.jdbcDao);
		entity.setGoodsId(goodsId).loadVo();
		if(PublicMethod.isEmptyStr(entity.getCode())){
			throw new Exception("不存在的产品信息");
		}
		
		
		RepertoryGoodsBatchEntity batch =new RepertoryGoodsBatchEntity(this.jdbcDao);
		batch.setGoodsBatchId(goodsBatchId).loadVo();
		if(PublicMethod.isEmptyStr(batch.goodsBatchCode)){
			throw new Exception("不存在的批次信息");
		}
		
		int inventoryValue = inventory - batch.inventory,lockingValue=locking-batch.locking;
		
		if(inventoryValue != 0 && lockingValue != 0 ){
			entity.setInventory(entity.getInventory()+inventoryValue);
			entity.setLocking(entity.getLocking()+lockingValue);
			entity.update(INVENTORY,LOCKING);
		}else if(inventoryValue != 0){
			entity.setInventory(entity.getInventory()+inventoryValue);
			entity.update(INVENTORY);
		}else if(lockingValue != 0){
			entity.setLocking(entity.getLocking()+lockingValue);
			entity.update(LOCKING);
		}
		if(this.inventory == 0 && this.locking == 0){
			this.valid = StaticBean.NO;
		}
		return super.update(fieldName);
	}



	@Override
	public long insert() throws Exception {
		if(PublicMethod.isEmptyValue(this.goodsId)){
			throw new Exception("产品信息参数不能为空");
		}
		RepertoryGoodsEntity entity =new RepertoryGoodsEntity(this.jdbcDao);
		entity.setGoodsId(goodsId).loadVo();
		if(PublicMethod.isEmptyStr(entity.getCode())){
			throw new Exception("不存在的产品信息");
		}
		
		this.valid = StaticBean.YES;
		if((inventory > 0 || locking > 0) ){//更新产品信息的库存
			entity.setInventory(this.inventory + entity.getInventory());
			entity.setLocking(this.locking+entity.getLocking());
			entity.update(RepertoryGoodsEntity.LOCKING,RepertoryGoodsEntity.INVENTORY);
		}
		return super.insert();
	}



	@Override
	public int compareTo(RepertoryGoodsBatchEntity o) {
		int flag=this.inventory.compareTo(o.inventory);
		if(flag==0){
			return this.goodsBatchId.compareTo(o.goodsBatchId);
		}else{
			return flag;
		} 
	}
    
    
    
    
}