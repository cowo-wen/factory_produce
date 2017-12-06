/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.report;

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
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：员工日绩效表
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_report_worker_ach_day")
@TableCache(isCache=true)
public class WorkerAchievementDayEntity extends CacheVo  implements Serializable
{

    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7147738175733016292L;
	
    
	
    
    public static final String ID = "id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    
    
    
    
    /**
     * 统计目标用户
     */
    public static final String USER_ID = "user_id";
    @Column
    @CustomCache(sort=1)
    private Long userId;
    
    /**
     * 统计日期
     */
    public static final String COUNT_DAY = "count_day";
    @Column
    @CustomCache(sort=0)
    private Integer countDay;
    
    /**
     * 完成任务次数
     */
    public static final String TASK_TIME = "task_time";
    @Column
    private Integer taskTime;
    
    /**
     * 超时次数
     */
    public static final String OVERTIME = "overtime";
    @Column
    private Integer overtime;
    
    /**
     * 返工次数
     */
    public static final String REWORK_TIME = "rework_time";
    @Column
    private Integer reworkTime;
    
    
    /**
     * 生产金额
     */
    public static final String PRODUCE_MONEY = "produce_money";
    @Column
    private Double produceMoney;
    
    /**
     * 奖励金额
     */
    public static final String BOUNTY = "bounty";
    @Column
    private Double bounty;
    
    /**
     * 处罚金额
     */
    public static final String FINES = "fines";
    @Column
    private Double fines;
    
   
    
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
    
    
    public WorkerAchievementDayEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
		// TODO Auto-generated constructor stub
	}
    
    public WorkerAchievementDayEntity(JdbcDao jdbcDao,Long userId,int countDay) {
		super(jdbcDao);
		this.userId = userId;
		this.bounty = 0.0;
		this.countDay = countDay;
		this.fines = 0.00;
		this.overtime = 0;
		this.produceMoney = 0.00;
		this.reworkTime = 0;
		this.taskTime = 0;
		
	}


	public Long getUserId() {
		return userId;
	}


	public WorkerAchievementDayEntity setUserId(Long userId) {
		this.userId = userId;
		
		return this;
	}




	public Date getCreateTime() {
		return createTime;
	}


	public WorkerAchievementDayEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		
		return this;
	}


	public Date getOperatorTime() {
		return operatorTime;
	}


	public WorkerAchievementDayEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		
		return this;
	}


	public String getUserName() {
		if(!PublicMethod.isEmptyValue(this.userId)){
			SysUserEntity user = new SysUserEntity(jdbcDao);
			user.setUserId(userId).loadVo();
			userName = user.getUserName();
		}
		return userName == null ? "" : userName;
	}


	public WorkerAchievementDayEntity setUserName(String userName) {
		this.userName = userName;
		
		return this;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Integer getCountDay() {
		return countDay;
	}


	public void setCountDay(Integer countDay) {
		this.countDay = countDay;
	}


	public Integer getTaskTime() {
		return taskTime;
	}


	public void setTaskTime(Integer taskTime) {
		this.taskTime = taskTime;
	}


	public Integer getOvertime() {
		return overtime;
	}


	public void setOvertime(Integer overtime) {
		this.overtime = overtime;
	}


	public Integer getReworkTime() {
		return reworkTime;
	}


	public void setReworkTime(Integer reworkTime) {
		this.reworkTime = reworkTime;
	}


	public Double getProduceMoney() {
		return produceMoney;
	}


	public void setProduceMoney(Double produceMoney) {
		this.produceMoney = produceMoney;
	}


	public Double getBounty() {
		return bounty;
	}


	public void setBounty(Double bounty) {
		this.bounty = bounty;
	}


	public Double getFines() {
		return fines;
	}


	public void setFines(Double fines) {
		this.fines = fines;
	}


    
    
}