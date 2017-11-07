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

/**
 * 功能说明：产品信息表
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_repertory_goods")
@TableCache(isCache=true)
public class RepertoryGoodsEntity extends CacheVo  implements Serializable
{

    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7147738175744016292L;
	/**
     * 产品类型-原料
     */
    public static final int GOOD_TYPE_MATERIAL = 1;
    /**
     * 产品类型-半成品
     */
    public static final int GOOD_TYPE_SEMI_PRODUCT = 2;
    /**
     * 产品类型-成品
     */
    public static final int GOOD_TYPE_FINISHED_PRODUCT = 3;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long goodsId;
    
    
    /**
     * 产品类型：原料、半成品、成品
     */
    @Column
    private Integer type;
    
    /**
     * 产品名称
     */
    @Column
    private String name;
    
    /**
     * 产品图片
     */
    @Column
    private String picture;
    
    /**
     * 产品单价
     */
    @Column
    private Double price;
    
    /**
     * 产品生产价
     */
    @Column
    private Double producePrice;
    
    /**
     * 产品出货价
     */
    @Column
    private Double commodityPrice;
    
    /**
     * 产品编号
     */
    @Column
    @CustomCache(sort = 0)
    private String code;
    
    /**
     * 产品规格
     */
    @Column
    private String specifications;
    
    /**
     * 净重
     */
    @Column
    private String suttle;
    
    /**
     * 毛重
     */
    @Column
    private String grossWeight;
    
    /**
     * 包装
     */
    @Column
    private String packing;
    
    
    /**
     * 库存
     */
    @Column
    private Long inventory;
    
    /**
     * 锁定数量
     */
    @Column
    private Long locking;
    
    /**
     * 立方数
     */
    @Column
    private String CcubicNumber;
    
   
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

	public Long getGoodsId() {
		return goodsId;
	}

	public RepertoryGoodsEntity setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
		return this;
	}

	
	
	public Double getProducePrice() {
		return producePrice;
	}

	public RepertoryGoodsEntity setProducePrice(Double producePrice) {
		this.producePrice = producePrice;
		return this;
	}

	public Long getInventory() {
		return inventory;
	}

	public RepertoryGoodsEntity setInventory(Long inventory) {
		this.inventory = inventory;
		return this;
	}

	public Integer getType() {
		return type;
	}

	public RepertoryGoodsEntity setType(Integer type) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public RepertoryGoodsEntity setName(String name) {
		this.name = name;
		return this;
	}

	public String getPicture() {
		return picture;
	}

	public RepertoryGoodsEntity setPicture(String picture) {
		this.picture = picture;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public RepertoryGoodsEntity setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Double getCommodityPrice() {
		return commodityPrice;
	}

	public RepertoryGoodsEntity setCommodityPrice(Double commodityPrice) {
		this.commodityPrice = commodityPrice;
		return this;
	}

	public String getCode() {
		return code;
	}

	public RepertoryGoodsEntity setCode(String code) {
		this.code = code;
		return this;
	}

	public String getSpecifications() {
		return specifications;
	}

	public RepertoryGoodsEntity setSpecifications(String specifications) {
		this.specifications = specifications;
		return this;
	}

	public String getSuttle() {
		return suttle;
	}

	public RepertoryGoodsEntity setSuttle(String suttle) {
		this.suttle = suttle;
		return this;
	}

	public String getGrossWeight() {
		return grossWeight;
	}

	public RepertoryGoodsEntity setGrossWeight(String grossWeight) {
		this.grossWeight = grossWeight;
		return this;
	}

	public String getPacking() {
		return packing;
	}

	public RepertoryGoodsEntity setPacking(String packing) {
		this.packing = packing;
		return this;
	}

	public String getCcubicNumber() {
		return CcubicNumber;
	}

	public RepertoryGoodsEntity setCcubicNumber(String ccubicNumber) {
		CcubicNumber = ccubicNumber;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public RepertoryGoodsEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public RepertoryGoodsEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

	public Long getLocking() {
		return locking;
	}

	public RepertoryGoodsEntity setLocking(Long locking) {
		this.locking = locking;
		return this;
	}
    
    
    
    
}