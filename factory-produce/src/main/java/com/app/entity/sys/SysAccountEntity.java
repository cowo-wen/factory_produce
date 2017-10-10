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
 * 功能说明：系统其他登录帐号表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_account")
public class SysAccountEntity extends CacheVo  implements Serializable
{

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 6371079352659004339L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    
    @Column
    private Integer type;

    @Column
    private String otherAccount;
    
    @Column
    private Long UserId;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

    public SysAccountEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
    
    public SysAccountEntity(String name)
    {
		super(name);
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOtherAccount() {
		return otherAccount;
	}

	public void setOtherAccount(String otherAccount) {
		this.otherAccount = otherAccount;
	}

	public Long getUserId() {
		return UserId;
	}

	public void setUserId(Long userId) {
		UserId = userId;
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