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
import javax.persistence.Transient;

import com.app.dao.JdbcDao;
import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：任务生产员工表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_task_worker")
@TableCache(isCache=true)
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
    @CustomCache(sort=0)
    private Long produceId;

    @Column
    @CustomCache(sort=1,hashKey=true)
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
    
    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String USER_NAME = "user_name";
    @Transient
    @Expose(deserialize = true)
    private String userName;

    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String USER_CODE = "user_code";
    @Transient
    @Expose(deserialize = true)
    private String userCode;
   

	public TaskWorkerEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
	}

	public Long getWorkerId() {
		return workerId;
	}

	public TaskWorkerEntity setWorkerId(Long workerId) {
		this.workerId = workerId;
		return this;
	}

	public Long getProduceId() {
		return produceId;
	}

	public TaskWorkerEntity setProduceId(Long produceId) {
		this.produceId = produceId;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public TaskWorkerEntity setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Date getCompletTime() {
		return completTime;
	}

	public TaskWorkerEntity setCompletTime(Date completTime) {
		this.completTime = completTime;
		return this;
	}

	public Integer getValid() {
		return valid;
	}

	public TaskWorkerEntity setValid(Integer valid) {
		this.valid = valid;
		return this;
	}

	public Integer getNumber() {
		return number;
	}

	public TaskWorkerEntity setNumber(Integer number) {
		this.number = number;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public TaskWorkerEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public TaskWorkerEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public TaskWorkerEntity setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getUserCode() {
		return userCode;
	}

	public TaskWorkerEntity setUserCode(String userCode) {
		this.userCode = userCode;
		return this;
	}

	
    
    
    
}