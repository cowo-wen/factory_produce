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
	public static final int TYPE_REPERTORY_GOODS_BILL = 1;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -5070696130620425878L;
	
	public static final String CHECK_LOG_ID = "check_log_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long checkLogId;
    
    /**
	 * 审核表类型id
	 */
    public static final String TYPE = "type";
    @Column
    private Integer type;
    
    
    /**
	 * 数据id
	 */
    public static final String DATA_ID = "data_id";
    @Column
    private Long dataId;
    
    
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
     * 数量
     */
    public static final String NUMBER = "number";
    @Column
    private Long number;
    
    /**
     * 备注
     */
    public static final String REMARK = "remark";
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

	public Long getCheckLogId() {
		return checkLogId;
	}

	public void setCheckLogId(Long checkLogId) {
		this.checkLogId = checkLogId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
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

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


    
    
}