package com.app.controller.oauth;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.bean.WxMenu.WxMenuButton;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.controller.common.Result;
import com.app.dao.sql.SQLWhere;
import com.app.dao.sql.cnd.EQCnd;
import com.app.dao.sql.sort.AscSort;
import com.app.entity.sys.SysApplicationEntity;
import com.app.entity.sys.SysUserBindingInfo;
import com.app.service.sys.UserCache;
import com.app.service.wechat.MessageTempFactory;
import com.app.service.wechat.WeiXinServer;
import com.app.service.wechat.WeiXinStaticBean;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.app.util.StaticBean;
import com.app.util.tree.Node;
import com.app.util.tree.TreeBuilder;

/**
 * 功能说明：微信接口
 * 
 * @author chenwen 2017-7-13
 */
@RestController
@RequestMapping("/OAuth/weixin")
@Scope("prototype")
public class WeiXinApi extends Result{
	/**
	 * 帐号:m13600222781@163.com
	 * 密码:QQ2716903  proxy_pass      	http://free.ngrok.cc;
	 */
    public static Log logger = LogFactory.getLog(WeiXinApi.class);
    
  
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
    
    /**
     * 微信验证接口
     * @return
     * @throws Exception
     */
    @RequestMapping(method=RequestMethod.GET,value="/menu")
    public String menu() throws Exception{
    	WxMenu menu = new WxMenu();
    	List<WxMenuButton> buttons = new ArrayList<WxMenuButton>();
    	List<Node> listNode = new ArrayList<Node>();
    	List<SysApplicationEntity> list = new SysApplicationEntity(jdbcDao).getListVO(new SQLWhere(new EQCnd(SysApplicationEntity.TERMINAL_TYPE,2)).and(new EQCnd(SysApplicationEntity.VALID, StaticBean.YES)).orderBy(new AscSort(SysApplicationEntity.SORT_CODE,SysApplicationEntity.APPLICATION_ID)));
    	//String http = "http://com.tunnel.qydev.com";
    	String http = "http://factory.exyws.org";
    	for(SysApplicationEntity entity : list){
    		Node node = new Node();
    		node.setObj(entity);
    		node.setId(entity.getApplicationId());
    		node.setParentId(entity.getParentId());
    		node.setName(entity.getName());
    		node.setCode(entity.getApplicationCode());
    		node.setType(entity.getEventType());
    		node.setSort(entity.getSortCode());
    		listNode.add(node);
    	}
    	TreeBuilder tree = new TreeBuilder();
    	listNode = tree.buildListToTree(listNode);
    	for(int i = 0,len = listNode.size();i < len;i++){
    		Node node = listNode.get(i);
    		for(Node childrenMenu : node.getChildren()){
    			SysApplicationEntity app = (SysApplicationEntity)childrenMenu.getObj();
    			WxMenuButton wmb = new WxMenuButton();
			     wmb.setKey(childrenMenu.getCode());
			     wmb.setName(childrenMenu.getName());
    			if(app.getAppType() == 2){//导航
    				wmb.setType("view");
    				String url = app.getUrl();
    				if(!url.startsWith("http")){
    					url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+WeiXinServer.getweChatWxMpInMemoryConfigStorage().getAppId()+"&redirect_uri="+URLEncoder.encode(http+url,"UTF-8")+"&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
    				}
    				wmb.setUrl(url);
    			}else{
    				wmb.setType("click");
    				List<WxMenuButton> buttonSub = new ArrayList<WxMenuButton>();
    				for(Node childrenNav : childrenMenu.getChildren()){
    					WxMenuButton wmbSub = new WxMenuButton();
    					SysApplicationEntity app2 = (SysApplicationEntity)childrenNav.getObj();
    					wmbSub.setKey(app2.getApplicationCode());
    					wmbSub.setName(app2.getName());
    					if(app2.getAppType() == 2){//导航
    						wmbSub.setType("view");
    						String url = app2.getUrl();
    	    				if(!url.startsWith("http")){
    	    					url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+WeiXinServer.getweChatWxMpInMemoryConfigStorage().getAppId()+"&redirect_uri="+URLEncoder.encode(http+url,"UTF-8")+"&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
    	    				}
    	    				wmbSub.setUrl(url);
    					}else{
    						wmbSub.setType("click");
    					}
    					buttonSub.add(wmbSub);
            		}
    				wmb.setSubButtons(buttonSub);
    			}
    			buttons.add(wmb);
    		}
    	}
        try{
        	 WxMpService wxMpService = WeiXinServer.getWeChatWxMpService();
        	 menu.setButtons(buttons);
        	 wxMpService.menuCreate(menu);
        	 return success("重建成功");
        }catch (Exception e){
             e.printStackTrace();
             logger.error("创建菜单失败", e);
             return error(e.getMessage());
        }
        
    }
    
    
    
