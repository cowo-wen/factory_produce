/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.business;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.app.entity.common.CacheVo;

/**
 * 功能说明：业务审核日志表
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_business_check_log")
public class BusinessCheckLogEntity extends CacheVo  implements Serializable
{

	/**
	 * 产品流水明细审核类型
	 */
	public static final int TYPE_REPERTORY_GOODS_DETAIL = 1;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -5070696130620425878L;
	
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long checkLogId;
    
    /**
	 * 审核表类型id
	 */
    @Column
    private Integer type;
    
    
    /**
	 * 数据id
	 */
    @Column
    private Long dataId;
    
    
    /**
     * 审核状态 1已审核，2未审核，3审核不通过
     */
    @Column
    private Integer checkStatus;
    
    
    /**
     * 审核人
     */
    @Column
    private Long checkUser;
    
    /**
     * 审核时间
     */
    @Column
    private Date checkTime;
    
    
    
    
    
    
    
    
    /**
     * 数量
     */
    @Column
    private Long number;
    
    /**
     * 备注
     */
    @Column
    private String remark;
    
   
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

	

	public Date getCreateTime() {
		return createTime;
	}

	public BusinessCheckLogEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public BusinessCheckLogEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}


    
    
}