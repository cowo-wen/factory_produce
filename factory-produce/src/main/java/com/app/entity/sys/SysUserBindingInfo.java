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

import com.app.dao.JdbcDao;
import com.app.entity.common.CacheVo;
import com.app.entity.common.TableCache;

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

    @Column
    private String openId;

    
    /** 数据id 绑定的学生id*/
    public static final String USER_ID = "user_id";

    @Column
    private Long userId;
    
    /** 1为微信*/
    public static final String TYPE = "type";

    @Column
    private Integer type;
    
    
    

    @Column
    private Date createTime;
    
    @Column
    private Date operatorTime;
    
    
    

    public Long getUserBindingInfoId()
    {
        return userBindingInfoId;
    }

    public SysUserBindingInfo setUserBindingInfoId(long userBindingInfoId)
    {
        this.userBindingInfoId = userBindingInfoId;
        return this;
    }

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
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

	public void setUserBindingInfoId(Long userBindingInfoId) {
		this.userBindingInfoId = userBindingInfoId;
	}

    
    
    
}
