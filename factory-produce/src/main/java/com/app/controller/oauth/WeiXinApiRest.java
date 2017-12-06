package com.app.controller.oauth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.controller.common.Result;
import com.app.service.wechat.MessageTempFactory;
import com.app.service.wechat.WeiXinServer;
import com.app.service.wechat.WeiXinStaticBean;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.JsonObject;
import com.xx.util.property.Config;

/**
 * 功能说明：微信接口
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/OAuth/weixin")
public class WeiXinApiRest extends Result{
    public static Log logger = LogFactory.getLog(WeiXinApiRest.class);
    
  
    @Autowired  
    private HttpServletRequest request;
    
    @Autowired  
    private HttpServletResponse response;
    
    
    /**
     * 微信验证接口
     * @return
     * @throws Exception
     */
    @RequestMapping(method=RequestMethod.GET,value="/api")
    public String getAPI() throws Exception{
    	
    	return checkAPI(1);
    	
        
    }
    
    @POST
    @Path("/api")
    public String postAPI()
    {
        try
        {

            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            String msgType = inMessage.getMsgType();// 获取消息类型，event是事件，text是回复内容
            String userOpenId = inMessage.getFromUserName();// 获取微信用户的openid;
            RedisAPI redis = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
            redis.put(userOpenId, "true");
            redis.expire(userOpenId,300);
            if (PublicMethod.isEmptyStr(userOpenId))
            {// 判断微信用户的openid是否为空
                logger.error("获取到的微信用户openid为空");
            }
            else
            {
                
                
                if (PublicMethod.isEmptyStr(msgType))
                {
                    logger.error("获取到msgType=null的情况");
                }
                else
                {
                    if (msgType.equals(WeiXinStaticBean.MSG_TYPE.event.toString()))
                    {
                        String event = inMessage.getEvent();
                        if (PublicMethod.isEmptyStr(event))
                        {
                            logger.error("获取到event=null的情况");
                        }
                        else
                        {
                            String eventKey = inMessage.getEventKey();
                            if (PublicMethod.isEmptyStr(eventKey))
                            {
                                if (event.equals(WeiXinStaticBean.EVENT.subscribe.toString()))
                                {// 关注
                                   logger.error("用户"+userOpenId+"进行了关注");
                                }
                                else if (event.equals(WeiXinStaticBean.EVENT.unsubscribe.toString()))
                                {// 取消关注
                                   
                                }
                                else
                                {
                                    if(!PublicMethod.isEmptyStr(inMessage.getMsgId())){//消息回调
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("msgid", inMessage.getMsgId());
                                        jsonObject.addProperty("wx_time", inMessage.getCreateTime());
                                    }
                                    

                                }
                            }
                            else
                            {
                                if (event.equals(WeiXinStaticBean.EVENT.view.toString()))
                                {// EventKey 为菜单的url值
                                 // 该逻辑暂不需实现
                                    //logger.error("点击了按钮 url=" + eventKey);
                                }
                                else if (event.equals(WeiXinStaticBean.EVENT.click.toString()))
                                {// EventKey为菜单的key值
                                    //logger.error(userOpenId + "--------" + eventKey);
                                	if (eventKey.equals("V3001_CUSTOMSERVICE"))// 接入客服
                                    {
                                		StringBuffer sb = new StringBuffer("<xml>");
                                        sb.append("<ToUserName>").append(userOpenId).append("</ToUserName>");
                                        sb.append("<FromUserName>").append(Config.getInstance().get("wx_guardian_account","baiyuesoft")).append("</FromUserName>");
                                        sb.append("<CreateTime>").append(System.currentTimeMillis()/1000).append("</CreateTime>");
                                        sb.append("<MsgType>").append("transfer_customer_service").append("</MsgType>");
                                        sb.append("</xml>");
                                        response.setContentType("text/html;charset=utf-8");
                                        response.setStatus(HttpServletResponse.SC_OK);
                                        response.getWriter().println(sb.toString());
                                        String key = "customservice:help:"+userOpenId;
                                        if(!redis.exists(key)){
                                        	StringBuffer buffer = new StringBuffer();
                                            //buffer.append("您好！正在为你接入客服。由于目前咨询人数较多，可能需要排队接入，请耐心等候...\n");
                                            buffer.append("您好！客服工作时间是周一至周五9:00--17:30。由于咨询人数较多，可能需要排队接入，请耐心等候...\n");
                                            
                                            new MessageTempFactory().sendWxMpCustomMessage(userOpenId, buffer.toString());
                                            redis.put(key, "true", 120);
                                        }
                                        
                                    }
                                	
                                }
                                else
                                {
                                    logger.error("未识别的event类型");
                                }
                            }
                        }
                    }
                    else if (msgType.equals(WeiXinStaticBean.MSG_TYPE.text.toString()))
                    {// Content有数据 输入文字
                        String content = inMessage.getContent();

                        if (PublicMethod.isEmptyStr(content))
                        {
                            logger.error("获取到content=null的情况");
                        }
                        else
                        {    content = content.trim();
                            if (content.equals("帮助") || content.equals("研学"))
                            {
                                StringBuffer sb = new StringBuffer("<xml>");
                                sb.append("<ToUserName>").append(userOpenId).append("</ToUserName>");
                                sb.append("<FromUserName>").append(Config.getInstance().get("wx_guardian_account","baiyuesoft")).append("</FromUserName>");
                                sb.append("<CreateTime>").append(System.currentTimeMillis()/1000).append("</CreateTime>");
                                sb.append("<MsgType>").append("transfer_customer_service").append("</MsgType>");
                                sb.append("</xml>");
                                //response.setContentType("text/html;charset=utf-8");
                                //response.setStatus(HttpServletResponse.SC_OK);
                                //response.getWriter().println(sb.toString());
                                if(!redis.exists("customservice:help:"+userOpenId)){
                                	StringBuffer buffer = new StringBuffer();
                                    buffer.append("您好！我是校园卫士小编，如有任何疑问或建议，请点击：我的--客服，联系我们的客服MM。").append("\n\n");
                                    buffer.append("客服时间：周一至周五9:00--17:30。").append("\n");
                                    buffer.append("如客服不在线，可直接留言，我们会尽快回复您！");
                                    new MessageTempFactory().sendWxMpCustomMessage(userOpenId, buffer.toString());
                                }
                                
                            }
                        }
                    }
                    else
                    {
                        logger.error("未识别的事件类型");
                    }
                }
            }
            return success("");

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return error(e.getMessage());
        }
        finally
        {

        }
    }
    
    /**
     * 
     * @param app 1为家长，2为老师
     * @return
     * @author chenwen 2016-9-9
     */
    private String checkAPI(int app)
    {
        try
        {
            boolean bool = false;
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            if (PublicMethod.isEmptyStr(signature, timestamp, nonce))
            {
                return "请求参数不能全为空";
            }
            WxMpService wxMpService = new WxMpServiceImpl();
            bool = wxMpService.checkSignature(timestamp, nonce, signature);
            
            logger.error("-----------------boolean:"+bool);
            String echostr = request.getParameter("echostr");
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            
            if (StringUtils.isNotBlank(echostr))
            {
                response.getWriter().println(echostr);
                logger.error("------------------------------------微信进行证");
            }
            
            return "";

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    
 
    /**
     * 数字签名算法
     * @param decript
     * @return
     * @author chenwen 2017-4-7
     */
    private String SHA1(String decript) {  
        try {  
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");  
            digest.update(decript.getBytes());  
            byte messageDigest[] = digest.digest();  
            StringBuffer hexString = new StringBuffer();  
            for (int i = 0; i < messageDigest.length; i++) {  
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);  
                if (shaHex.length() < 2) {  
                    hexString.append(0);  
                }  
                hexString.append(shaHex);  
            }  
            return hexString.toString();  
   
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return "";  
    }  
    
    /**
     * 获取老师公众号的js签名
     * @return
     * @author chenwen 2017-4-7
     */
    @POST
    @Path("/signature")
    public String signature(@FormParam("url") String url)
    {
        try
        {
            Map<String, Object> map = new HashMap<String,Object>();
            WxMpService wxMpService = WeiXinServer.getWeChatWxMpService();
            RedisAPI redis = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
            String keyJsApiTicket="temp:jsapiticket:code",jsapiTicket = redis.get(keyJsApiTicket);
            
            if(PublicMethod.isEmptyStr(jsapiTicket)){
                jsapiTicket = wxMpService.getJsapiTicket(true);
                redis.putTowHours(keyJsApiTicket, jsapiTicket);
            }
            
            String noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//随机字符串  
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳
            String str = "jsapi_ticket="+jsapiTicket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;  
            String signature =SHA1(str);
            String appid = WeiXinServer.getweChatWxMpInMemoryConfigStorage().getAppId();
            map.put("jsapi_ticket", jsapiTicket);
            map.put("appid", appid);
            map.put("timestamp", timestamp);
            map.put("noncestr", noncestr);
            map.put("signature", signature);
            return success(map);
        }catch(Exception e){
            logger.error("获取认证失败:"+url, e);
            return error(e.getMessage());
        }
    }
   
}
