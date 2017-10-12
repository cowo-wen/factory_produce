/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-11
 */
package com.app.bean;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.entity.sys.SysUserEntity;
import com.app.entity.sys.SysUserRoleEntity;

/**
 * 功能说明：
 * @author chenwen 2017-8-11
 *
 */
public class SysUserDetails extends SysUserEntity implements UserDetails{

    /**
     * 
     */
    private static final long serialVersionUID = 9167799082917807318L;
    private List<SysUserRoleEntity> roles;

    public SysUserDetails(SysUserEntity user, List<SysUserRoleEntity> roles){
        super(user);
        this.roles = roles;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(roles == null || roles.size() == 0){
            return AuthorityUtils.commaSeparatedStringToAuthorityList("");
        }
        StringBuilder commaBuilder = new StringBuilder();
        for(SysUserRoleEntity role : roles){
            commaBuilder.append(role.getRoleId()).append(",");
        }
        String authorities = "";
        if(commaBuilder.length() > 0){
        	authorities = commaBuilder.substring(0,commaBuilder.length()-1);
        }
         
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    public String getUsername() {
        return super.getUserName();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

}
