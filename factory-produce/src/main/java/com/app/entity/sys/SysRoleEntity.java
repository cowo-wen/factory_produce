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
 * 功能说明：系统角色表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_role")
@TableCache(isCache=true)
public class SysRoleEntity extends CacheVo implements Serializable {


	public static final String ADMIN_CODE = "1000";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2744040054922288267L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long roleId;
	
	/**
	 * 角色名称
	 */
	@Column
	private String roleName;

	/**
	 * 父id
	 */
	@Column
	private Long parentId;
	/**
	 * 角色编码
	 */
	@Column
	@CustomCache(sort = 0)
	private String roleCode;
	
	/**
	 * 根接点查询所有子接点使用
	 * 连接码
	 */
	@Column
	private String linkCode;
	
	/**
	 * 是否有效 1是 2否
	 */
	@Column
    private Integer valid;
	
	/**
	 * 备注
	 */
	@Column
	private String remark;
	
	/**
	 * pc端浏览主页
	 */
	@Column
	private String pcIndex;
	
	/**
	 * 微信端浏览主页
	 */
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

	public SysRoleEntity setRoleId(Long roleId) {
		this.roleId = roleId;
		return this;
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

	public SysRoleEntity setRoleCode(String roleCode) {
		this.roleCode = roleCode;
		return this;
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

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public String getLinkCode() {
		return linkCode;
	}

	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}

	

}
