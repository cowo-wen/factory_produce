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

import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;
import com.app.util.StaticBean;

/**
 * 功能说明：产品批次
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_repertory_goods_batch")
@TableCache(isCache=true)
public class RepertoryGoodsBatchEntity extends CacheVo  implements Serializable
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
    @CustomCache(sort = 0,hashKey={true,false},gorup={0,1})
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
    @CustomCache(sort = 1,gorup={0,1})
    private Integer valid;
    
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



	public Integer getNumber() {
		return number;
	}



	public RepertoryGoodsBatchEntity setNumber(Integer number) {
		this.number = number;
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



	@Override
	public int update(String... fieldName) throws Exception {
		if(this.number == 0){
			this.valid = StaticBean.NO;
		}
		return super.update(fieldName);
	}
    
    
    
    
}