/**
 * 版权所有(C) 广东德生科技有限公司 2013-2020<br>
 * 创建日期 2016-8-3
 */
package com.app.service.wechat;

/**
 * 功能说明：
 * 
 * @author chenwen 2016-8-3
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.StandardSessionManager;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.common.util.RandomUtils;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.common.util.crypto.WxCryptUtil;
import me.chanjar.weixin.common.util.fs.FileUtils;
import me.chanjar.weixin.common.util.http.MediaDownloadRequestExecutor;
import me.chanjar.weixin.common.util.http.MediaUploadRequestExecutor;
import me.chanjar.weixin.common.util.http.RequestExecutor;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.common.util.http.URIUtil;
import me.chanjar.weixin.common.util.http.Utf8ResponseHandler;
import me.chanjar.weixin.common.util.json.GsonHelper;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;
import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import me.chanjar.weixin.mp.bean.WxMpGroup;
import me.chanjar.weixin.mp.bean.WxMpMassGroupMessage;
import me.chanjar.weixin.mp.bean.WxMpMassNews;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.WxMpMassVideo;
import me.chanjar.weixin.mp.bean.WxMpMaterial;
import me.chanjar.weixin.mp.bean.WxMpMaterialArticleUpdate;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.WxMpSemanticQuery;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.bean.result.WxMpMassUploadResult;
import me.chanjar.weixin.mp.bean.result.WxMpMaterialCountResult;
import me.chanjar.weixin.mp.bean.result.WxMpMaterialFileBatchGetResult;
import me.chanjar.weixin.mp.bean.result.WxMpMaterialNewsBatchGetResult;
import me.chanjar.weixin.mp.bean.result.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.result.WxMpMaterialVideoInfoResult;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpPayCallback;
import me.chanjar.weixin.mp.bean.result.WxMpPayResult;
import me.chanjar.weixin.mp.bean.result.WxMpPrepayIdResult;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpSemanticQueryResult;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserCumulate;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import me.chanjar.weixin.mp.bean.result.WxMpUserSummary;
import me.chanjar.weixin.mp.bean.result.WxRedpackResult;
import me.chanjar.weixin.mp.util.http.MaterialDeleteRequestExecutor;
import me.chanjar.weixin.mp.util.http.MaterialNewsInfoRequestExecutor;
import me.chanjar.weixin.mp.util.http.MaterialUploadRequestExecutor;
import me.chanjar.weixin.mp.util.http.MaterialVideoInfoRequestExecutor;
import me.chanjar.weixin.mp.util.http.MaterialVoiceAndImageDownloadRequestExecutor;
import me.chanjar.weixin.mp.util.http.QrCodeRequestExecutor;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.app.util.PublicMethod;
import com.app.util.RedisAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.thoughtworks.xstream.XStream;

public class WxMessageSendServiceImpl implements WxMpService
{
    protected final Logger log = LoggerFactory.getLogger(WxMessageSendServiceImpl.class);

    protected final Object globalAccessTokenRefreshLock = new Object();

    protected final Object globalJsapiTicketRefreshLock = new Object();

    protected WxMpConfigStorage wxMpConfigStorage;

    protected CloseableHttpClient httpClient;

    protected HttpHost httpProxy;

    private int retrySleepMillis = 1000;

    private int maxRetryTimes = 5;
    
    private static String system ="send_message_token";
    
    private static String code ="1001";

    protected WxSessionManager sessionManager = new StandardSessionManager();

    private final String[] REQUIRED_ORDER_PARAMETERS = {"appid", "mch_id", "body", "out_trade_no", "total_fee", "spbill_create_ip", "notify_url", "trade_type"};

    private static String get_token_url = null;
    
    
    
    public void setGet_token_url(String get_token_url)
    {
        WxMessageSendServiceImpl.get_token_url = get_token_url;
    }

    /**
     * 验证服务接口是否正确
     */
    public boolean checkSignature(String timestamp, String nonce, String signature)
    {
        try
        {
            return SHA1.gen(new String[]{this.wxMpConfigStorage.getToken(), timestamp, nonce}).equals(signature);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取微信验证令牌
     */
    public String getAccessToken() throws WxErrorException
    {
        return getAccessToken(false);
    }

    /**
     * 获取令牌 是否手动更新令牌
     */
    public String getAccessToken(boolean forceRefresh) throws WxErrorException
    {
        RedisAPI redisAPI = new RedisAPI(RedisAPI.REDIS_CORE_DATABASE);
        if (forceRefresh)
        {
            String bool = redisAPI.get("wx:send_message_token:update_token");
            if(!PublicMethod.isEmptyStr(bool) && bool.equals("true")){
                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }else{
                redisAPI.put("wx:send_message_token:update_token","true");
                redisAPI.del("wx:send_message_token:token");
            }
            // this.wxMpConfigStorage.expireAccessToken();
            
        }
        String token = redisAPI.get("wx:send_message_token:token");
        if ( token == null)
        {
            synchronized (this.globalAccessTokenRefreshLock)
            {
                String url = new StringBuilder().append("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=").append(this.wxMpConfigStorage.getAppId()).append("&secret=").append(this.wxMpConfigStorage.getSecret()).toString();
                try
                {
                    HttpGet httpGet = new HttpGet(url);
                    if (this.httpProxy != null)
                    {
                        RequestConfig config = RequestConfig.custom().setProxy(this.httpProxy).build();
                        httpGet.setConfig(config);
                    }
                    CloseableHttpClient httpClient = getHttpclient();
                    CloseableHttpResponse response = httpClient.execute(httpGet);
                    String resultContent = new BasicResponseHandler().handleResponse(response);
                    WxError error = WxError.fromJson(resultContent);
                    if (error.getErrorCode() != 0)
                    {
                        throw new WxErrorException(error);
                    }
                    WxAccessToken accessToken = WxAccessToken.fromJson(resultContent);
                    token = accessToken.getAccessToken();
                    this.wxMpConfigStorage.updateAccessToken(token, accessToken.getExpiresIn());
                    redisAPI.put("wx:send_message_token:token", token, accessToken.getExpiresIn());
                }
                catch (ClientProtocolException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }finally{
                    redisAPI.put("wx:send_message_token:update_token","false");
                }
                
            }
        }
        return token;
    }

    public String getJsapiTicket() throws WxErrorException
    {
        return getJsapiTicket(false);
    }

    public String getJsapiTicket(boolean forceRefresh) throws WxErrorException
    {
        if (forceRefresh)
        {
            this.wxMpConfigStorage.expireJsapiTicket();
        }
        if (this.wxMpConfigStorage.isJsapiTicketExpired())
        {
            synchronized (this.globalJsapiTicketRefreshLock)
            {
                if (this.wxMpConfigStorage.isJsapiTicketExpired())
                {
                    String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi";
                    String responseContent = (String) execute(new SimpleGetRequestExecutor(), url, null);
                    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
                    JsonObject tmpJsonObject = tmpJsonElement.getAsJsonObject();
                    String jsapiTicket = tmpJsonObject.get("ticket").getAsString();
                    int expiresInSeconds = tmpJsonObject.get("expires_in").getAsInt();
                    this.wxMpConfigStorage.updateJsapiTicket(jsapiTicket, expiresInSeconds);
                }
            }
        }
        return this.wxMpConfigStorage.getJsapiTicket();
    }

    public WxJsapiSignature createJsapiSignature(String url) throws WxErrorException
    {
        long timestamp = System.currentTimeMillis() / 1000L;
        String noncestr = RandomUtils.getRandomStr();
        String jsapiTicket = getJsapiTicket(false);
        try
        {
            String signature = SHA1.genWithAmple(new String[]{new StringBuilder().append("jsapi_ticket=").append(jsapiTicket).toString(), new StringBuilder().append("noncestr=").append(noncestr).toString(), new StringBuilder().append("timestamp=").append(timestamp).toString(),
                    new StringBuilder().append("url=").append(url).toString()});

            WxJsapiSignature jsapiSignature = new WxJsapiSignature();
            jsapiSignature.setAppid(this.wxMpConfigStorage.getAppId());
            jsapiSignature.setTimestamp(timestamp);
            jsapiSignature.setNoncestr(noncestr);
            jsapiSignature.setUrl(url);
            jsapiSignature.setSignature(signature);
            return jsapiSignature;
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }

    }

    public void customMessageSend(WxMpCustomMessage message) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send";
        execute(new SimplePostRequestExecutor(), url, message.toJson());
    }

    public void menuCreate(WxMenu menu) throws WxErrorException
    {
        if (menu.getMatchRule() != null)
        {
            String url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional";
            execute(new SimplePostRequestExecutor(), url, menu.toJson());
        }
        else
        {
            String url = "https://api.weixin.qq.com/cgi-bin/menu/create";
            execute(new SimplePostRequestExecutor(), url, menu.toJson());
        }
    }

    public void menuDelete() throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/delete";
        execute(new SimpleGetRequestExecutor(), url, null);
    }

    public void menuDelete(String menuid) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/delconditional";
        execute(new SimpleGetRequestExecutor(), url, new StringBuilder().append("menuid=").append(menuid).toString());
    }

    public WxMenu menuGet() throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/get";
        try
        {
            String resultContent = (String) execute(new SimpleGetRequestExecutor(), url, null);
            return WxMenu.fromJson(resultContent);
        }
        catch (WxErrorException e)
        {
            if (e.getError().getErrorCode() == 46003)
                return null;

            throw e;
        }
    }

    public WxMenu menuTryMatch(String userid) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/trymatch";
        try
        {
            String resultContent = (String) execute(new SimpleGetRequestExecutor(), url, new StringBuilder().append("user_id=").append(userid).toString());
            return WxMenu.fromJson(resultContent);
        }
        catch (WxErrorException e)
        {
            if ((e.getError().getErrorCode() == 46003) || (e.getError().getErrorCode() == 46002))
                return null;
            throw e;
        }

    }

    public WxMediaUploadResult mediaUpload(String mediaType, String fileType, InputStream inputStream) throws WxErrorException, IOException
    {
        return mediaUpload(mediaType, FileUtils.createTmpFile(inputStream, UUID.randomUUID().toString(), fileType));
    }

    public WxMediaUploadResult mediaUpload(String mediaType, File file) throws WxErrorException
    {
        String url = new StringBuilder().append("http://file.api.weixin.qq.com/cgi-bin/media/upload?type=").append(mediaType).toString();
        return (WxMediaUploadResult) execute(new MediaUploadRequestExecutor(), url, file);
    }

    public File mediaDownload(String media_id) throws WxErrorException
    {
        String url = "http://file.api.weixin.qq.com/cgi-bin/media/get";
        return (File) execute(new MediaDownloadRequestExecutor(this.wxMpConfigStorage.getTmpDirFile()), url, new StringBuilder().append("media_id=").append(media_id).toString());
    }

    public WxMpMaterialUploadResult materialFileUpload(String mediaType, WxMpMaterial material) throws WxErrorException
    {
        String url = new StringBuilder().append("https://api.weixin.qq.com/cgi-bin/material/add_material?type=").append(mediaType).toString();
        return (WxMpMaterialUploadResult) execute(new MaterialUploadRequestExecutor(), url, material);
    }

    public WxMpMaterialUploadResult materialNewsUpload(WxMpMaterialNews news) throws WxErrorException
    {
        if ((news == null) || (news.isEmpty()))
        {
            throw new IllegalArgumentException("news is empty!");
        }
        String url = "https://api.weixin.qq.com/cgi-bin/material/add_news";
        String responseContent = post(url, news.toJson());
        return WxMpMaterialUploadResult.fromJson(responseContent);
    }

    public InputStream materialImageOrVoiceDownload(String media_id) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_material";
        return (InputStream) execute(new MaterialVoiceAndImageDownloadRequestExecutor(this.wxMpConfigStorage.getTmpDirFile()), url, media_id);
    }

    public WxMpMaterialVideoInfoResult materialVideoInfo(String media_id) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_material";
        return (WxMpMaterialVideoInfoResult) execute(new MaterialVideoInfoRequestExecutor(), url, media_id);
    }

    public WxMpMaterialNews materialNewsInfo(String media_id) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_material";
        return (WxMpMaterialNews) execute(new MaterialNewsInfoRequestExecutor(), url, media_id);
    }

    public boolean materialNewsUpdate(WxMpMaterialArticleUpdate wxMpMaterialArticleUpdate) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/update_news";
        String responseText = post(url, wxMpMaterialArticleUpdate.toJson());
        WxError wxError = WxError.fromJson(responseText);
        if (wxError.getErrorCode() == 0)
        {
            return true;
        }
        throw new WxErrorException(wxError);
    }

    public boolean materialDelete(String media_id) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/del_material";
        return ((Boolean) execute(new MaterialDeleteRequestExecutor(), url, media_id)).booleanValue();
    }

    public WxMpMaterialCountResult materialCount() throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount";
        String responseText = get(url, null);
        WxError wxError = WxError.fromJson(responseText);
        if (wxError.getErrorCode() == 0)
        {
            return (WxMpMaterialCountResult) WxMpGsonBuilder.create().fromJson(responseText, WxMpMaterialCountResult.class);
        }
        throw new WxErrorException(wxError);
    }

    public WxMpMaterialNewsBatchGetResult materialNewsBatchGet(int offset, int count) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", "news");
        params.put("offset", Integer.valueOf(offset));
        params.put("count", Integer.valueOf(count));
        String responseText = post(url, WxGsonBuilder.create().toJson(params));
        WxError wxError = WxError.fromJson(responseText);
        if (wxError.getErrorCode() == 0)
        {
            return (WxMpMaterialNewsBatchGetResult) WxMpGsonBuilder.create().fromJson(responseText, WxMpMaterialNewsBatchGetResult.class);
        }
        throw new WxErrorException(wxError);
    }

    public WxMpMaterialFileBatchGetResult materialFileBatchGet(String type, int offset, int count) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", type);
        params.put("offset", Integer.valueOf(offset));
        params.put("count", Integer.valueOf(count));
        String responseText = post(url, WxGsonBuilder.create().toJson(params));
        WxError wxError = WxError.fromJson(responseText);
        if (wxError.getErrorCode() == 0)
        {
            return (WxMpMaterialFileBatchGetResult) WxMpGsonBuilder.create().fromJson(responseText, WxMpMaterialFileBatchGetResult.class);
        }
        throw new WxErrorException(wxError);
    }

    public WxMpMassUploadResult massNewsUpload(WxMpMassNews news) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/media/uploadnews";
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, news.toJson());
        return WxMpMassUploadResult.fromJson(responseContent);
    }

    public WxMpMassUploadResult massVideoUpload(WxMpMassVideo video) throws WxErrorException
    {
        String url = "http://file.api.weixin.qq.com/cgi-bin/media/uploadvideo";
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, video.toJson());
        return WxMpMassUploadResult.fromJson(responseContent);
    }

    public WxMpMassSendResult massGroupMessageSend(WxMpMassGroupMessage message) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall";
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, message.toJson());
        return WxMpMassSendResult.fromJson(responseContent);
    }

    public WxMpMassSendResult massOpenIdsMessageSend(WxMpMassOpenIdsMessage message) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/send";
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, message.toJson());
        return WxMpMassSendResult.fromJson(responseContent);
    }

    public WxMpGroup groupCreate(String name) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/groups/create";
        JsonObject json = new JsonObject();
        JsonObject groupJson = new JsonObject();
        json.add("group", groupJson);
        groupJson.addProperty("name", name);

        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, json.toString());

        return WxMpGroup.fromJson(responseContent);
    }

    public List<WxMpGroup> groupGet() throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/groups/get";
        String responseContent = (String) execute(new SimpleGetRequestExecutor(), url, null);

        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));

        List<WxMpGroup> list = WxMpGsonBuilder.INSTANCE.create().fromJson(tmpJsonElement.getAsJsonObject().get("groups"), new TypeToken<List<WxMpGroup>>()
        {
        }.getType());

        return list;
    }

    public long userGetGroup(String openid) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/groups/getid";
        JsonObject o = new JsonObject();
        o.addProperty("openid", openid);
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, o.toString());
        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
        return GsonHelper.getAsLong(tmpJsonElement.getAsJsonObject().get("groupid")).longValue();
    }

    public void groupUpdate(WxMpGroup group) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/groups/update";
        execute(new SimplePostRequestExecutor(), url, group.toJson());
    }

    public void userUpdateGroup(String openid, long to_groupid) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/groups/members/update";
        JsonObject json = new JsonObject();
        json.addProperty("openid", openid);
        json.addProperty("to_groupid", Long.valueOf(to_groupid));
        execute(new SimplePostRequestExecutor(), url, json.toString());
    }

    public void userUpdateRemark(String openid, String remark) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark";
        JsonObject json = new JsonObject();
        json.addProperty("openid", openid);
        json.addProperty("remark", remark);
        execute(new SimplePostRequestExecutor(), url, json.toString());
    }

    public WxMpUser userInfo(String openid, String lang) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/user/info";
        lang = lang == null ? "zh_CN" : lang;
        String responseContent = (String) execute(new SimpleGetRequestExecutor(), url, new StringBuilder().append("openid=").append(openid).append("&lang=").append(lang).toString());
        return WxMpUser.fromJson(responseContent);
    }

    public WxMpUserList userList(String next_openid) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/user/get";
        String responseContent = (String) execute(new SimpleGetRequestExecutor(), url, next_openid == null ? null : new StringBuilder().append("next_openid=").append(next_openid).toString());
        return WxMpUserList.fromJson(responseContent);
    }

    public WxMpQrCodeTicket qrCodeCreateTmpTicket(int scene_id, Integer expire_seconds) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
        JsonObject json = new JsonObject();
        json.addProperty("action_name", "QR_SCENE");
        if (expire_seconds != null)
        {
            json.addProperty("expire_seconds", expire_seconds);
        }
        JsonObject actionInfo = new JsonObject();
        JsonObject scene = new JsonObject();
        scene.addProperty("scene_id", Integer.valueOf(scene_id));
        actionInfo.add("scene", scene);
        json.add("action_info", actionInfo);
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, json.toString());
        return WxMpQrCodeTicket.fromJson(responseContent);
    }

    public WxMpQrCodeTicket qrCodeCreateLastTicket(int scene_id) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
        JsonObject json = new JsonObject();
        json.addProperty("action_name", "QR_LIMIT_SCENE");
        JsonObject actionInfo = new JsonObject();
        JsonObject scene = new JsonObject();
        scene.addProperty("scene_id", Integer.valueOf(scene_id));
        actionInfo.add("scene", scene);
        json.add("action_info", actionInfo);
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, json.toString());
        return WxMpQrCodeTicket.fromJson(responseContent);
    }

    public WxMpQrCodeTicket qrCodeCreateLastTicket(String scene_str) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
        JsonObject json = new JsonObject();
        json.addProperty("action_name", "QR_LIMIT_STR_SCENE");
        JsonObject actionInfo = new JsonObject();
        JsonObject scene = new JsonObject();
        scene.addProperty("scene_str", scene_str);
        actionInfo.add("scene", scene);
        json.add("action_info", actionInfo);
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, json.toString());
        return WxMpQrCodeTicket.fromJson(responseContent);
    }

    public File qrCodePicture(WxMpQrCodeTicket ticket) throws WxErrorException
    {
        String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode";
        return (File) execute(new QrCodeRequestExecutor(), url, ticket);
    }

    public String shortUrl(String long_url) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/shorturl";
        JsonObject o = new JsonObject();
        o.addProperty("action", "long2short");
        o.addProperty("long_url", long_url);
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, o.toString());
        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
        return tmpJsonElement.getAsJsonObject().get("short_url").getAsString();
    }
   

    public String templateSend(WxMpTemplateMessage templateMessage) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send";
        String responseContent = (String) execute(new HttpClientPostRequestExecutor(), url, templateMessage.toJson());
        JsonElement tmpJsonElement = new JsonParser().parse(responseContent);
        JsonObject jsonObject = tmpJsonElement.getAsJsonObject();
        if (jsonObject.get("errcode").getAsInt() == 0)
            return jsonObject.get("msgid").getAsString();
        throw new WxErrorException(WxError.fromJson(responseContent));
    }

    public WxMpSemanticQueryResult semanticQuery(WxMpSemanticQuery semanticQuery) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/semantic/semproxy/search";
        String responseContent = (String) execute(new SimplePostRequestExecutor(), url, semanticQuery.toJson());
        return WxMpSemanticQueryResult.fromJson(responseContent);
    }

    public String oauth2buildAuthorizationUrl(String scope, String state)
    {
        return oauth2buildAuthorizationUrl(this.wxMpConfigStorage.getOauth2redirectUri(), scope, state);
    }

    public String oauth2buildAuthorizationUrl(String redirectURI, String scope, String state)
    {
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?";
        url = new StringBuilder().append(url).append("appid=").append(this.wxMpConfigStorage.getAppId()).toString();
        url = new StringBuilder().append(url).append("&redirect_uri=").append(URIUtil.encodeURIComponent(redirectURI)).toString();
        url = new StringBuilder().append(url).append("&response_type=code").toString();
        url = new StringBuilder().append(url).append("&scope=").append(scope).toString();
        if (state != null)
        {
            url = new StringBuilder().append(url).append("&state=").append(state).toString();
        }
        url = new StringBuilder().append(url).append("#wechat_redirect").toString();
        return url;
    }

    public WxMpOAuth2AccessToken oauth2getAccessToken(String code) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?";
        url = new StringBuilder().append(url).append("appid=").append(this.wxMpConfigStorage.getAppId()).toString();
        url = new StringBuilder().append(url).append("&secret=").append(this.wxMpConfigStorage.getSecret()).toString();
        url = new StringBuilder().append(url).append("&code=").append(code).toString();
        url = new StringBuilder().append(url).append("&grant_type=authorization_code").toString();
        try
        {
            RequestExecutor<String, String> executor = new SimpleGetRequestExecutor();
            String responseText = (String) executor.execute(getHttpclient(), this.httpProxy, url, null);
            return WxMpOAuth2AccessToken.fromJson(responseText);
        }
        catch (ClientProtocolException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    public WxMpOAuth2AccessToken oauth2refreshAccessToken(String refreshToken) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?";
        url = new StringBuilder().append(url).append("appid=").append(this.wxMpConfigStorage.getAppId()).toString();
        url = new StringBuilder().append(url).append("&grant_type=refresh_token").toString();
        url = new StringBuilder().append(url).append("&refresh_token=").append(refreshToken).toString();
        try
        {
            RequestExecutor<String, String> executor = new SimpleGetRequestExecutor();
            String responseText = (String) executor.execute(getHttpclient(), this.httpProxy, url, null);
            return WxMpOAuth2AccessToken.fromJson(responseText);
        }
        catch (ClientProtocolException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    public WxMpUser oauth2getUserInfo(WxMpOAuth2AccessToken oAuth2AccessToken, String lang) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/sns/userinfo?";
        url = new StringBuilder().append(url).append("access_token=").append(oAuth2AccessToken.getAccessToken()).toString();
        url = new StringBuilder().append(url).append("&openid=").append(oAuth2AccessToken.getOpenId()).toString();
        if (lang == null)
            url = new StringBuilder().append(url).append("&lang=zh_CN").toString();
        else
        {
            url = new StringBuilder().append(url).append("&lang=").append(lang).toString();
        }
        try
        {
            RequestExecutor<String, String> executor = new SimpleGetRequestExecutor();
            String responseText = (String) executor.execute(getHttpclient(), this.httpProxy, url, null);
            return WxMpUser.fromJson(responseText);
        }
        catch (ClientProtocolException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    public boolean oauth2validateAccessToken(WxMpOAuth2AccessToken oAuth2AccessToken)
    {
        String url = "https://api.weixin.qq.com/sns/auth?";
        url = new StringBuilder().append(url).append("access_token=").append(oAuth2AccessToken.getAccessToken()).toString();
        url = new StringBuilder().append(url).append("&openid=").append(oAuth2AccessToken.getOpenId()).toString();
        try
        {
            RequestExecutor<String, String> executor = new SimpleGetRequestExecutor();
            executor.execute(getHttpclient(), this.httpProxy, url, null);
        }
        catch (ClientProtocolException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (WxErrorException e)
        {
            return false;
        }
        return true;
    }

    public String[] getCallbackIP() throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/cgi-bin/getcallbackip";
        String responseContent = get(url, null);
        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
        JsonArray ipList = tmpJsonElement.getAsJsonObject().get("ip_list").getAsJsonArray();
        String[] ipArray = new String[ipList.size()];
        for (int i = 0; i < ipList.size(); i++)
        {
            ipArray[i] = ipList.get(i).getAsString();
        }
        return ipArray;
    }

    public List<WxMpUserSummary> getUserSummary(Date beginDate, Date endDate) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/datacube/getusersummary";
        JsonObject param = new JsonObject();
        param.addProperty("begin_date", SIMPLE_DATE_FORMAT.format(beginDate));
        param.addProperty("end_date", SIMPLE_DATE_FORMAT.format(endDate));
        String responseContent = post(url, param.toString());
        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
        List<WxMpUserSummary> list = WxMpGsonBuilder.INSTANCE.create().fromJson(tmpJsonElement.getAsJsonObject().get("list"), new TypeToken<List<WxMpUserSummary>>()
        {
        }.getType());
        return list;
    }

    public List<WxMpUserCumulate> getUserCumulate(Date beginDate, Date endDate) throws WxErrorException
    {
        String url = "https://api.weixin.qq.com/datacube/getusercumulate";
        JsonObject param = new JsonObject();
        param.addProperty("begin_date", SIMPLE_DATE_FORMAT.format(beginDate));
        param.addProperty("end_date", SIMPLE_DATE_FORMAT.format(endDate));
        String responseContent = post(url, param.toString());
        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
        List<WxMpUserCumulate> list = WxMpGsonBuilder.INSTANCE.create().fromJson(tmpJsonElement.getAsJsonObject().get("list"), new TypeToken<List<WxMpUserCumulate>>()
        {
        }.getType());
        return list;
    }

    public String get(String url, String queryParam) throws WxErrorException
    {
        return (String) execute(new SimpleGetRequestExecutor(), url, queryParam);
    }

    public String post(String url, String postData) throws WxErrorException
    {
        return (String) execute(new SimplePostRequestExecutor(), url, postData);
    }

    public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException
    {
        int retryTimes = 0;
        do
        {
            try
            {
                return executeInternal(executor, uri, data);
            }
            catch (WxErrorException e)
            {
                WxError error = e.getError();

                if (error.getErrorCode() == -1)
                {
                    int sleepMillis = this.retrySleepMillis * (1 << retryTimes);
                    try
                    {
                        this.log.error("微信系统繁忙，{}ms 后重试(第{}次)", Integer.valueOf(sleepMillis), Integer.valueOf(retryTimes + 1));
                        Thread.sleep(sleepMillis);
                    }
                    catch (InterruptedException e1)
                    {
                        throw new RuntimeException(e1);
                    }
                }
                else
                {
                    throw e;
                }
            }
            retryTimes++;
        } while (retryTimes < this.maxRetryTimes);

        throw new RuntimeException("微信服务端异常，超出重试次数");
    }

    protected synchronized <T, E> T executeInternal(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException
    {
        if (uri.indexOf("access_token=") != -1)
        {
            throw new IllegalArgumentException(new StringBuilder().append("uri参数中不允许有access_token: ").append(uri).toString());
        }
        String accessToken = getAccessToken(false);

        String uriWithAccessToken = uri;
        uriWithAccessToken = new StringBuilder().append(uriWithAccessToken).append(uri.indexOf(63) == -1 ? new StringBuilder().append("?access_token=").append(accessToken).toString() : new StringBuilder().append("&access_token=").append(accessToken).toString()).toString();
        try
        {
            return executor.execute(getHttpclient(), this.httpProxy, uriWithAccessToken, data);
        }
        catch (WxErrorException e)
        {
            WxError error = e.getError();

            if ((error.getErrorCode() == 42001) || (error.getErrorCode() == 40001))
            {
                if(get_token_url == null || get_token_url.trim().length() == 0){
                    new RedisAPI(RedisAPI.REDIS_CORE_DATABASE).del("wx:send_message_token:token");
                }else{
                    Form form = new Form();
                    form.add("system", system);
                    form.add("code", code);
                    Client client = new Client();
                    WebResource webResource = client.resource(get_token_url);
                    ClientResponse response = (ClientResponse)webResource.type(MediaType.APPLICATION_FORM_URLENCODED).put(ClientResponse.class, form);
                    client.destroy();
                    log.error("---------------获取公众号微信令牌:"+response.getEntity(String.class));
                    
                }
                
                return execute(executor, uri, data);
            }
            if (error.getErrorCode() != 0)
            {
                throw new WxErrorException(error);
            }
            return null;
        }
        catch (ClientProtocolException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    protected CloseableHttpClient getHttpclient()
    {
        return this.httpClient;
    }

    public void setWxMpConfigStorage(WxMpConfigStorage wxConfigProvider)
    {
        this.wxMpConfigStorage = wxConfigProvider;

        String http_proxy_host = this.wxMpConfigStorage.getHttp_proxy_host();
        int http_proxy_port = this.wxMpConfigStorage.getHttp_proxy_port();
        String http_proxy_username = this.wxMpConfigStorage.getHttp_proxy_username();
        String http_proxy_password = this.wxMpConfigStorage.getHttp_proxy_password();

        HttpClientBuilder builder = HttpClients.custom();
        if (StringUtils.isNotBlank(http_proxy_host))
        {
            if (StringUtils.isNotBlank(http_proxy_username))
            {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new AuthScope(http_proxy_host, http_proxy_port), new UsernamePasswordCredentials(http_proxy_username, http_proxy_password));

                builder.setDefaultCredentialsProvider(credsProvider);
            }

            this.httpProxy = new HttpHost(http_proxy_host, http_proxy_port);
        }
        if (wxConfigProvider.getSSLContext() != null)
        {
            @SuppressWarnings("deprecation")
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(wxConfigProvider.getSSLContext(), new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

            builder.setSSLSocketFactory(sslsf);
        }
        this.httpClient = builder.build();
    }

    public void setRetrySleepMillis(int retrySleepMillis)
    {
        this.retrySleepMillis = retrySleepMillis;
    }

    public void setMaxRetryTimes(int maxRetryTimes)
    {
        this.maxRetryTimes = maxRetryTimes;
    }

    public WxMpPrepayIdResult getPrepayId(String openId, String outTradeNo, double amt, String body, String tradeType, String ip, String callbackUrl)
    {
        Map<String, String> packageParams = new HashMap<String, String>();
        packageParams.put("appid", this.wxMpConfigStorage.getAppId());
        packageParams.put("mch_id", this.wxMpConfigStorage.getPartnerId());
        packageParams.put("body", body);
        packageParams.put("out_trade_no", outTradeNo);
        packageParams.put("total_fee", new StringBuilder().append((int) (amt * 100.0D)).append("").toString());
        packageParams.put("spbill_create_ip", ip);
        packageParams.put("notify_url", callbackUrl);
        packageParams.put("trade_type", tradeType);
        packageParams.put("openid", openId);

        return getPrepayId(packageParams);
    }

    public WxMpPrepayIdResult getPrepayId(Map<String, String> parameters)
    {
        String nonce_str = new StringBuilder().append(System.currentTimeMillis()).append("").toString();

        SortedMap<String, String> packageParams = new TreeMap<String, String>(parameters);
        packageParams.put("appid", this.wxMpConfigStorage.getAppId());
        packageParams.put("mch_id", this.wxMpConfigStorage.getPartnerId());
        packageParams.put("nonce_str", nonce_str);
        checkParameters(packageParams);

        String sign = WxCryptUtil.createSign(packageParams, this.wxMpConfigStorage.getPartnerKey());
        packageParams.put("sign", sign);

        StringBuilder request = new StringBuilder("<xml>");
        for (Map.Entry<String, String> para : packageParams.entrySet())
        {
            request.append(String.format("<%s>%s</%s>", new Object[]{para.getKey(), para.getValue(), para.getKey()}));
        }
        request.append("</xml>");

        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder");
        if (this.httpProxy != null)
        {
            RequestConfig config = RequestConfig.custom().setProxy(this.httpProxy).build();
            httpPost.setConfig(config);
        }

        StringEntity entity = new StringEntity(request.toString(), Consts.UTF_8);
        httpPost.setEntity(entity);
        try
        {
            CloseableHttpResponse response = getHttpclient().execute(httpPost);
            String responseContent = (String) Utf8ResponseHandler.INSTANCE.handleResponse(response);
            XStream xstream = XStreamInitializer.getInstance();
            xstream.alias("xml", WxMpPrepayIdResult.class);
            WxMpPrepayIdResult wxMpPrepayIdResult = (WxMpPrepayIdResult) xstream.fromXML(responseContent);
            return wxMpPrepayIdResult;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to get prepay id due to IO exception.", e);
        }

    }

    private void checkParameters(Map<String, String> parameters)
    {
        for (String para : this.REQUIRED_ORDER_PARAMETERS)
        {
            if (!parameters.containsKey(para))
                throw new IllegalArgumentException(new StringBuilder().append("Reqiured argument '").append(para).append("' is missing.").toString());
        }
        if (("JSAPI".equals(parameters.get("trade_type"))) && (!parameters.containsKey("openid")))
            throw new IllegalArgumentException("Reqiured argument 'openid' is missing when trade_type is 'JSAPI'.");
        if (("NATIVE".equals(parameters.get("trade_type"))) && (!parameters.containsKey("product_id")))
            throw new IllegalArgumentException("Reqiured argument 'product_id' is missing when trade_type is 'NATIVE'.");
    }

    public Map<String, String> getJSSDKPayInfo(String openId, String outTradeNo, double amt, String body, String tradeType, String ip, String callbackUrl)
    {
        Map<String, String> packageParams = new HashMap<String, String>();
        packageParams.put("appid", this.wxMpConfigStorage.getAppId());
        packageParams.put("mch_id", this.wxMpConfigStorage.getPartnerId());
        packageParams.put("body", body);
        packageParams.put("out_trade_no", outTradeNo);
        packageParams.put("total_fee", new StringBuilder().append((int) (amt * 100.0D)).append("").toString());
        packageParams.put("spbill_create_ip", ip);
        packageParams.put("notify_url", callbackUrl);
        packageParams.put("trade_type", tradeType);
        packageParams.put("openid", openId);

        return getJSSDKPayInfo(packageParams);
    }

    public Map<String, String> getJSSDKPayInfo(Map<String, String> parameters)
    {
        WxMpPrepayIdResult wxMpPrepayIdResult = getPrepayId(parameters);
        String prepayId = wxMpPrepayIdResult.getPrepay_id();
        if ((prepayId == null) || (prepayId.equals("")))
        {
            throw new RuntimeException(String.format("Failed to get prepay id due to error code '%s'(%s).", new Object[]{wxMpPrepayIdResult.getErr_code(), wxMpPrepayIdResult.getErr_code_des()}));
        }

        Map<String, String> payInfo = new HashMap<String, String>();
        payInfo.put("appId", this.wxMpConfigStorage.getAppId());

        payInfo.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000L));
        payInfo.put("nonceStr", new StringBuilder().append(System.currentTimeMillis()).append("").toString());
        payInfo.put("package", new StringBuilder().append("prepay_id=").append(prepayId).toString());
        payInfo.put("signType", "MD5");

        String finalSign = WxCryptUtil.createSign(payInfo, this.wxMpConfigStorage.getPartnerKey());
        payInfo.put("paySign", finalSign);
        return payInfo;
    }

    public WxMpPayResult getJSSDKPayResult(String transactionId, String outTradeNo)
    {
        String nonce_str = new StringBuilder().append(System.currentTimeMillis()).append("").toString();

        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("appid", this.wxMpConfigStorage.getAppId());
        packageParams.put("mch_id", this.wxMpConfigStorage.getPartnerId());
        if ((transactionId != null) && (!"".equals(transactionId.trim())))
            packageParams.put("transaction_id", transactionId);
        else if ((outTradeNo != null) && (!"".equals(outTradeNo.trim())))
            packageParams.put("out_trade_no", outTradeNo);
        else
            throw new IllegalArgumentException("Either 'transactionId' or 'outTradeNo' must be given.");
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("sign", WxCryptUtil.createSign(packageParams, this.wxMpConfigStorage.getPartnerKey()));

        StringBuilder request = new StringBuilder("<xml>");
        for (Map.Entry<String, String> para : packageParams.entrySet())
        {
            request.append(String.format("<%s>%s</%s>", new Object[]{para.getKey(), para.getValue(), para.getKey()}));
        }
        request.append("</xml>");

        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/pay/orderquery");
        if (this.httpProxy != null)
        {
            RequestConfig config = RequestConfig.custom().setProxy(this.httpProxy).build();
            httpPost.setConfig(config);
        }

        StringEntity entity = new StringEntity(request.toString(), Consts.UTF_8);
        httpPost.setEntity(entity);
        try
        {
            CloseableHttpResponse response = this.httpClient.execute(httpPost);
            String responseContent = (String) Utf8ResponseHandler.INSTANCE.handleResponse(response);
            XStream xstream = XStreamInitializer.getInstance();
            xstream.alias("xml", WxMpPayResult.class);
            WxMpPayResult wxMpPayResult = (WxMpPayResult) xstream.fromXML(responseContent);
            return wxMpPayResult;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to query order due to IO exception.", e);
        }

    }

    public WxMpPayCallback getJSSDKCallbackData(String xmlData)
    {
        try
        {
            XStream xstream = XStreamInitializer.getInstance();
            xstream.alias("xml", WxMpPayCallback.class);
            WxMpPayCallback wxMpCallback = (WxMpPayCallback) xstream.fromXML(xmlData);
            return wxMpCallback;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new WxMpPayCallback();
    }

    public boolean checkJSSDKCallbackDataSignature(Map<String, String> kvm, String signature)
    {
        return signature.equals(WxCryptUtil.createSign(kvm, this.wxMpConfigStorage.getPartnerKey()));
    }

    public WxRedpackResult sendRedpack(Map<String, String> parameters) throws WxErrorException
    {
        String nonce_str = new StringBuilder().append(System.currentTimeMillis()).append("").toString();

        SortedMap<String, String> packageParams = new TreeMap<String, String>(parameters);
        packageParams.put("wxappid", this.wxMpConfigStorage.getAppId());
        packageParams.put("mch_id", this.wxMpConfigStorage.getPartnerId());
        packageParams.put("nonce_str", nonce_str);

        String sign = WxCryptUtil.createSign(packageParams, this.wxMpConfigStorage.getPartnerKey());
        packageParams.put("sign", sign);

        StringBuilder request = new StringBuilder("<xml>");
        for (Map.Entry<String, String> para : packageParams.entrySet())
        {
            request.append(String.format("<%s>%s</%s>", new Object[]{para.getKey(), para.getValue(), para.getKey()}));
        }
        request.append("</xml>");

        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack");
        if (this.httpProxy != null)
        {
            RequestConfig config = RequestConfig.custom().setProxy(this.httpProxy).build();
            httpPost.setConfig(config);
        }
        StringEntity entity = new StringEntity(request.toString(), Consts.UTF_8);
        httpPost.setEntity(entity);
        WxError error;
        try
        {
            CloseableHttpResponse response = getHttpclient().execute(httpPost);
            String responseContent = (String) Utf8ResponseHandler.INSTANCE.handleResponse(response);
            XStream xstream = XStreamInitializer.getInstance();
            xstream.processAnnotations(WxRedpackResult.class);
            WxRedpackResult wxMpRedpackResult = (WxRedpackResult) xstream.fromXML(responseContent);
            return wxMpRedpackResult;
        }
        catch (IOException e)
        {
            this.log.error(MessageFormatter.format("The exception was happened when sending redpack '{}'.", request.toString()).getMessage(), e);
            error = new WxError();
            error.setErrorCode(-1);
        }
        throw new WxErrorException(error);
    }
}
