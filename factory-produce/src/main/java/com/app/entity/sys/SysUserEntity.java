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
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;

/**
 * 功能说明：系统用户表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_user")
@TableCache(isCache=true)
public class SysUserEntity extends CacheVo  implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -7577009420662238475L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long userId;
    
    /**
     * 用户类型1系统管理员，2普通用户
     */
    @Column
    private Integer type;
    
    @Column
    private Integer valid;

    @Column
    private String userName;
    
    @Column
    private String password;
    
    @Column
    private String mobile;
    
    @Column
    @CustomCache(sort = 0,gorup=1)
    private String number;
    
    @Column
    private String idcard;
    
    @Column
    @CustomCache(sort = 0)
    private String loginName;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

    public SysUserEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
    
    public SysUserEntity(String name)
    {
		super(name);
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }

    public SysUserEntity(SysUserEntity user)
    {
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.loginName = user.getLoginName();
        this.userId = user.getUserId();
        this.valid = user.getValid();
        this.mobile = user.getMobile();
        this.idcard = user.getIdcard();
        this.number = user.getNumber();
        this.type = user.type;
    }

	

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

    
    
}