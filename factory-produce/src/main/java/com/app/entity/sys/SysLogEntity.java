/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.sys;

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
 * 功能说明：系统日志表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_log")
public class SysLogEntity extends CacheVo implements Serializable
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
	

	public static final String LOG_ID = "log_id";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name=LOG_ID)
    private Long logId;

	/**
	 * 应用id
	 */
	public static final String APPLICATION_ID = "application_id";
	@Column(name=APPLICATION_ID)
    private long applicationId;

	/**
	 * 数据id
	 */
	public static final String DATA_ID = "data_id";
	@Column(name=DATA_ID)
    private long dataId;
    
	/**
	 * 消息
	 */
	public static final String MESSAGE = "message";
	@Column(name=MESSAGE)
    private String message;
    
	/**
	 * ip
	 */
	public static final String IP = "ip";
	@Column
    private String ip;
    
	/**
	 * 类型
	 */
	public static final String TYPE = "type";
	@Column
    private int type;
    
	/**
	 * 用户
	 */
	public static final String USER_ID = "user_id";
	@Column
    private long userId;
    
	@Column
    private Date createTime;
    
	@Column
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
	
	public SysLogEntity(String name)
    {
		super(name);
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