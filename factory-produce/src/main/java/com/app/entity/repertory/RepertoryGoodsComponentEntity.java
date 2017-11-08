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
 * 功能说明：产品组成信息
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_repertory_goods_component")
@TableCache(isCache=true)
public class RepertoryGoodsComponentEntity extends CacheVo  implements Serializable
{

	
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6179414446517649112L;


	public static final String GOODS_COMPONENT_ID = "goods_component_id";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long goodsComponentId;
    
    
   /**
    * 产品id
    */
	public static final String GOODS_ID = "goods_id";
    @Column
    @CustomCache(sort = 0)
    private Long goodsId;
    
    /**
     * 零件的产品id
     */
    public static final String COMPONENT_ID = "component_id";
    @Column
    private Long componentId;
    
    
    /**
     * 需要零件数量
     */
    public static final String NUMBER = "number";
    @Column
    private Integer number;
   
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

	public Long getGoodsId() {
		return goodsId;
	}

	

	public Date getCreateTime() {
		return createTime;
	}

	public RepertoryGoodsComponentEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public RepertoryGoodsComponentEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}






	public Long getGoodsComponentId() {
		return goodsComponentId;
	}



	public RepertoryGoodsComponentEntity setGoodsComponentId(Long goodsComponentId) {
		this.goodsComponentId = goodsComponentId;
		return this;
	}



	public Long getComponentId() {
		return componentId;
	}



	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}



	public Integer getNumber() {
		return number;
	}



	public RepertoryGoodsComponentEntity setNumber(Integer number) {
		this.number = number;
		return this;
	}



	public RepertoryGoodsComponentEntity setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
		return this;
	}
    
    
    
    
}