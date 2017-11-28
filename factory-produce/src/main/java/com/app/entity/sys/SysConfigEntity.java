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

import com.app.dao.JdbcDao;
import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
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
	
	public static final String GROUP_CODE = "group_code";
    @Column
    @CustomCache(sort = 0)
    private String groupCode;

	public static final String GROUP_TYPE = "group_type";
    @Column
    @CustomCache(sort = 0,hashKey=true)
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
    
    public static final String VALUE = "value";
    @Column
    private String value;
    
    public static final String VALID = "valid";
    @Column
    private Integer valid;
    
    public static final String REMARK = "remark";
    @Column
    private Integer remark;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

   

	public SysConfigEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public SysConfigEntity setId(Long id) {
		this.id = id;
		return this;
	}

	

	public String getGroupType() {
		return groupType;
	}

	public SysConfigEntity setGroupType(String groupType) {
		this.groupType = groupType;
		return this;
	}

	public String getName() {
		return name;
	}

	public SysConfigEntity setName(String name) {
		this.name = name;
		return this;
	}

	

	public String getFiledName() {
		return filedName;
	}

	public SysConfigEntity setFiledName(String filedName) {
		this.filedName = filedName;
		return this;
	}

	public String getValueClass() {
		return valueClass;
	}

	public SysConfigEntity setValueClass(String valueClass) {
		this.valueClass = valueClass;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public SysConfigEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public SysConfigEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public SysConfigEntity setGroupCode(String groupCode) {
		this.groupCode = groupCode;
		return this;
	}

	public String getValue() {
		return value;
	}

	public SysConfigEntity setValue(String value) {
		this.value = value;
		return this;
	}

	public Integer getValid() {
		return valid;
	}

	public SysConfigEntity setValid(Integer valid) {
		this.valid = valid;
		return this;
	}

	public Integer getRemark() {
		return remark;
	}

	public SysConfigEntity setRemark(Integer remark) {
		this.remark = remark;
		return this;
	}

	
    
    
}