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
 * 功能说明：任务审核表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_task_review")
public class TaskReviewEntity extends CacheVo  implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1469879657732990493L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long reviewId;
    
    @Column
    private Long produceId;

    @Column
    private Long userId;
    
    
    @Column
    private Integer status;
    
    @Column
    private String remart;
    
    @Column
    private Integer number;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

    public TaskReviewEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
    
    public TaskReviewEntity(String name)
    {
		super(name);
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }

	public Long getReviewId() {
		return reviewId;
	}

	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemart() {
		return remart;
	}

	public void setRemart(String remart) {
		this.remart = remart;
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