/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-11
 */
package com.app.service.sys;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.bean.SysUserDetails;
import com.app.entity.sys.SysAccountEntity;
import com.app.entity.sys.SysUserEntity;
import com.app.entity.sys.SysUserRoleEntity;
import com.app.util.RedisAPI;

/**
 * 功能说明：
 * 
 * @author chenwen 2017-8-11
 */
@Service("SysUserDetailsImpl")
public class SysUserDetailsService implements UserDetailsService
{
    public static Log logger = LogFactory.getLog(SysUserDetailsService.class);
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException
    {
        SysUserEntity user = new SysUserEntity(RedisAPI.REDIS_CORE_DATABASE);
        try
        {
        	//Map<String,Object> map = new HashMap<String,Object>();
        	userName = userName.trim().replaceAll("--", "").replaceAll("(?i) or ", "");
            //map.put("field_1", "login_name");
            //map.put("value_1", userName);
        	//List<?> list = user.getCustomCache(map, "user_id");
        	user.setLoginName(userName);
        	List<?> list =user.queryCustomCacheValue(0, null);
        	if(list.size() == 0){
        		SysAccountEntity account = new SysAccountEntity(RedisAPI.REDIS_CORE_DATABASE);
            	List<?> accountList = account.getCustomCache(userName);
            	if(accountList == null || accountList.size() == 0){
            		return null;
            	}else{
            		user = (SysUserEntity) accountList.get(0);
            	}
        	}else{
        		user = (SysUserEntity) list.get(0);
        	}
            
            List<SysUserRoleEntity> roles = null;
            
            if(user.getType() == SysUserEntity.USER_ADMIN){
            	roles =  new ArrayList<SysUserRoleEntity>();
            	SysUserRoleEntity ur = new SysUserRoleEntity();
                ur.setId(1L);
                ur.setRoleId(1L);
                ur.setUserId(1L);
                roles.add(ur);
            }else{
            	SysUserRoleEntity ur = new SysUserRoleEntity();
            	ur.setUserId(user.getUserId());
            	roles =ur.queryCustomCacheValue(0, null);
            	logger.error("----------------获取角色数据:"+roles.size());
            }
            return new SysUserDetails(user, roles);
        }
        catch (Exception e)
        {
        	logger.error("查找用户出错", e);
            throw new UsernameNotFoundException("user role select fail");
        }
    }
}
