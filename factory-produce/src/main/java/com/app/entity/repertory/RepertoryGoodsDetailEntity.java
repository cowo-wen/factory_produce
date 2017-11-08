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
import com.app.entity.common.TableCache;

/**
 * 功能说明：产品流水明细表
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_repertory_goods_detail")
@TableCache(isCache=true)
public class RepertoryGoodsDetailEntity extends CacheVo  implements Serializable
{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -7587404743759862803L;
	/**
     * 出货
     */
    public static final int GOOD_DETAIL_TYPE_SOLD = -1;
    /**
     * 冲单
     */
    public static final int GOOD_TYPE_SEMI_PRODUCT = -2;
    /**
     * 入库
     */
    public static final int GOOD_TYPE_SEMI_PURCHASE = 1;
    
    /**
     * 生产
     */
    public static final int GOOD_TYPE_SEMI_PRODUCTION = 2;
	
    
    public static final String GOODS_DETAIL_ID = "goods_detail_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long goodsDetailId;
    
    /**
	 * 批次id
	 */
    public static final String GOODS_BATCH_ID = "goods_batch_id";
    @Column
    private Long goodsBatchId;
    
    
    /**
     * 审核状态 1已审核，2未审核，3审核不通过
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
     * 审核时间
     */
    public static final String CHECK_TIME = "check_time";
    @Column
    private Date checkTime;
    
    /**
     * 产品类型：冲单，出货，入货
     */
    public static final String TYPE = "type";
    @Column
    private Integer type;
    
    /**
     * 数量
     */
    public static final String NUMBER = "number";
    @Column
    private Long number;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

	

	public Date getCreateTime() {
		return createTime;
	}

	public RepertoryGoodsDetailEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public RepertoryGoodsDetailEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}


    
    
}