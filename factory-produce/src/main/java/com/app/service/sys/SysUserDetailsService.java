/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-11
 */
package com.app.service.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.bean.SysUserDetails;
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
        	Map<String,Object> map = new HashMap<String,Object>();
            map.put("field_1", "login_name");
            map.put("value_1", userName);
        	List<?> list = user.getCustomCache(map, "user_id");
        	if(list.size() == 0){
        		return null;
        	}else{
        		user = (SysUserEntity) list.get(0);
        	}
            
            List<SysUserRoleEntity> roles = new ArrayList<SysUserRoleEntity>();
            SysUserRoleEntity ur = new SysUserRoleEntity();
            ur.setId(1L);
            ur.setRoleId(1L);
            ur.setUserId(1L);
            roles.add(ur);
            return new SysUserDetails(user, roles);
        }
        catch (Exception e)
        {
        	logger.error("查找用户出错", e);
            throw new UsernameNotFoundException("user role select fail");
        }
    }
}
