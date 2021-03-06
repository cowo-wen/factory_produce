package com.app.controller.oauth;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.util.RedisAPI;
import com.xx.util.img.VerifyCode;
import com.xx.util.img.VerifyCode.Img;

/**
 * 功能说明：验证码接口
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/OAuth/identifyingcode")
@Scope("prototype")
public class IdentifyingCodeAPI {
    public static Log logger = LogFactory.getLog(IdentifyingCodeAPI.class);
    
    public static final String IDENTIFYINGCODE_LOGIN="identifyingcode:login:";
    
  
    @Autowired  
    private HttpSession session;
    
    
    @RequestMapping(method=RequestMethod.GET,value="/base64")
    public String base64(Model model) throws Exception{
    	
		Img img = VerifyCode.getImgCode(120, 41, 4, "1234567890");
		
		try{
			new RedisAPI(RedisAPI.REDIS_CORE_DATABASE).put(IDENTIFYINGCODE_LOGIN+session.getId(), img.verifyCode, 120);
			return img.base64String;
		}catch(Exception e){
			logger.error("验证码获取异常", e);
			throw new Exception("验证码获取异常");
		}
		
    	
        
    }
    
 
    
   
}
