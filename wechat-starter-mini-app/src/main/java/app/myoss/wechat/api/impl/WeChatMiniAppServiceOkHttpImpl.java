/*
 * Copyright 2018-2019 https://github.com/myoss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package app.myoss.wechat.api.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.binarywang.wx.miniapp.api.WxMaAnalysisService;
import cn.binarywang.wx.miniapp.api.WxMaCloudService;
import cn.binarywang.wx.miniapp.api.WxMaCodeService;
import cn.binarywang.wx.miniapp.api.WxMaExpressService;
import cn.binarywang.wx.miniapp.api.WxMaJsapiService;
import cn.binarywang.wx.miniapp.api.WxMaLiveGoodsService;
import cn.binarywang.wx.miniapp.api.WxMaLiveMemberService;
import cn.binarywang.wx.miniapp.api.WxMaLiveService;
import cn.binarywang.wx.miniapp.api.WxMaMediaService;
import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaPluginService;
import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaRunService;
import cn.binarywang.wx.miniapp.api.WxMaSchemeService;
import cn.binarywang.wx.miniapp.api.WxMaSecCheckService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaSettingService;
import cn.binarywang.wx.miniapp.api.WxMaShareService;
import cn.binarywang.wx.miniapp.api.WxMaShopOrderService;
import cn.binarywang.wx.miniapp.api.WxMaShopSpuService;
import cn.binarywang.wx.miniapp.api.WxMaSubscribeService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.api.impl.WxMaAnalysisServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaCloudServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaCodeServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaExpressServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaImgProcServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaJsapiServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaLiveGoodsServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaLiveMemberServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaLiveServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaMediaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaMsgServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaOcrServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaPluginServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaQrcodeServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaRunServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaSchemeServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaSecCheckServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaSettingServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaShareServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaShopOrderServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaShopSpuServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaSubscribeServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaUserServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.ToJson;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.enums.WxType;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxImgProcService;
import me.chanjar.weixin.common.service.WxOcrService;
import me.chanjar.weixin.common.util.DataUtils;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.common.util.http.HttpType;
import me.chanjar.weixin.common.util.http.RequestExecutor;
import me.chanjar.weixin.common.util.http.RequestHttp;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.common.util.http.okhttp.OkHttpProxyInfo;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * 微信小程序接口服务, OkHttp3 实现类
 *
 * @author Jerry.Chen
 * @since 2019年1月24日 下午2:59:42
 */
@Slf4j
public class WeChatMiniAppServiceOkHttpImpl implements WxMaService, RequestHttp<OkHttpClient, OkHttpProxyInfo> {
    private OkHttpClient                httpClient;
    private OkHttpProxyInfo             httpProxy;
    private WxMaConfig                  wxMaConfig;

    private final WxMaMsgService        kefuService       = new WxMaMsgServiceImpl(this);
    private final WxMaMediaService      materialService   = new WxMaMediaServiceImpl(this);
    private final WxMaUserService       userService       = new WxMaUserServiceImpl(this);
    private final WxMaQrcodeService     qrCodeService     = new WxMaQrcodeServiceImpl(this);
    private final WxMaLiveGoodsService  liveGoodsService  = new WxMaLiveGoodsServiceImpl(this);
    private final WxMaAnalysisService   analysisService   = new WxMaAnalysisServiceImpl(this);
    private final WxMaSchemeService     schemeService     = new WxMaSchemeServiceImpl(this);
    private final WxMaCodeService       codeService       = new WxMaCodeServiceImpl(this);
    private final WxMaSettingService    settingService    = new WxMaSettingServiceImpl(this);
    private final WxMaJsapiService      jsapiService      = new WxMaJsapiServiceImpl(this);
    private final WxMaShareService      shareService      = new WxMaShareServiceImpl(this);
    private final WxMaRunService        runService        = new WxMaRunServiceImpl(this);
    private final WxMaSecCheckService   secCheckService   = new WxMaSecCheckServiceImpl(this);
    private final WxMaPluginService     pluginService     = new WxMaPluginServiceImpl(this);
    private final WxMaExpressService    expressService    = new WxMaExpressServiceImpl(this);
    private final WxMaSubscribeService  subscribeService  = new WxMaSubscribeServiceImpl(this);
    private final WxMaCloudService      cloudService      = new WxMaCloudServiceImpl(this);
    private final WxMaLiveService       liveService       = new WxMaLiveServiceImpl(this);
    private final WxMaLiveMemberService liveMemberService = new WxMaLiveMemberServiceImpl(this);
    private final WxOcrService          ocrService        = new WxMaOcrServiceImpl(this);
    private final WxImgProcService      imgProcService    = new WxMaImgProcServiceImpl(this);
    private final WxMaShopSpuService    shopSpuService    = new WxMaShopSpuServiceImpl(this);
    private final WxMaShopOrderService  shopOrderService  = new WxMaShopOrderServiceImpl(this);

