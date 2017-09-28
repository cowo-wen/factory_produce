/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.service.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.sys.SysUserRepository;
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
    private SysUserRepository sysUserRepository;
    
    public void save(SysUserEntity user) throws Exception{
    	
    	Map<String,Object> map = new HashMap<String,Object>();
        map.put("field_1", "login_name");
        map.put("value_1", user.getLoginName());
		List<?> list = user.getCustomCache(map, "user_id");
		if(list.size() == 0){
			sysUserRepository.save(user);
			user.insertInNosql();
			user.saveCustomCache(map, "user_id", user.getUserId().toString(), user.getUserId().toString());
		}else{
			throw new Exception("已存在相同的登录帐号");
		}
    }
}
