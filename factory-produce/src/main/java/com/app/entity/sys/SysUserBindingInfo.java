/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2013-11-13
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
import javax.persistence.Transient;

import com.app.dao.JdbcDao;
import com.app.entity.common.CacheVo;
import com.app.entity.common.CustomCache;
import com.app.entity.common.TableCache;
import com.app.util.PublicMethod;
import com.google.gson.annotations.Expose;

/**
 * 功能说明：微信绑定信息
 * 
 * @author cowo 2017-12-07
 */
@Entity
@Table(name = "t_sys_user_binding")
@TableCache(isCache=true)
public class SysUserBindingInfo extends CacheVo  implements Serializable
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8473793266800448594L;
	
	public static final int BINDING_TYPE_WEIXIN = 1;




	public SysUserBindingInfo(JdbcDao jdbcDao) {
		super(jdbcDao);
	}

    
    /** 主键 */
    public static final String USER_BINDING_INFO_ID = "user_binding_info_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long userBindingInfoId;

    
    /** 微信身份唯一码 */
    public static final String OPEN_ID = "open_id";

    @CustomCache(sort = {0,2},group={0,1},hashKey={false,true})
    @Column
    private String openId;

    
    /** 数据id 绑定的学生id*/
    public static final String USER_ID = "user_id";
    @Column
    @CustomCache(sort = {2,0},group={0,1},hashKey={true,false})
    private Long userId;
    
    /** 1为微信*/
    public static final String TYPE = "type";
    @CustomCache(sort = 1,group={0,1})
    @Column
    private Integer type;
    
    
    /**昵称 */
    public static final String NICKNAME = "nickname";

    @Column
    private String nickname;

    /** 头像路径 */
    public static final String HEAD_IMG_URL = "head_img_url";

    @Column
    private String headImgUrl;
    
    

    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;
    
    
    /**
     * 注解@Transient 不需要持久化到数据库的字段
     */
    /** 头像路径 */
    public static final String USER_NAME = "user_name";
    @Transient
    @Expose(deserialize = true)
    private String userName;
    

    public Long getUserBindingInfoId()
    {
        return userBindingInfoId;
    }

    

	public String getOpenId() {
		return openId;
	}

	public SysUserBindingInfo setOpenId(String openId) {
		this.openId = openId;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public SysUserBindingInfo setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Integer getType() {
		return type;
	}

	public SysUserBindingInfo setType(Integer type) {
		this.type = type;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public SysUserBindingInfo setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public Date getOperatorTime() {
		return operatorTime;
	}

	public SysUserBindingInfo setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
		return this;
	}

	public SysUserBindingInfo setUserBindingInfoId(Long userBindingInfoId) {
		this.userBindingInfoId = userBindingInfoId;
		return this;
	}

	public String getNickname() {
		return nickname;
	}

	public SysUserBindingInfo setNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public SysUserBindingInfo setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
		return this;
	}



	public String getUserName() {
		if(!PublicMethod.isEmptyStr(userId)){
			SysUserEntity user = new SysUserEntity(jdbcDao);
			user.setUserId(this.userId).loadVo();
			userName = user.getUserName();
		}
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}

    
    
    
}
