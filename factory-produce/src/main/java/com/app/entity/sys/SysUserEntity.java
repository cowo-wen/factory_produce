/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.entity.sys;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.app.dao.JdbcDao;
import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：系统用户表
 * 
 * @author chenwen 2017-8-11
 */
@Entity
@Table(name = "t_sys_user")
@TableCache(isCache=true)
public class SysUserEntity extends CacheVo  implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -7577009420662238475L;
    
    
    /**
     * 管理员类型
     */
    public static final int USER_ADMIN = 1;
    
    /**
     * 一般类型
     */
    public static final int USER_GENERAL = 2;
    
    /**
     * 管理员登录名称
     */
    public static final String ADMIN_USER_NAME = "admin";
    
	
    public static final String USER_ID = "user_id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long userId;
    
    /**
     * 用户类型1系统管理员，2普通用户
     */
    public static final String TYPE = "type";
    @Column
    private Integer type;
    
    
    public static final String VALID = "valid";
    @Column
    private Integer valid;

    public static final String USER_NAME = "user_name";
    @Column
    private String userName;

    public static final String PASSWORD = "password";
    @Column
    private String password;

    public static final String MOBILE = "mobile";
    @Column
    private String mobile;

    public static final String NUMBER = "number";
    @Column
    @CustomCache(sort = 0,group=1)
    private String number;

    public static final String IDCARD = "idcard";
    @Column
    private String idcard;

    public static final String LOGIN_NAME = "login_name";
    @Column
    @CustomCache(sort = 0)
    private String loginName;
    
    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;
    
    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    @Transient
    @Expose(deserialize = true)
    private String role;

    

    public SysUserEntity(JdbcDao jdbcDao) {
		super(jdbcDao);
	}
    
   


	public SysUserEntity(SysUserEntity user)
    {
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.loginName = user.getLoginName();
        this.userId = user.getUserId();
        this.valid = user.getValid();
        this.mobile = user.getMobile();
        this.idcard = user.getIdcard();
        this.number = user.getNumber();
        this.type = user.type;
    }

	

	public Integer getType() {
		return type;
	}

	public SysUserEntity setType(Integer type) {
		this.type = type;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public SysUserEntity setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public String getUserName() {
		return userName==null?"":userName;
	}

	public SysUserEntity setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public SysUserEntity setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getLoginName() {
		return loginName;
	}

	public SysUserEntity setLoginName(String loginName) {
		this.loginName = loginName;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}
	public SysUserEntity setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public SysUserEntity setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public SysUserEntity setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public SysUserEntity setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getIdcard() {
		return idcard;
	}

	public SysUserEntity setIdcard(String idcard) {
		this.idcard = idcard;
		return this;
	}

	public Integer getValid() {
		return valid;
	}

	public SysUserEntity setValid(Integer valid) {
		this.valid = valid;
		return this;
	}

	public String getRole() {
		StringBuilder sb = new StringBuilder();
		if(this.userId > 0){
			List<SysUserRoleEntity> list = new SysUserRoleEntity(this.jdbcDao).setUserId(this.userId).queryCustomCacheValue(0, null);
			if(list != null && list.size() > 0){
				
				SysRoleEntity r = new SysRoleEntity(this.jdbcDao);
				r.setRoleId(list.get(0).getRoleId()).loadVo();
				sb.append(r.getRoleName());
				for(int i = 1,len = list.size();i<len;i++){
					SysRoleEntity ri = new SysRoleEntity(this.jdbcDao);
					ri.setRoleId(list.get(i).getRoleId()).loadVo();
					sb.append(" ").append(ri.getRoleName());
				}
			}
		}
		this.role = sb.toString();
		return this.role;
	}

	public SysUserEntity setRole(String role) {
		this.role =role;
		return this;
	}

    
    
}