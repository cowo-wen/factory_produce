/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2016-8-3
 */
package com.app.service.wechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

/**
 * 功能说明：
 * 
 * @author chenwen 2016-8-3
 */
public class HttpClientUtil
{
    protected static Logger logger = Logger.getLogger(HttpClientUtil.class);

    /**
     * 日志处理类
     */
    // private static final Log logger = LogFactory.getLog(HttpClientUtil.class);

    // 读取超时
    private final static int SOCKET_TIMEOUT = 10000;

    // 连接超时
    private final static int CONNECTION_TIMEOUT = 10000;

    // 每个HOST的最大连接数量
    private final static int MAX_CONN_PRE_HOST = 200;

    // 连接池的最大连接数
    private final static int MAX_CONN = 200;

    // 连接池
    private final static HttpConnectionManager httpConnectionManager;

    static
    {
        httpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = httpConnectionManager.getParams();
        params.setConnectionTimeout(CONNECTION_TIMEOUT);
        params.setSoTimeout(SOCKET_TIMEOUT);
        params.setDefaultMaxConnectionsPerHost(MAX_CONN_PRE_HOST);
        params.setMaxTotalConnections(MAX_CONN);

    }

    public static synchronized HttpConnectionManager getHttpConnectionManager()
    {
        return httpConnectionManager;
    }

    public HttpClientUtil()
    {
        super();
    }

    /**
     * 发送主要方法,异常捕获
     * 
     * @param post
     * @param code
     * @return
     */
    public String doHttpRequest(PostMethod post, String code) throws SocketTimeoutException, HttpException, UnknownHostException, IOException
    {
        HttpClient httpClient = null;
        synchronized (httpConnectionManager)
        {
            httpClient = new HttpClient(httpConnectionManager);
        }

        // resetRequestHeader(httpClient, "10.0.23.178");
        // 设置读取超时时间(单位毫秒)
        // httpClient.getParams().setParameter("http.socket.timeout",socket_timeout);
        // 设置连接超时时间(单位毫秒)
        // httpClient.getParams().setParameter("http.connection.timeout",connection_timeout);
        // httpClient.getParams().setParameter("http.connection-manager.timeout",100000000L);
        BufferedReader in = null;
        String resultString = "";
        try
        {
            httpClient.executeMethod(post);
            in = new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream(), code));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null)
            {
                buffer.append(line);
            }
            resultString = buffer.toString();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            post.releaseConnection();
        }
        return resultString;
    }
    
    /**
     * 发送主要方法,异常捕获
     * 
     * @param get
     * @param code
     * @return
     */
    public String doHttpRequest(GetMethod get, String code) throws SocketTimeoutException, HttpException, UnknownHostException, IOException
    {
        HttpClient httpClient = null;
        synchronized (httpConnectionManager)
        {
            httpClient = new HttpClient(httpConnectionManager);
        }
        // resetRequestHeader(httpClient, "10.0.23.178");
        // 设置读取超时时间(单位毫秒)
        // httpClient.getParams().setParameter("http.socket.timeout",socket_timeout);
        // 设置连接超时时间(单位毫秒)
        // httpClient.getParams().setParameter("http.connection.timeout",connection_timeout);
        // httpClient.getParams().setParameter("http.connection-manager.timeout",100000000L);
        //BufferedReader in = null;
        String resultString = "";
        try
        {
            int statusCode = httpClient.executeMethod(get);
            if (statusCode != HttpStatus.SC_OK) {
                logger.error("GET请求出错: "+get.getStatusLine());
            }
            
            //Header[] headers = get.getResponseHeaders();
            //for (Header h : headers)System.out.println(h.getName() + "------------ " + h.getValue());
            // 读取 HTTP 响应内容，这里简单打印网页内容
            resultString = new String(get.getResponseBody(), code);
            // 读取为 InputStream，在网页内容数据量大时候推荐使用
            // InputStream response = get.getResponseBodyAsStream();
        }
        finally
        {
            
            get.releaseConnection();
        }
        return resultString;
    }

    /**
     * 设置一下返回错误的通用提示,可以自定义格式.
     * 
     * @param reason
     * @return
     */
    public static String returnError(String reason)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        buffer.append("<Response>");
        buffer.append("<Success>false</Success>");
        buffer.append("<reason>");
        buffer.append(reason);
        buffer.append("</reason>");
        buffer.append("</Response>");
        return buffer.toString();
    }

    public final static String REQUEST_HEADER = "x-forwarded-for";

    /**
     * 将客户IP写入请求头
     * 这个设置可以伪装IP请求,注意使用
     * 
     * @param client
     * @param ip
     * @return
     */
    public static void resetRequestHeader(HttpClient client, String ip)
    {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new Header(REQUEST_HEADER, ip));
        client.getHostConfiguration().getParams().setParameter("http.default-headers", headers);
    }

}
