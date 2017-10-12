/**
 * 版权所有(C) cowo工作室 2017-2020<br>
 * 创建日期 2017-7-13
 */
package com.app.service.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    /**
     * 新增
     * @param user
     * @throws Exception
     */
    @Transactional
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
    
    
    /**
     * 修改
     * @param user
     * @throws Exception
     */
    @Transactional
    public void update(SysUserEntity user) throws Exception{
    	Map<String,Object> map = new HashMap<String,Object>();
        map.put("field_1", "login_name");
        map.put("value_1", user.getLoginName());
		List<?> list = user.getCustomCache(map, "user_id");
		int size = list.size();
		if(size == 0 || (size == 1 && ((SysUserEntity)list.get(0)).getUserId() == user.getUserId())){
			user.deleteNoSql();
			sysUserRepository.save(user);
		}else{
			throw new Exception("已存在相同的登录帐号,不能修改");
		}
    }
    
    /**
     * 删除
     * @param user
     * @throws Exception
     */
    @Transactional
    public void delete(SysUserEntity user) throws Exception{
    	user.deleteNoSql();
		sysUserRepository.delete(user);
    }
    
    
    /**
     * 查询
     * @param user
     * @throws Exception
     */
    public void queryPage(Integer page, Integer size) throws Exception{
    	 Pageable pageable = new PageRequest(page, size, Sort.Direction.ASC, "user_id");
    	 Page<SysUserEntity> list= sysUserRepository.findAll(pageable);
    	 
    	 
    }
}
