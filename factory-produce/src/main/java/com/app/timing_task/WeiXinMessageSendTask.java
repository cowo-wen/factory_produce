/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2014-12-28
 */
package com.app.timing_task;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.app.service.wechat.message.WxMessageOperatorInterface;
import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.app.util.StaticBean;
import com.app.util.WeixinMessageContainer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xx.util.string.Format;

/**
 * 功能说明：微信消息发送任务
 * 
 * @author chenwen 2017-12-22
 */
public class WeiXinMessageSendTask implements Runnable
{
    public static Log logger = LogFactory.getLog(WeiXinMessageSendTask.class);

    private static boolean isRun = false;

    

    private WeiXinMessageSendTask() {
		super();
	}


	@Override
    public void run()
    {
        
		RedisAPI redisAPI = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
        try
        {
        	while(isRun){
        		long len = redisAPI.lLen(StaticBean.WEIXIN_MESSAGE_WAIT_SEND_LIST);
            	if(len > 0){
            		while(len > 0){
            			String result = redisAPI.lPop(StaticBean.WEIXIN_MESSAGE_WAIT_SEND_LIST);
            			if(!PublicMethod.isEmptyStr(result)){
            				try{
            					JsonObject jo = new JsonParser().parse(result).getAsJsonObject();
                				if(jo.has("message_type") && !jo.get("message_type").isJsonNull() && jo.has("user_id") && !jo.get("user_id").isJsonNull() ){
                					Constructor<?> constructor=Class.forName(jo.get("message_type").getAsString()).getConstructor();
                					WxMessageOperatorInterface imp =  (WxMessageOperatorInterface) constructor.newInstance();
                					try{
                						imp.sendMessage(jo);
                					}catch(Exception e){
                						if(jo.has("exception")){
                							logger.error("消息执行失败："+jo.toString(),e);
                						}else if(jo.has("execute_number") && !jo.get("execute_number").isJsonNull() && Format.isNumeric(jo.get("execute_number").getAsString())){
                							int number = 0;
                							if((number = jo.get("execute_number").getAsInt()) < 3){
                								jo.addProperty("execute_number", ++number);
                    							WeixinMessageContainer.pushMessage(redisAPI ,jo.get("message_type").getAsString(), jo.get("user_id").getAsLong(), jo);
                							}else{
                								logger.error("消息执行失败："+jo.toString(),e);
                							}
                						}else{
                							jo.addProperty("execute_number", 1);
                							WeixinMessageContainer.pushMessage(redisAPI ,jo.get("message_type").getAsString(), jo.get("user_id").getAsLong(), jo);
                						}
                					}
                					
                				}else{
                					logger.error("微信推诚送消息不规范（缺少message_type或user_id字段）:"+result);
                				}
            				}catch(Exception e){
            					e.printStackTrace();
            					logger.error("处理微信推送消息失败:"+result, e);
            				}
            			}
            			--len;
            		}
            	}else{
            		Thread.sleep(30000);
            	}
        	}
        }
        catch (Exception e)
        {
        	
            e.printStackTrace();
            logger.error("微信消息发送任务====================" + e.getMessage());
           
        }
        finally
        {
            
            isRun = false;
        }

    }
	
    /**
     * 关闭线程
     * 
     * @return
     * @author chenwen 2017-12-22
     */
    public static boolean closeThread()
    {
        isRun = false;
        return isRun;
    }

    /**
     * 启动线程
     * 
     * @author chenwen 2017-12-22
     */
    public synchronized static void startThread()
    {
        if (!isRun)
        {
        	isRun = true;
            new Thread(new WeiXinMessageSendTask()).start();
        }
    }

}
