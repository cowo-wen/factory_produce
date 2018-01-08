package com.app.controller.oauth;

import java.util.List;

import javax.servlet.http.HttpSession;

import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.controller.common.Result;
import com.app.entity.sys.SysUserBindingInfo;
import com.app.entity.sys.SysUserEntity;
import com.app.service.sys.UserCache;
import com.app.service.wechat.WeiXinServer;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.app.util.StaticBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xx.util.string.MD5;

/**
 * 功能说明：微信个人中心
 * 
 * @author chenwen 2017-12-13
 */
@RestController
@RequestMapping("/OAuth/wx_user")
@Scope("prototype")
public class WXUserInfoApi extends Result{
	
    public static Log logger = LogFactory.getLog(WXUserInfoApi.class);
    
    public static final String TEMP_WETHAT_TOKEN_CODE ="temp:wethat:token:code:";
    
  
    
    @Autowired  
    private HttpSession session;
    
    
    
    

    @RequestMapping(method=RequestMethod.GET,value="/userinfo/{code}")
    public String userinfo(@PathVariable("code") String code)
    {
        try
        {
        	String key = TEMP_WETHAT_TOKEN_CODE+code;
        	RedisAPI redis = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
        	boolean bool = redis.exists(key);
        	if(!bool){
        		WxMpOAuth2AccessToken token = WeiXinServer.getWeChatWxMpService().oauth2getAccessToken(code);
                redis.putTenMinutes(key,token.getOpenId());
        	}
        	
        	SysUserBindingInfo userBind = new SysUserBindingInfo(jdbcDao);
        	List<SysUserBindingInfo> list = userBind.setType(1).setOpenId(redis.get(key)).queryCustomCacheValue(0);
            JsonArray ja = new JsonArray();
        	for(SysUserBindingInfo ub : list){
            	JsonObject jo = new JsonObject();
            	jo.addProperty(SysUserBindingInfo.NICKNAME, ub.getNickname());
            	jo.addProperty(SysUserBindingInfo.HEAD_IMG_URL, ub.getHeadImgUrl());
            	SysUserEntity user = new SysUserEntity(jdbcDao);
            	user.setUserId(ub.getUserId()).loadVo();
            	if(user.getValid() == StaticBean.YES){
            		jo.addProperty(SysUserEntity.USER_NAME, user.getUserName());
                	jo.addProperty(SysUserEntity.NUMBER, user.getNumber());
                	jo.addProperty(SysUserEntity.MOBILE, user.getMobile());
                	ja.add(jo);
            	}
            	
            }
            return success(ja);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("获取用户异常:", e);
            return error("获取用户异常:"+e.getMessage());
        }

        
    }
    
    
    
    
    /**
     * 用户绑定接口
     * @param code
     * @param login_user
     * @param password
     * @param token
     * @return
     */
    @RequestMapping(method=RequestMethod.POST,value="/binding_user")
    public String binding(@RequestParam String code,@RequestParam String pw,@RequestParam String login_name,@RequestParam String token)
    {
        try
        {
        	if(PublicMethod.isEmptyStr(token)){
        		return error("验证码不能为空");
        	}
        	if(PublicMethod.isEmptyStr(code)){
        		return error("令牌不能为空");
        	}
        	if(PublicMethod.isEmptyStr(login_name)){
        		return error("帐号不能为空");
        	}
        	if(PublicMethod.isEmptyStr(pw)){
        		return error("密码不能为空");
        	}
        	String identifyingcode = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE).get(IdentifyingCodeAPI.IDENTIFYINGCODE_LOGIN+session.getId());
        	if(PublicMethod.isEmptyStr(identifyingcode)){
        		return error("验证码已失效");
        	}
        	
        	if(!identifyingcode.equals(token)){
        		return error("验证码不正确");
        	}
        	
        	
        	String key = TEMP_WETHAT_TOKEN_CODE+code;
        	RedisAPI redis = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
        	boolean bool = redis.exists(key);
        	if(!bool){
        		WxMpOAuth2AccessToken accessToken = WeiXinServer.getWeChatWxMpService().oauth2getAccessToken(code);
                redis.putTenMinutes(key,accessToken.getOpenId());
        	}
        	String openId = redis.get(key);
        	if(PublicMethod.isEmptyStr(openId)){
        		return error("非法的关注用户，请重新关注");
        	}
        	
        	SysUserEntity user = new SysUserEntity(jdbcDao);
        	List<SysUserEntity> userList = user.setLoginName(login_name).queryCustomCacheValue(0);
        	if(userList == null || userList.size() == 0){
        		return error("不存在的用户数据");
        	}
        	
        	
        	
        	if(userList.get(0).getValid() == StaticBean.NO){
        		return error("用户已被禁用，不能绑定");
        	}else if(userList.get(0).getValid() == StaticBean.YES){
        		
        	}else{
        		return error("不存在的用户");
        	}
        	
    		if(!userList.get(0).getPassword().equals(MD5.encode(pw))){
        		return error("密码不正确");
        	}
        	
        	SysUserBindingInfo userBind = new SysUserBindingInfo(jdbcDao);
        	List<SysUserBindingInfo> list = userBind.setType(1).setUserId(userList.get(0).getUserId()).queryCustomCacheValue(1);
        	if(list != null && list.size() > 0){
        		return error("该用户已被绑定，请联系管理员解绑再试");
        	}
        	 WxMpUser wxMpUser = WeiXinServer.getWeChatWxMpService().userInfo(openId, null);
             
        	SysUserBindingInfo binding = new SysUserBindingInfo(jdbcDao);
        	binding.setNickname(wxMpUser.getNickname());
        	binding.setHeadImgUrl(wxMpUser.getHeadImgUrl());
        	binding.setType(1);
        	binding.setUserId(userList.get(0).getUserId());
        	binding.setOpenId(openId);
        	binding.insert();
        	
        	JsonObject jo = new JsonObject();
        	jo.addProperty(SysUserBindingInfo.NICKNAME, binding.getNickname());
        	jo.addProperty(SysUserBindingInfo.HEAD_IMG_URL, binding.getHeadImgUrl());
        	jo.addProperty(SysUserEntity.USER_NAME, userList.get(0).getUserName());
        	jo.addProperty(SysUserEntity.NUMBER, userList.get(0).getNumber());
        	jo.addProperty(SysUserEntity.MOBILE, userList.get(0).getMobile());
            return success(jo);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return error("绑定异常:"+e.getMessage());
        }

        
    }
    
    
    /**
     * 用户解除绑定接口
     * @param code
     * @param login_user
     * @param password
     * @param token
     * @return
     */
    @RequestMapping(method=RequestMethod.POST,value="/cancel_user")
    public String cancelBinding(@RequestParam String code)
    {
        try
        {
        	
        	if(PublicMethod.isEmptyStr(code)){
        		return error("令牌不能为空");
        	}
        	String key = TEMP_WETHAT_TOKEN_CODE+code;
        	RedisAPI redis = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
        	boolean bool = redis.exists(key);
        	if(!bool){
        		WxMpOAuth2AccessToken accessToken = WeiXinServer.getWeChatWxMpService().oauth2getAccessToken(code);
                redis.putTenMinutes(key,accessToken.getOpenId());
        	}
        	String openId = redis.get(key);
        	if(PublicMethod.isEmptyStr(openId)){
        		return error("非法的关注用户，请重新关注");
        	}
        	
        	SysUserBindingInfo userBind = new SysUserBindingInfo(jdbcDao);
        	List<SysUserBindingInfo> list = userBind.setType(1).setOpenId(openId).queryCustomCacheValue(0);
        	if(list != null && list.size() > 0){
        		for(SysUserBindingInfo ub : list){
        			UserCache.delUserLoginTemp(ub.getUserId());//删除用户的登录缓存
        			ub.delete();
        		}
        	}else{
        		return error("未绑定用户，不用解绑");
        	}
        	 
            return success("解绑成功");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return error("解除绑定异常:"+e.getMessage());
        }
        
    }
    
    /**
     * 修改密码
     * @param code
     * @param login_user
     * @param password
     * @param token
     * @return
     */
    @RequestMapping(method=RequestMethod.POST,value="/update_pw")
    public String updatePW(@RequestParam String aoData)
    {
        try
        {
        	JsonObject jo = new JsonParser().parse(aoData).getAsJsonObject();
        	
        	String token = null;
        	if(!jo.has("token") || PublicMethod.isEmptyStr(token = jo.get("token").getAsString())){
        		return error("验证码不能为空");
        	}
        	if(!jo.has("code") || PublicMethod.isEmptyStr(jo.get("code").getAsString())){
        		return error("令牌不能为空");
        	}
        	if(!jo.has("old_password") || PublicMethod.isEmptyStr(jo.get("old_password").getAsString())){
        		return error("旧密码不能为空");
        	}
        	if(!jo.has("new_password") || PublicMethod.isEmptyStr(jo.get("new_password").getAsString())){
        		return error("新密码不能为空");
        	}
        	if(!jo.has("confirm_password") || PublicMethod.isEmptyStr(jo.get("confirm_password").getAsString())){
        		return error("确认密码不能为空");
        	}
        	if(!jo.get("confirm_password").getAsString().equals(jo.get("new_password").getAsString())){
        		return error("密码输入不一致");
        	}
        	
        	
        	
        	String identifyingcode = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE).get(IdentifyingCodeAPI.IDENTIFYINGCODE_LOGIN+session.getId());
        	if(!identifyingcode.equals(token)){
        		return error("验证码不正确");
        	}
        	
        	
        	String key = TEMP_WETHAT_TOKEN_CODE+jo.get("code").getAsString();
        	RedisAPI redis = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
        	boolean bool = redis.exists(key);
        	if(!bool){
        		WxMpOAuth2AccessToken accessToken = WeiXinServer.getWeChatWxMpService().oauth2getAccessToken(jo.get("code").getAsString());
                redis.putTenMinutes(key,accessToken.getOpenId());
        	}
        	String openId = redis.get(key);
        	if(PublicMethod.isEmptyStr(openId)){
        		return error("非法的关注用户，请重新关注并绑定");
        	}
        	
        	List<SysUserBindingInfo> list = new SysUserBindingInfo(jdbcDao).setType(1).setOpenId(openId).queryCustomCacheValue(0);
        	if(list == null || list.size() == 0){
        		return error("未绑定用户");
        	}else{
        		SysUserEntity user = new SysUserEntity(jdbcDao);
        		user.setUserId(list.get(0).getUserId()).loadVo();
        		if(!user.getPassword().equals(MD5.encode(jo.get("old_password").getAsString()))){
            		return error("旧密码不正确");
            	}else{
            		user.setPassword(MD5.encode(jo.get("new_password").getAsString())).update(SysUserEntity.PASSWORD);
            		return success("密码已修改");
            	}
        	}
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("修改密码异常",e);
            return error("密码已修改:"+e.getMessage());
        }

        
    }
    
    
   
}
