package com.app.util;

public class StaticBean
{
	public static final String _T = "\t";

    public static final String _N = "\n";

    public static final String _R = "\r";

	public static final int YES = 1;
	
	public static final int NO = 2;
	
	public static final int WAIT = 3;
	
	/**
	 * 等待发送微信数据的消息容器地址
	 */
	public static final String WEIXIN_MESSAGE_WAIT_SEND_LIST = "weixin:message:list:wait_send";
	
	/**
	 * 生产任务审核提醒消息推送
	 */
	public static final String WEIXIN_MESSAGE_TYPE_TASK_CHECKWAIT_MESSAGE = "com.app.service.wechat.message.WxMessageOperatorCheckWaitImp";
	
	/**
	 * 生产任务消息推送到工人
	 */
	public static final String WEIXIN_MESSAGE_TYPE_TASK_PRODUCE_MESSAGE = "com.app.service.wechat.message.WxMessageOperatorTaskProduceImp";
	
    /**
     * 
     * 
     * @author 是否有效
     */
    public static enum YES_OR_NO
    {
        yes
        {

            public String toString()
            {

                return String.valueOf(YES);
            }
        },
        no
        {

            public String toString()
            {

            	 return String.valueOf(NO);
            }
        };
    }

    
    /**
     * 2013-09-17
     * Parameter取值名称
     * 
     * @author cowo
     */
    public static enum CHAR_CODE
    {
        utf_8
        {

            public String toString()
            {

                return "UTF-8";
            }
        },
        gbk
        {

            public String toString()
            {

                return "GBK";
            }
        },
        iso
        {

            public String toString()
            {

                return "ISO-8859-1";
            }
        }
    }
    
}