    private int                         retrySleepMillis  = 1000;
    private int                         maxRetryTimes     = 5;
    public static final int             ERR_40001         = 40001;
    public static final int             ERR_42001         = 42001;
    public static final int             ERR_40014         = 40014;

    @Override
    public OkHttpClient getRequestHttpClient() {
        return httpClient;
    }

    @Override
    public OkHttpProxyInfo getRequestHttpProxy() {
        return httpProxy;
    }

    @Override
    public HttpType getRequestType() {
        return HttpType.OK_HTTP;
    }

    @Override
    public void initHttp() {
        WxMaConfig configStorage = this.getWxMaConfig();

        //设置代理
        if (configStorage.getHttpProxyHost() != null && configStorage.getHttpProxyPort() > 0) {
            httpProxy = OkHttpProxyInfo.httpProxy(configStorage.getHttpProxyHost(), configStorage.getHttpProxyPort(),
                    configStorage.getHttpProxyUsername(), configStorage.getHttpProxyPassword());
        }

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (httpProxy != null) {
            clientBuilder.proxy(getRequestHttpProxy().getProxy());

            //设置授权
            clientBuilder.authenticator(new Authenticator() {
                @Override
                public Request authenticate(Route route, Response response) throws IOException {
                    String credential = Credentials.basic(httpProxy.getProxyUsername(), httpProxy.getProxyPassword());
                    return response.request().newBuilder().header("Authorization", credential).build();
                }
            });
        }
        httpClient = clientBuilder.build();
    }

    @Override
    public RequestHttp getRequestHttp() {
        return this;
    }

    @Override
    public String getAccessToken(boolean forceRefresh) throws WxErrorException {
        Lock lock = this.getWxMaConfig().getAccessTokenLock();
        try {
            lock.lock();

            if (this.getWxMaConfig().isAccessTokenExpired() || forceRefresh) {
                String url = String.format(WxMaService.GET_ACCESS_TOKEN_URL, this.getWxMaConfig().getAppid(),
                        this.getWxMaConfig().getSecret());
                try {
                    Request request = new Request.Builder().url(url).get().build();
                    Response response = getRequestHttpClient().newCall(request).execute();
                    String resultContent = response.body().string();
                    WxError error = WxError.fromJson(resultContent);
                    if (error.getErrorCode() != 0) {
                        throw new WxErrorException(error);
                    }
                    WxAccessToken accessToken = WxAccessToken.fromJson(resultContent);
                    this.getWxMaConfig().updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            lock.unlock();
        }

        return this.getWxMaConfig().getAccessToken();
    }

    @Override
    public String getPaidUnionId(String openid, String transactionId, String mchId, String outTradeNo)
            throws WxErrorException {
        Map<String, String> params = new HashMap<>(8);
        params.put("openid", openid);

        if (StringUtils.isNotEmpty(transactionId)) {
            params.put("transaction_id", transactionId);
        }

        if (StringUtils.isNotEmpty(mchId)) {
            params.put("mch_id", mchId);
        }

        if (StringUtils.isNotEmpty(outTradeNo)) {
            params.put("out_trade_no", outTradeNo);
        }

        String responseContent = this.get(GET_PAID_UNION_ID_URL,
                Joiner.on("&").withKeyValueSeparator("=").join(params));
        WxError error = WxError.fromJson(responseContent, WxType.MiniApp);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }

        return JsonParser.parseString(responseContent).getAsJsonObject().get("unionid").getAsString();
    }

    @Override
    public WxMaJscode2SessionResult jsCode2SessionInfo(String jsCode) throws WxErrorException {
        final WxMaConfig config = getWxMaConfig();
        Map<String, String> params = new HashMap<>(8);
        params.put("appid", config.getAppid());
        params.put("secret", config.getSecret());
        params.put("js_code", jsCode);
        params.put("grant_type", "authorization_code");

        String result = get(JSCODE_TO_SESSION_URL, Joiner.on("&").withKeyValueSeparator("=").join(params));
        return WxMaJscode2SessionResult.fromJson(result);
    }

    @Override
    public void setDynamicData(int lifespan, String type, int scene, String data) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("lifespan", lifespan);
        jsonObject.addProperty("query", WxGsonBuilder.create().toJson(ImmutableMap.of("type", type)));
        jsonObject.addProperty("data", data);
        jsonObject.addProperty("scene", scene);

        this.post(SET_DYNAMIC_DATA_URL, jsonObject.toString());
    }

