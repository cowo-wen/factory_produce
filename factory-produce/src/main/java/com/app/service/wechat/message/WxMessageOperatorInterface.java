package com.app.service.wechat.message;

import com.google.gson.JsonObject;


/**
 * 微主消息处理接口
 * @author cowo
 *
 */
public interface WxMessageOperatorInterface {
	
	
	public void sendMessage(JsonObject jo) throws Exception;

}
