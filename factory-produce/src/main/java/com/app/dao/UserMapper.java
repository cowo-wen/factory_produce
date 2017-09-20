/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-8-15
 */
package com.app.dao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.app.entity.sys.SysUserEntity;
/**
 * 功能说明：
 * @author chenwen 2017-8-15
 *
 */
@Mapper
public interface UserMapper {
    @Select("select * from t_sys_user where login_name = #{name}")
    public SysUserEntity findUserByName(@Param("name")String name);
}
