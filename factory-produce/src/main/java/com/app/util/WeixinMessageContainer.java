package com.app.util;

import com.google.gson.JsonObject;

public class WeixinMessageContainer {
	private static RedisAPI redisAPI = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
	/**
	 * 将消息保存到推送容器
	 * @param messageType 消息类型
	 * @param userId 消息用户
	 * @param jo 消息主体
	 */
	public synchronized static void pushMessage(String messageType,Long userId,JsonObject jo){
		
		createMessage( redisAPI, messageType, userId,jo);
	}
	
	/**
	 * 将消息保存到推送容器
	 * @param redisObj 容器对象
	 * @param messageType 消息类型
	 * @param userId 消息用户
	 * @param jo 消息主体
	 */
	public synchronized static void pushMessage(RedisAPI redisObj,String messageType,Long userId,JsonObject jo){
		createMessage( redisObj, messageType, userId,jo);
	}
	
	private synchronized static void createMessage(RedisAPI redisObj,String messageType,Long userId,JsonObject jo){
		if(PublicMethod.isEmptyStr(redisObj,jo,userId)){
			return;
		}
		jo.addProperty("user_id", userId);
		jo.addProperty("message_type", messageType);//消息类型
		jo.addProperty("execute_number", 0);
        jo.addProperty("receive_time", System.currentTimeMillis());//开始时间
		redisObj.rPush(StaticBean.WEIXIN_MESSAGE_WAIT_SEND_LIST, jo.toString());
	}

}
