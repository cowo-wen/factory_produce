/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-14
 */
package com.app.service;

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
public class StudentService
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<SysUserEntity> getList(){
        String sql = "SELECT ID,username,password FROM t_sys_user";
        return (List<SysUserEntity>) jdbcTemplate.query(sql, new RowMapper<SysUserEntity>(){

            
            public SysUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            	SysUserEntity su = new SysUserEntity();
                su.setUserId(rs.getLong("id"));
                su.setUserName(rs.getString("user_name"));
                su.setLoginName(rs.getString("login_name"));
                su.setPassword(rs.getString("password"));
                return su;
            }

        });
    }
}
