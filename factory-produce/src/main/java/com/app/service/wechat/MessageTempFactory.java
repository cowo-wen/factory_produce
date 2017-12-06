/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2016-4-27
 */
package com.app.service.wechat;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;

import org.apache.log4j.Logger;

/**
 * 功能说明：故障通报通知 模板
 * 
 * @author chenwen 2016-4-27
 */
public class MessageTempFactory
{
    protected static Logger logger = Logger.getLogger(MessageTempFactory.class);

    

    
    /**
     * 发送组装的客服消息对象
     * 
     * @param userId
     *            微信的openid
     * @param content
     *            客服内容
     * @return
     * @author chenwen 2016-4-27
     */
    public void sendWxMpCustomMessage(String userId, String content)
    {

        sendWxMpCustomMessage(WeiXinServer.getWeChatWxMpService(), userId, content);
    }

    /**
     * 发送组装的客服消息对象
     * 
     * @param userId
     *            微信的openid
     * @param content
     *            客服内容
     * @return
     * @author chenwen 2016-4-27
     */
    public void sendWxMpCustomMessage(WxMpService wxMpService, String userId, String content)
    {
        try
        {
            if(wxMpService == null){
                wxMpService = WeiXinServer.getWeChatWxMpService();
            }
            
            /**
            if(wxMpService.getClass().equals(WxGuardianServiceImpl.class)){
                CustomMessageBean.addCustomMessage(0, userId, content);
            }else{
                CustomMessageBean.addCustomMessage(2, userId, content);
            }*/
            wxMpService.customMessageSend(WxMpCustomMessage.TEXT().toUser(userId).content(content).build());
            
        }
        catch (Exception e)
        {
            logger.error("发送自定义消息失败:"+userId+"|"+content, e);
        }
    }
}
