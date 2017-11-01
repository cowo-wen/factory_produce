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
 * 功能说明：系统应用表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_application")
@TableCache(isCache=true)
public class SysApplicationEntity extends CacheVo implements Serializable
{

	
	
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 473367927408629970L;


	/**
	 * 主键
	 */
	@Id
	@Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

	
	/**
	 * 父节点
	 */
	@Column
    private Long parentId;

	/**
	 * 终端类型 1表示pc端，2表示微信端
	 */
	@Column
    private Integer terminalType;
	
	/**
	 * 应用类型，1表示菜单,2表示导航方法，3表示按钮，4表示其他
	 */
	@Column
    private Integer appType;
	
	/**
	 * 事件类型，1表示url,2表示js方法，3表示html
	 */
	@Column
    private Integer eventType;
	
	/**
	 * 是否有效
	 */
	@Column
    private Integer valid;
    
	/**
	 * 应用名称
	 */
	@Column
    private String name;
    
	/**
	 * 图标代码
	 */
	@Column
    private String iconCode;
	
	/**
	 * 应用代码
	 */
	@Column
	@CustomCache(sort = 0)
    private String applicationCode;
	
	/**
	 * 父应用代码
	 */
	@Column
    private String parentApplicationCode;
	
	
    
	
	public String getParentApplicationCode() {
		return parentApplicationCode;
	}

	public void setParentApplicationCode(String parentApplicationCode) {
		this.parentApplicationCode = parentApplicationCode;
	}

	/**
	 * 备注
	 */
	@Column
    private String remark;
	
	/**
	 * 排序
	 */
	@Column
    private Integer sortCode;
    
	/**
	 * 输出代码
	 */
	@Column
    private String outCode;
	
	/**
	 * url
	 */
	@Column
    private String url;
    
	@Column
    private Date createTime;
    
	@Column
    private Date operatorTime;
    
    

	
    

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	

	public String getApplicationCode() {
		return applicationCode;
	}

	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Integer getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(Integer terminalType) {
		this.terminalType = terminalType;
	}

	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public Integer getEventType() {
		return eventType;
	}

	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}

	public String getOutCode() {
		return outCode;
	}

	public void setOutCode(String outCode) {
		this.outCode = outCode;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public Integer getSortCode() {
		return sortCode;
	}

	public void setSortCode(Integer sortCode) {
		this.sortCode = sortCode;
	}

   
    
}