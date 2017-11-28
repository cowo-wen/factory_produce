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

import com.app.dao.JdbcDao;
import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;

/**
 * 功能说明：任务生产关联的配件信息
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_task_lock_component")
@TableCache(isCache=true)
public class TaskLockComponentEntity extends CacheVo  implements Serializable
{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 828075987442621839L;
	public static final String LOCK_COMPONENT_ID = "lock_component_id";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CustomCache(sort=1,hashKey=true)
    @Column
    private Long lockComponentId;
	
	public static final String PRODUCE_ID = "produce_id";
    @Column
    private Long produceId;
	
    /**
	 * 批次id
	 */
	public static final String GOODS_BATCH_ID = "goods_batch_id";
    @Column
    private Long goodsBatchId;
    
	
    
	/**
	 * 数量
	 */
	public static final String NUMBER = "number";
    @Column
    private Integer number;

   
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;
    
    
    

	public TaskLockComponentEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
		// TODO Auto-generated constructor stub
	}

	public Long getLockComponentId() {
		return lockComponentId;
	}

	public TaskLockComponentEntity setLockComponentId(Long lockComponentId) {
		this.lockComponentId = lockComponentId;
		return this;
	}

	public Long getProduceId() {
		return produceId;
	}

	public TaskLockComponentEntity setProduceId(Long produceId) {
		this.produceId = produceId;
		return this;
	}

	public Long getGoodsBatchId() {
		return goodsBatchId;
	}

	public TaskLockComponentEntity setGoodsBatchId(Long goodsBatchId) {
		this.goodsBatchId = goodsBatchId;
		return this;
	}

	public Integer getNumber() {
		return number;
	}

	public TaskLockComponentEntity setNumber(Integer number) {
		this.number = number;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public TaskLockComponentEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public TaskLockComponentEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

    
    
    
    
    
}