/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.task;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.app.dao.JdbcDao;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.sort.DescSort;
import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：任务审核表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_task_review")
@TableCache(isCache=true)
public class TaskReviewEntity extends CacheVo  implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1469879657732990493L;
	
	public static final String REVIEW_ID = "review_id";
	@CustomCache(hashKey=true,sort=1)
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long reviewId;
	
	/**
	 * 任务id
	 */
	public static final String PRODUCE_ID = "produce_id";
    @Column
    @CustomCache(sort=0)
    private Long produceId;

    /**
     * 审核用户
     */
    @Column
    private Long userId;
    
    /**
     * 审核状态
     */
    @Column
    private Integer status;
    
    /**
     * 备注
     */

    public static final String REMARK = "remark";
    @Column
    private String remark;
    
    /**
     * 不合格数
     */

    public static final String NUMBER = "number";
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
    
    
    
    

	public TaskReviewEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
		// TODO Auto-generated constructor stub
	}

	public Long getReviewId() {
		return reviewId;
	}

	public TaskReviewEntity setReviewId(Long reviewId) {
		this.reviewId = reviewId;
		return this;
	}

	public Long getProduceId() {
		return produceId;
	}

	public TaskReviewEntity setProduceId(Long produceId) {
		this.produceId = produceId;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public TaskReviewEntity setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public TaskReviewEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public String getRemark() {
		return remark;
	}

	public TaskReviewEntity setRemark(String remark) {
		this.remark= remark;
		return this;
	}

	public Integer getNumber() {
		return number;
	}

	public TaskReviewEntity setNumber(Integer number) {
		this.number = number;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public TaskReviewEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public TaskReviewEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}
	
	
	
	public String getUserName() {
		if(userId != null && userId > 0){
			SysUserEntity user = new SysUserEntity(jdbcDao);
			user.setUserId(userId).loadVo();
			userName = user.getUserName();
		}
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public TaskReviewEntity getMaxId(long produceId){
		String key = getTableName()+":max:id:"+produceId;
		String value = new RedisAPI(this.redisObj).get(key);
		if(!PublicMethod.isEmptyStr(value)){
			this.reviewId = Long.parseLong(value);
			if(this.reviewId != 0){
				this.loadVo();
			}
			
			return this;
		}else{
			List<TaskReviewEntity> reviewList = getListVO(0, 1000, new SQLWhere(new EQCnd(TaskReviewEntity.PRODUCE_ID, produceId)).orderBy(new DescSort(TaskReviewEntity.REVIEW_ID)));
			if(reviewList.size() > 0){
				new RedisAPI(this.redisObj).put(key, String.valueOf(reviewList.get(0).getReviewId()), 60*60*24*100);//缓存一百天
				return reviewList.get(0);
			}else{
				new RedisAPI(this.redisObj).put(key, String.valueOf("0"), 60*60*24*100);//缓存一百天
			}
			return null;
		}
	}

	@Override
	public long insert() throws Exception {
		long id = super.insert();
		String key = getTableName()+":max:id:"+this.produceId;
		if(this.status == TaskProduceEntity.PRODUC_STATUS_FINISH){
			new RedisAPI(this.redisObj).del(key);//删除缓存
		}else if(this.status == TaskProduceEntity.PRODUC_STATUS_REDO){
			new RedisAPI(this.redisObj).put(key, String.valueOf(id), 60*60*24*100);//缓存一百天
		}
		return id;
	}

	
	
    
    
    
}