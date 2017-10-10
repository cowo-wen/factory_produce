/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.dao.sys;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.sys.SysUserEntity;
@Transactional
public interface  SysUserRepository extends CrudRepository<SysUserEntity, Integer>{

}
