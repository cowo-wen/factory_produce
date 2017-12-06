/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2016-4-21
 */
package com.app.service.wechat;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;

import com.xx.util.property.Config;

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
            weChatConfig = new WxMpInMemoryConfigStorage();
            weChatConfig.setAppId(Config.getInstance().get("wx_weChat_appid")); // 设置微信公众号的appid
            weChatConfig.setSecret(Config.getInstance().get("wx_weChat_secret")); // 设置微信公众号的app corpSecret
            weChatConfig.setToken(Config.getInstance().get("wx_weChat_token")); // 设置微信公众号的token
            weChatConfig.setAesKey(Config.getInstance().get("wx_weChat_aeskey")); // 设置微信公众号的EncodingAESKey
        }
        return weChatConfig;
    }

}
