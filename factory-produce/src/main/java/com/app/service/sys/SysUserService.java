/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.service.sys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.app.entity.sys.SysUserEntity;

/**
 * 功能说明：
 * @author chenwen 2017-8-14
 *
 */
@Service
public class SysUserService
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<SysUserEntity> getList(){
        String sql = "SELECT user_id,user_name,password FROM t_sys_user";
        return (List<SysUserEntity>) jdbcTemplate.query(sql, new RowMapper<SysUserEntity>(){

            
            public SysUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                SysUserEntity su = new SysUserEntity();
                su.setUserId(rs.getLong("user_id"));
                su.setUserName(rs.getString("username"));
                su.setPassword(rs.getString("password"));
                return su;
            }

        });
    }
}
