/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2016-4-21
 */
package com.app.service.wechat;

import java.util.List;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;

import com.app.controller.common.Result;
import com.app.entity.sys.SysConfigEntity;

/**
 * 功能说明：
 * 
 * @author chenwen 2016-4-21
 */
public class WeiXinServer
{

    private static WxMpInMemoryConfigStorage weChatConfig;


    public synchronized static WxMpService getWeChatWxMpService()
    {
        WxMpService wxMpService = new WxMessageSendServiceImpl();
        wxMpService.setWxMpConfigStorage(getweChatWxMpInMemoryConfigStorage());
        return wxMpService;
    }

   


    public synchronized static WxMpInMemoryConfigStorage getweChatWxMpInMemoryConfigStorage()
    {
        if (weChatConfig == null)
        {
        	
        	List<SysConfigEntity> list = new SysConfigEntity(new Result().getJdbcDao()).setGroupCode("wechat").queryCustomCacheValue(0);
            weChatConfig = new WxMpInMemoryConfigStorage();
            for(SysConfigEntity config : list){
            	if(config.getGroupType().equals("appId")){
            		weChatConfig.setAppId(config.getValue()); // 设置微信公众号的appid
            	}else if(config.getGroupType().equals("secret")){
            		weChatConfig.setSecret(config.getValue()); // 
            	}else if(config.getGroupType().equals("token")){
            		weChatConfig.setToken(config.getValue()); // 
            	}else if(config.getGroupType().equals("aesKey")){
            		weChatConfig.setAesKey(config.getValue()); // 
            	}
            }
            //weChatConfig = new WxMpInMemoryConfigStorage();
            //weChatConfig.setAppId(Config.getInstance().get("wx_weChat_appid")); // 设置微信公众号的appid
            //weChatConfig.setSecret(Config.getInstance().get("wx_weChat_secret")); // 设置微信公众号的app corpSecret
            //weChatConfig.setToken(Config.getInstance().get("wx_weChat_token")); // 设置微信公众号的token
            //weChatConfig.setAesKey(Config.getInstance().get("wx_weChat_aeskey")); // 设置微信公众号的EncodingAESKey
        }
        return weChatConfig;
    }

}
