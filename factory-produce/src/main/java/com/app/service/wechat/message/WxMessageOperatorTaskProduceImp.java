package com.app.service.wechat.message;

import java.util.List;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.app.controller.common.Result;
import com.app.entity.sys.SysUserBindingInfo;
import com.app.service.wechat.WeiXinServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WxMessageOperatorTaskProduceImp implements WxMessageOperatorInterface {
	public static Log logger = LogFactory.getLog(WxMessageOperatorTaskProduceImp.class);
	
	@Override
	public void sendMessage(JsonObject jo) throws Exception {
		logger.error("捕获到微信消息:"+jo);
		WxMpService wxMpService = WeiXinServer.getWeChatWxMpService();
		JsonArray jaNO = new JsonArray();
		JsonArray jaYES = new JsonArray();
		String userName = "",date="",content="",remark="",first="";
		if(jo.has("user_name") && !jo.get("user_name").isJsonNull()){
			userName = jo.get("user_name").getAsString();
		}
		if(jo.has("date") && !jo.get("date").isJsonNull()){
			date = jo.get("date").getAsString();
		}
		if(jo.has("content") && !jo.get("content").isJsonNull()){
			content = jo.get("content").getAsString();
		}
		if(jo.has("remark") && !jo.get("remark").isJsonNull()){
			remark = jo.get("remark").getAsString();
		}
		if(jo.has("first") && !jo.get("first").isJsonNull()){
			first = jo.get("first").getAsString();
		}
		
		if(!jo.has("open_id")){
			List<SysUserBindingInfo> list = new SysUserBindingInfo(new Result().getJdbcDao()).setUserId(jo.get("user_id").getAsLong()).setType(SysUserBindingInfo.BINDING_TYPE_WEIXIN).queryCustomCacheValue(1);
			if(list != null && list.size() > 0){
				for(SysUserBindingInfo user : list){
					try{
						String msgid = sendMessage( wxMpService,user.getOpenId(),userName,date,content,remark,first);
						jaYES.add(user.getOpenId()+" - "+msgid);
					}catch(Exception e){
						jaNO.add(user.getOpenId());
						logger.error(user.getOpenId()+"发送消息异常", e);
					}
				}
			}else{
				//jo.addProperty("exception", "用户未绑定");
				//throw new Exception("用户未绑定");
				logger.error(jo.get("user_id").getAsLong()+"用户未绑定");
			}
		}else{
			JsonArray ja2 = jo.get("open_id").getAsJsonArray();
			for(JsonElement je : ja2){
				String openId = je.getAsString();
				try{
					String msgid = sendMessage( wxMpService,openId,userName,date,content,remark,first); 
					jaYES.add(openId+" - "+msgid);
				}catch(Exception e){
					logger.error(openId+"发送消息异常", e);
					jaNO.add(openId);
				}
			}
		}
		
		logger.error("发送结果(user_id="+jo.get("user_id").getAsLong()+"):"+jaYES.toString()+"发送成功；"+jaNO.toString()+"发送失败");
		
		
	}
	
	
	private String sendMessage(WxMpService wxMpService,String openId,String name,String date,String content,String remark,String first) throws WxErrorException{
		WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
        templateMessage.setToUser(openId);
        templateMessage.setTemplateId("MfdhkozqD6p9X-eaRnU6qTkXw-xvM7y_htfoL8nmNTE");
        templateMessage.getDatas().add(new WxMpTemplateData("keyword1", name, "#173177"));
        templateMessage.getDatas().add(new WxMpTemplateData("keyword2", date, "#173177"));
        templateMessage.getDatas().add(new WxMpTemplateData("keyword3", content.replaceAll("&#39;", "\'"), "#173177"));
        templateMessage.getDatas().add(new WxMpTemplateData("first", first, "#173177"));
        templateMessage.getDatas().add(new WxMpTemplateData("remark", remark, "#173177"));
        //templateMessage.setUrl(url + "/html/guardian/assiginment_list.html?studentid=" + studentId+"&date="+PublicMethod.formatDateStr(new Date(),"yyyy-MM-dd")+"&openid="+openId);
		
		return wxMpService.templateSend(templateMessage);
	}

}