    @Override
    public boolean checkSignature(String timestamp, String nonce, String signature) {
        try {
            return SHA1.gen(this.getWxMaConfig().getToken(), timestamp, nonce).equals(signature);
        } catch (Exception e) {
            log.error("Checking signature failed, and the reason is :" + e.getMessage());
            return false;
        }
    }

    @Override
    public String getAccessToken() throws WxErrorException {
        return getAccessToken(false);
    }

    @Override
    public String get(String url, String queryParam) throws WxErrorException {
        return execute(SimpleGetRequestExecutor.create(this), url, queryParam);
    }

    @Override
    public String post(String url, String postData) throws WxErrorException {
        return execute(SimplePostRequestExecutor.create(this), url, postData);
    }

    @Override
    public String post(String url, Object obj) throws WxErrorException {
        return this.execute(SimplePostRequestExecutor.create(this), url, WxGsonBuilder.create().toJson(obj));
    }

    @Override
    public String post(String url, ToJson obj) throws WxErrorException {
        return this.post(url, obj.toJson());
    }

    @Override
    public String post(String url, JsonObject jsonObject) throws WxErrorException {
        return this.post(url, jsonObject.toString());
    }

    /**
     * 向微信端发送请求，在这里执行的策略是当发生access_token过期时才去刷新，然后重新执行请求，而不是全局定时请求
     */
    @Override
    public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
        int retryTimes = 0;
        do {
            try {
                return this.executeInternal(executor, uri, data);
            } catch (WxErrorException e) {
                if (retryTimes + 1 > this.maxRetryTimes) {
                    log.warn("重试达到最大次数【{}】", maxRetryTimes);
                    //最后一次重试失败后，直接抛出异常，不再等待
                    throw new RuntimeException("微信服务端异常，超出重试次数");
                }

                WxError error = e.getError();
                // -1 系统繁忙, 1000ms后重试
                if (error.getErrorCode() == -1) {
                    int sleepMillis = this.retrySleepMillis * (1 << retryTimes);
                    try {
                        log.warn("微信系统繁忙，{} ms 后重试(第{}次)", sleepMillis, retryTimes + 1);
                        Thread.sleep(sleepMillis);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            }
        } while (retryTimes++ < this.maxRetryTimes);

        log.warn("重试达到最大次数【{}】", this.maxRetryTimes);
        throw new RuntimeException("微信服务端异常，超出重试次数");
    }

    private <T, E> T executeInternal(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
        E dataForLog = DataUtils.handleDataWithSecret(data);

        if (uri.contains("access_token=")) {
            throw new IllegalArgumentException("uri参数中不允许有access_token: " + uri);
        }
        String accessToken = getAccessToken(false);

        String uriWithAccessToken = uri + (uri.contains("?") ? "&" : "?") + "access_token=" + accessToken;

        try {
            T result = executor.execute(uriWithAccessToken, data, WxType.MiniApp);
            log.debug("\n【请求地址】: {}\n【请求参数】：{}\n【响应数据】：{}", uriWithAccessToken, dataForLog, result);
            return result;
        } catch (WxErrorException e) {
            WxError error = e.getError();
            /*
             * 发生以下情况时尝试刷新access_token
             */
            if (error.getErrorCode() == ERR_40001 || error.getErrorCode() == ERR_42001
                    || error.getErrorCode() == ERR_40014) {
                // 强制设置wxMpConfigStorage它的access token过期了，这样在下一次请求里就会刷新access token
                this.getWxMaConfig().expireAccessToken();
                if (this.getWxMaConfig().autoRefreshToken()) {
                    return this.execute(executor, uri, data);
                }
            }

            if (error.getErrorCode() != 0) {
                log.error("\n【请求地址】: {}\n【请求参数】：{}\n【错误信息】：{}", uriWithAccessToken, dataForLog, error);
                throw new WxErrorException(error, e);
            }
            return null;
        } catch (IOException e) {
            log.error("\n【请求地址】: {}\n【请求参数】：{}\n【异常信息】：{}", uriWithAccessToken, dataForLog, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public WxMaConfig getWxMaConfig() {
        return this.wxMaConfig;
    }

    @Override
    public void setWxMaConfig(WxMaConfig wxConfigProvider) {
        this.wxMaConfig = wxConfigProvider;
        this.initHttp();
    }

    @Override
    public void addConfig(String miniappId, WxMaConfig configStorage) {

    }

    @Override
    public void removeConfig(String miniappId) {

    }

    @Override
    public void setMultiConfigs(Map<String, WxMaConfig> configs) {

    }

    @Override
    public void setMultiConfigs(Map<String, WxMaConfig> configs, String defaultMiniappId) {

    }

    @Override
    public boolean switchover(String mpId) {
        return false;
    }

    @Override
    public WxMaService switchoverTo(String miniappId) {
        return null;
    }

    @Override
    public void setRetrySleepMillis(int retrySleepMillis) {
        this.retrySleepMillis = retrySleepMillis;
    }

    @Override
    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    @Override
    public WxMaMsgService getMsgService() {
        return this.kefuService;
    }

    @Override
    public WxMaMediaService getMediaService() {
        return this.materialService;
    }

    @Override
    public WxMaUserService getUserService() {
        return this.userService;
    }

    @Override
    public WxMaQrcodeService getQrcodeService() {
        return this.qrCodeService;
    }

    @Override
    public WxMaSchemeService getWxMaSchemeService() {
        return this.schemeService;
    }

    @Override
    public WxMaLiveGoodsService getLiveGoodsService() {
        return this.liveGoodsService;
    }

    @Override
    public WxOcrService getOcrService() {
        return this.ocrService;
    }

    @Override
    public WxImgProcService getImgProcService() {
        return this.imgProcService;
    }

    @Override
    public WxMaAnalysisService getAnalysisService() {
        return this.analysisService;
    }

    @Override
    public WxMaCodeService getCodeService() {
        return this.codeService;
    }

    @Override
    public WxMaJsapiService getJsapiService() {
        return this.jsapiService;
    }

    @Override
    public WxMaSettingService getSettingService() {
        return this.settingService;
    }

    @Override
    public WxMaShareService getShareService() {
        return this.shareService;
    }

    @Override
    public WxMaRunService getRunService() {
        return this.runService;
    }

    @Override
    public WxMaSecCheckService getSecCheckService() {
        return this.secCheckService;
    }

    @Override
    public WxMaPluginService getPluginService() {
        return this.pluginService;
    }

    @Override
    public WxMaSubscribeService getSubscribeService() {
        return this.subscribeService;
    }

    @Override
    public WxMaExpressService getExpressService() {
        return this.expressService;
    }

    @Override
    public WxMaCloudService getCloudService() {
        return this.cloudService;
    }

    @Override
    public WxMaLiveService getLiveService() {
        return this.liveService;
    }

    @Override
    public WxMaLiveMemberService getLiveMemberService() {
        return this.liveMemberService;
    }

    @Override
    public WxMaShopOrderService getShopOrderService() {
        return this.shopOrderService;
    }

    @Override
    public WxMaShopSpuService getShopSpuService() {
        return this.shopSpuService;
    }
}
