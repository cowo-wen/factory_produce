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
 * 功能说明：系统用户角色关联表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_user_role")
@TableCache(isCache=true)
public class SysUserRoleEntity extends CacheVo implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 5584285939754090250L;

	public static final String ID = "id";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	
	public static final String USER_ID = "user_id";
	@Column
	@CustomCache(sort = 0)
	private Long userId;

	public static final String ROLE_ID = "role_id";
	@Column
	@CustomCache(hashKey=true, sort = 1)
	private Long roleId;

	@Column
	private Date createTime;

	@Column
	private Date operatorTime;

	public SysUserRoleEntity() {
		this.createTime = new Date();
		this.operatorTime = this.createTime;
	}
	
	public SysUserRoleEntity(String name)
    {
		super(name);
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getUserId() {
		return userId;
	}

	public SysUserRoleEntity setUserId(Long userId) {
		this.userId = userId;
		return this;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
