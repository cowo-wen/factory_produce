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
 * 功能说明：系统角色表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_role")
public class SysRoleEntity extends CacheVo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2744040054922288267L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long roleId;
	
	@Column
	private String roleName;

	@Column
	private Long parentId;
	
	@Column
	private String roleCode;
	
	@Column
	private String remark;
	
	@Column
	private String pcIndex;
	
	@Column
	private String wxIndex;

	@Column
	private Date createTime;

	@Column
	private Date operatorTime;

	public SysRoleEntity() {
		this.createTime = new Date();
		this.operatorTime = this.createTime;
	}

	public SysRoleEntity(String name)
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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPcIndex() {
		return pcIndex;
	}

	public void setPcIndex(String pcIndex) {
		this.pcIndex = pcIndex;
	}

	public String getWxIndex() {
		return wxIndex;
	}

	public void setWxIndex(String wxIndex) {
		this.wxIndex = wxIndex;
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
