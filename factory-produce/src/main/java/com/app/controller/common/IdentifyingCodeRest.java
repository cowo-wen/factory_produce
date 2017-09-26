package com.app.controller.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

/**
 * 功能说明：验证码接口
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/OAuth/identifyingcode")
public class IdentifyingCodeRest {
    public static Log logger = LogFactory.getLog(IdentifyingCodeRest.class);
    
    @Autowired
	private RedisTemplate<String, String> redisTemplate;
  
    
    @RequestMapping(method=RequestMethod.GET,value="/aaa")
    public String index(Model model) {
       
    	//redisTemplate.opsForValue().set("一分钟", "60=一分钟", 60L);
    	//redisTemplate.opsForValue().set("十分钟", "600=十分钟", 600L);
    	
        return new Gson().toJson(redisTemplate.opsForValue().get("一分钟").toString()+" --- "+redisTemplate.opsForValue().get("十分钟").toString());
    }
    
    
}
