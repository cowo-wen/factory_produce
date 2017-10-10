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
 * 功能说明：任务生产表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_task_produce")
public class TaskProduceEntity extends CacheVo  implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7986852625158168654L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long produceId;
    
    @Column
    private Integer amount;

    @Column
    private String produceName;
    
    @Column
    private Date beginTime;
    
    @Column
    private Date endTime;
    
    @Column
    private Date startTime;
    
    @Column
    private Integer status;
    
    @Column
    private Double wages;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

    public TaskProduceEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
    
    public TaskProduceEntity(String name)
    {
		super(name);
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }

	public Long getProduceId() {
		return produceId;
	}

	public void setProduceId(Long produceId) {
		this.produceId = produceId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getProduceName() {
		return produceName;
	}

	public void setProduceName(String produceName) {
		this.produceName = produceName;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Double getWages() {
		return wages;
	}

	public void setWages(Double wages) {
		this.wages = wages;
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