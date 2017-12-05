/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2016-8-3
 */
package com.app.service.wechat;

import java.io.IOException;

import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.RequestExecutor;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;


/**
 * 功能说明：
 * 
 * @author chenwen 2016-8-3
 */

public class HttpClientPostRequestExecutor implements RequestExecutor<String, String>
{
    protected final Logger logger = Logger.getLogger(HttpClientPostRequestExecutor.class);




    public String execute(CloseableHttpClient httpclient, HttpHost httpProxy, String uri, String postEntity) throws WxErrorException, ClientProtocolException, IOException
    {
        PostMethod postMethod = new PostMethod(uri);
        RequestEntity requestEntity = new StringRequestEntity(postEntity, "text/xml", "UTF-8");
        postMethod.setRequestEntity(requestEntity);
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String responseContent = new HttpClientUtil().doHttpRequest(postMethod, "UTF-8");
        WxError error = WxError.fromJson(responseContent);
        if (error.getErrorCode() != 0)
        {
            throw new WxErrorException(error);
        }
        return responseContent;
    }

}
