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
 * 功能说明：角色应用表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_role_application")
@TableCache(isCache=true)
public class SysRoleApplicationEntity extends CacheVo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1727093310464458636L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	@Column
	@CustomCache(sort = 0)
	private Long roleId;
	
	@Column
	@CustomCache(hashKey=true, sort = 1)
	private Long applicationId;

	@Column
	private Date createTime;

	@Column
	private Date operatorTime;

	public SysRoleApplicationEntity() {
		this.createTime = new Date();
		this.operatorTime = this.createTime;
	}

	public SysRoleApplicationEntity(String name)
    {
		super(name);
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
	
	public Long getRoleId() {
		return roleId;
	}

	public SysRoleApplicationEntity setRoleId(Long roleId) {
		this.roleId = roleId;
		return this;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
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
