/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.task;

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
 * 功能说明：任务生产员工表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_task_worker")
public class TaskWorkerEntity extends CacheVo  implements Serializable
{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1469879657732990493L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long workerId;
    
    @Column
    private Long produceId;

    @Column
    private Long userId;
    
    @Column
    private Date completTime;
    
    @Column
    private Integer valid;
    
    @Column
    private Integer number;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

    public TaskWorkerEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
    
    public TaskWorkerEntity(String name)
    {
		super(name);
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }

	public Long getWorkerId() {
		return workerId;
	}

	public void setWorkerId(Long workerId) {
		this.workerId = workerId;
	}

	public Long getProduceId() {
		return produceId;
	}

	public void setProduceId(Long produceId) {
		this.produceId = produceId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getCompletTime() {
		return completTime;
	}

	public void setCompletTime(Date completTime) {
		this.completTime = completTime;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
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