    @RequestMapping(method=RequestMethod.POST,value="/api")
    public String postAPI()
    {
        try
        {

            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            String msgType = inMessage.getMsgType();// 获取消息类型，event是事件，text是回复内容
            String userOpenId = inMessage.getFromUserName();// 获取微信用户的openid;
           
            if (PublicMethod.isEmptyStr(userOpenId))
            {// 判断微信用户的openid是否为空
                logger.error("获取到的微信用户openid为空");
            }
            else
            {
                if (PublicMethod.isEmptyStr(msgType))//消息类型
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
                            if (PublicMethod.isEmptyStr(eventKey))//事件代码为空
                            {
                                if (event.equals(WeiXinStaticBean.EVENT.subscribe.toString()))
                                {// 关注
                                   logger.error("用户"+userOpenId+"进行了关注");
                                }
                                else if (event.equals(WeiXinStaticBean.EVENT.unsubscribe.toString()))
                                {// 取消关注
                                	logger.error("*用户"+userOpenId+"取消了关注*");
                                	SysUserBindingInfo userBind = new SysUserBindingInfo(jdbcDao);
                                	List<SysUserBindingInfo> list = userBind.setType(1).setOpenId(userOpenId).queryCustomCacheValue(0);
                                	if(list != null && list.size() > 0){
                                		for(SysUserBindingInfo ub : list){
                                			UserCache.delUserLoginTemp(ub.getUserId());//删除用户的登录缓存
                                			ub.delete();
                                		}
                                	}
                                	
                                }
                                else
                                {
                                    if(!PublicMethod.isEmptyStr(inMessage.getMsgId())){//消息回调
                                    	logger.error("发送的模板消息回调");
                                    }else{
                                    	logger.error("其他事件----------");
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
                                	StringBuffer buffer = new StringBuffer();
                                    buffer.append("您好！服务正在开发").append("\n\n");
                                    buffer.append("客服时间：周一至周五9:00--17:30。").append("\n");
                                    buffer.append("如客服不在线，可直接留言，我们会尽快回复您！");
                                    new MessageTempFactory().sendWxMpCustomMessage(userOpenId, buffer.toString());
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
                        {    
                        	content = content.trim();
                        	logger.error("=====用户输入的内容=========");
                        }
                    }
                    else
                    {
                        logger.error("=====未识别的事件类型=========");
                    }
                }
            }
            success("");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            error(e.getMessage());
        }
        finally
        {

        }
        return "";
    }
    
    /**
     * 
     * @param 
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
            
            /**
            List<SysConfigEntity> list = new SysConfigEntity(jdbcDao).setGroupCode("wechat").queryCustomCacheValue(0);
            WxMpInMemoryConfigStorage weChatConfig = new WxMpInMemoryConfigStorage();
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
            //weChatConfig.setAppId("wx7104f006a0fdf33b"); // 设置微信公众号的appid
            //weChatConfig.setSecret("89aa2da3ea78f3303d3625d91c63eb85"); // 设置微信公众号的app corpSecret
            //weChatConfig.setToken("abc123321cba"); // 设置微信公众号的token
            //weChatConfig.setAesKey("2cqD7qGAjhKUXrtdn6BoRLg3WzJ19bOJzi6TN80msVA"); // 设置微信公众号的EncodingAESKey
            */
            wxMpService.setWxMpConfigStorage(WeiXinServer.getweChatWxMpInMemoryConfigStorage());
            bool = wxMpService.checkSignature(timestamp, nonce, signature);
            String echostr = request.getParameter("echostr");
            if(bool && StringUtils.isNotBlank(echostr)){
            	
            	return echostr;
            }else{
            	return "";
            }
            

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
