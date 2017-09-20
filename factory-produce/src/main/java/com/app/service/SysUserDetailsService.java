/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-11
 */
package com.app.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.bean.SysUserDetails;
import com.app.entity.sys.SysUserEntity;
import com.app.entity.sys.SysUserRoleEntity;

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
        SysUserEntity user = new SysUserEntity();
        try
        {
            user.setUserId(1L);
            user.setPassword("123456");
            user.setUserName("cowo");
            user.setLoginName("admin");
            List<SysUserRoleEntity> roles = new ArrayList<SysUserRoleEntity>();
            SysUserRoleEntity ur = new SysUserRoleEntity();
            ur.setId(1L);
            ur.setRoleId(1L);
            ur.setUserId(1L);
            roles.add(ur);
            logger.error("---------1----------");
            return new SysUserDetails(user, roles);
        }
        catch (Exception e)
        {
            throw new UsernameNotFoundException("user role select fail");
        }
    }
}
