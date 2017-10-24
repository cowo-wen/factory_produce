/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.sys;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.app.entity.common.CacheVo;

/**
 * 功能说明：系统其他登录帐号表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_account")
public class SysAccountEntity extends CacheVo  implements Serializable
{

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 6371079352659004339L;
	
	
	/**
	 * 类型身份证
	 */
	
	public static final int TYPE_IDCARD = 1;
	
	/**
	 * 类型手机号码
	 */
	
	public static final int TYPE_MOBLIE = 2;
	
	/**
	 * 类型邮箱
	 */
	
	public static final int TYPE_EMAIL = 3;
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    
    @Column
    private Integer type;

    @Column
    private String otherAccount;
    
    @Column
    private Long UserId;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;

    public SysAccountEntity()
    {
    	this.createTime = new Date();
    	this.operatorTime = this.createTime;
    }
    
    public SysAccountEntity(String name)
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOtherAccount() {
		return otherAccount;
	}

	public void setOtherAccount(String otherAccount) {
		this.otherAccount = otherAccount;
	}

	public Long getUserId() {
		return UserId;
	}

	public void setUserId(Long userId) {
		UserId = userId;
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

	
	/**
	 * 获取其他的登录帐号信息
	 * @param type
	 * @param account
	 * @return
	 */
	public List<?> getCustomCache(int type,String account){
		Map<String,Object> accountMap = new HashMap<String,Object>();
		accountMap.put("field_1", "type");
		accountMap.put("value_1", type);
		accountMap.put("field_2", "other_account");
		accountMap.put("value_2", account);
    	return null;// getCustomCache(accountMap, "user_id");
	}
	
	/**
	 * 获取其他的登录帐号信息
	 * @param type
	 * @param account
	 * @return
	 */
	public List<?> getCustomCache(String account){
		for(int i = 1;i<=3;i++){
			List<?> list = getCustomCache(i, account);
			if(list != null && list.size() > 0){
				return list;
			}
		}
		
		return null;
	} 
    
	/**
	 * 获取其他的登录帐号信息
	 * @param type
	 * @param account
	 * @return
	 */
	public void saveCustomCache(int type,String account,String userId){
		Map<String,Object> accountMap = new HashMap<String,Object>();
		accountMap.put("field_1", "type");
		accountMap.put("value_1", type);
		accountMap.put("field_2", "other_account");
		accountMap.put("value_2", account);
		//saveCustomCache(accountMap,"user_id",userId,userId);
	}
    
}