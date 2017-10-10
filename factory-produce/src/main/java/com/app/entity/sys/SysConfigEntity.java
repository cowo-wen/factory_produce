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
 * 功能说明：系统用户表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_config")
public class SysConfigEntity extends CacheVo  implements Serializable
{

	
    /**
	 * 
	 */
	private static final long serialVersionUID = -2638845486880415208L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String groupType;
    
    @Column
    private String name;
    
    @Column
    private String Filed;
    
    @Column
    private String valueClass;
    
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

    public SysConfigEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
    
    public SysConfigEntity(String name)
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

	

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFiled() {
		return Filed;
	}

	public void setFiled(String filed) {
		Filed = filed;
	}

	public String getValueClass() {
		return valueClass;
	}

	public void setValueClass(String valueClass) {
		this.valueClass = valueClass;
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