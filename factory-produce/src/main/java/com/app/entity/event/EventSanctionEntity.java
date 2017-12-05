/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.event;

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
import com.app.entity.common.TableCache;
import com.app.entity.sys.SysUserEntity;
import com.app.util.PublicMethod;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：事件奖罚
 * 
 * @author chenwen 2017-11-7
 */
@Entity
@Table(name = "t_event_sanction")
@TableCache(isCache=true)
public class EventSanctionEntity extends CacheVo  implements Serializable
{

    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7147738175744016292L;
	/**
     * 奖罚类型-奖励
     */
    public static final int SANCTION_TYPE_REWARD = 1;
    
    /**
     * 奖罚类型-罚款
     */
    public static final int SANCTION_TYPE_PENALTY = 2;
    
	
    
    public static final String EVENT_SANCTION_ID = "event_sanction_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long eventSanctionId;
    
    
    /**
     * 奖罚类型 
     */
    public static final String TYPE = "type";
    @Column
    private Integer type;
    
    /**
     * 奖罚目标用户
     */
    public static final String USER_ID = "user_id";
    @Column
    private Long userId;
    
    /**
     * 名称
     */
    public static final String NAME = "name";
    @Column
    private String name;
    
    
    /**
     * 金额
     */
    public static final String MONEY = "money";
    @Column
    private Double money;
    
    /**
     * 描述
     */
    public static final String REMARK = "remark";
    @Column
    private String remark;
    
    public static final String CHECK_TIME = "check_time";
    @Column
    private Date checkTime;
    
    public static final String CHECK_USER_ID = "check_user_id";
    @Column
    private Long checkUserId;
   
    public static final String CHECK_STATUS = "check_status";
    @Column
    private Integer checkStatus;
    
    /**
     * 描述
     */
    public static final String CHECK_REMARK = "check_remark";
    @Column
    private String checkRemark;
    
    @Column
    private Long operatorUserId;
    
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
    public static final String OPERATOR_NAME = "operator_name";
    @Transient
    @Expose(deserialize = true)
    private String operatorName;
    
    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    public static final String CHECK_NAME = "check_name";
    @Transient
    @Expose(deserialize = true)
    private String checkName;
    
	
	public EventSanctionEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
	}


	public Long getEventSanctionId() {
		return eventSanctionId;
	}


	public EventSanctionEntity setEventSanctionId(Long eventSanctionId) {
		this.eventSanctionId = eventSanctionId;
		
		return this;
	}


	public Integer getType() {
		return type;
	}


	public EventSanctionEntity setType(Integer type) {
		this.type = type;
		
		return this;
	}


	public Long getUserId() {
		return userId;
	}


	public EventSanctionEntity setUserId(Long userId) {
		this.userId = userId;
		
		return this;
	}


	public String getName() {
		return name;
	}


	public EventSanctionEntity setName(String name) {
		this.name = name;
		
		return this;
	}


	public Double getMoney() {
		return money;
	}


	public EventSanctionEntity setMoney(Double money) {
		this.money = money;
		
		return this;
	}


	public String getRemark() {
		return remark;
	}


	public EventSanctionEntity setRemark(String remark) {
		this.remark = remark;
		
		return this;
	}


	public Date getCheckTime() {
		return checkTime;
	}


	public EventSanctionEntity setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
		
		return this;
	}


	public Long getCheckUserId() {
		return checkUserId;
	}


	public EventSanctionEntity setCheckUserId(Long checkUserId) {
		this.checkUserId = checkUserId;
		
		return this;
	}


	public Integer getCheckStatus() {
		return checkStatus;
	}


	public EventSanctionEntity setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
		
		return this;
	}


	public Long getOperatorUserId() {
		return operatorUserId;
	}


	public EventSanctionEntity setOperatorUserId(Long operatorUserId) {
		this.operatorUserId = operatorUserId;
		
		return this;
	}


	public Date getCreateTime() {
		return createTime;
	}


	public EventSanctionEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		
		return this;
	}


	public Date getOperatorTime() {
		return operatorTime;
	}


	public EventSanctionEntity setOperatorTime(Date operatorTime) {
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


	public EventSanctionEntity setUserName(String userName) {
		this.userName = userName;
		
		return this;
	}


	public String getOperatorName() {
		if(!PublicMethod.isEmptyValue(this.operatorUserId)){
			SysUserEntity user = new SysUserEntity(jdbcDao);
			user.setUserId(operatorUserId).loadVo();
			operatorName = user.getUserName();
		}
		return operatorName == null ? "" : operatorName;
	}


	public EventSanctionEntity setOperatorName(String operatorName) {
		this.operatorName = operatorName;
		
		return this;
	}


	public String getCheckName() {
		if(!PublicMethod.isEmptyValue(this.checkUserId)){
			SysUserEntity user = new SysUserEntity(jdbcDao);
			user.setUserId(checkUserId).loadVo();
			checkName = user.getUserName();
		}
		return checkName == null ? "" : checkName;
	}


	public EventSanctionEntity setCheckName(String checkName) {
		this.checkName = checkName;
		
		return this;
	}


	public String getCheckRemark() {
		return checkRemark;
	}


	public EventSanctionEntity setCheckRemark(String checkRemark) {
		this.checkRemark = checkRemark;
		return this;
	}

	
	
	
    
    
}