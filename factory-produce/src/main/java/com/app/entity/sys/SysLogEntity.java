/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.sys;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 功能说明：系统日志表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_log")
public class SysLogEntity implements Serializable
{

	private static final long serialVersionUID = -9178816069533396484L;
	
	/**
	 * 类型成功
	 */
	
	public static final int TYPE_SUCCESS = 1;
	
	/**
	 * 类型失败
	 */
	public static final int TYPE_FAIL = 2;
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    private long applicationId;

    private long dataId;
    
    private String message;
    
    private String ip;
    
    private int type;
    
    private long userId;
    
    private Date createTime;
    
    private Date operatorTime;
    
    

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public SysLogEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public void setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
	}

   
    
}