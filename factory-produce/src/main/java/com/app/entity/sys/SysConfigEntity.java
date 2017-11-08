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
import com.app.entity.common.TableCache;

/**
 * 功能说明：系统用户表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_config")
@TableCache(isCache=true)
public class SysConfigEntity extends CacheVo  implements Serializable
{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5226339948182555979L;
	
	
	public static final String ID = "id";
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

	public static final String GROUP_TYPE = "group_type";
    @Column
    private String groupType;
    
    public static final String NAME = "name";
    @Column
    private String name;
    
    public static final String FILED_NAME = "filed_name";
    @Column
    private String filedName;
    
    public static final String VALUE_CLASS = "value_class";
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

	

	public String getFiledName() {
		return filedName;
	}

	public void setFiledName(String filedName) {
		this.filedName = filedName;
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