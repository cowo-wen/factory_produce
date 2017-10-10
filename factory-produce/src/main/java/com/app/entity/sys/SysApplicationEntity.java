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
 * 功能说明：系统应用表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_application")
public class SysApplicationEntity extends CacheVo implements Serializable
{

	
	
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 473367927408629970L;


	@Id
	@Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

	
	@Column
    private Long parentId;

	
	@Column
    private Integer type;
    
	@Column
    private String name;
    
	
	@Column
    private String code;
    
	
	@Column
    private String remark;
    
	/**
	 * 用户
	 */
	@Column
    private String url;
    
	@Column
    private Date createTime;
    
	@Column
    private Date operatorTime;
    
    

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public SysApplicationEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
	
	public SysApplicationEntity(String name)
    {
		super(name);
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }

	

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setType(Integer type) {
		this.type = type;
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