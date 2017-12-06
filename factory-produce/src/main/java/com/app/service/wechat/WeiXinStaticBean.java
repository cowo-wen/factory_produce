package com.app.service.wechat;

public class WeiXinStaticBean
{
   

    public static final String _T = "\t";

    public static final String _N = "\n";

    public static final String _R = "\r";

    public static final String NEWLINE = "\r\n";

    /**
     * 2016-04-24
     * 
     * @author 微信回调的消息类型
     */
    public static enum MSG_TYPE
    {
        event
        {

            public String toString()
            {

                return "event";
            }
        },
        text
        {

            public String toString()
            {

                return "text";
            }
        };
    }
    
    /**
     * 2016-04-24
     * 
     * @author 微信回调的消息类型
     */
    public static enum EVENT
    {
        click//按钮点击
        {

            public String toString()
            {

                return "CLICK";
            }
        },subscribe//关注
        {

            public String toString()
            {

                return "subscribe";
            }
        },unsubscribe//取消关注
        {

            public String toString()
            {

                return "unsubscribe";
            }
        },
        view//跳转链接
        {

            public String toString()
            {

                return "VIEW";
            }
        };
    }

    
    
}
