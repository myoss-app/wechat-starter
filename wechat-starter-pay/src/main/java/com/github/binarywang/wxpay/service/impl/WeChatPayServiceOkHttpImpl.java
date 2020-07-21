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

package com.github.binarywang.wxpay.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;

import com.github.binarywang.wxpay.bean.WxPayApiData;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.http.okhttp.OkHttpProxyInfo;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 微信支付请求实现类，ok-http3实现.
 *
 * @author Jerry.Chen
 * @since 2019年10月14日 下午12:55:55
 */
@Slf4j
public class WeChatPayServiceOkHttpImpl extends BaseWxPayServiceImpl {
    private OkHttpClient    httpClient;
    private OkHttpProxyInfo httpProxy;

    public OkHttpClient getRequestHttpClient() {
        return httpClient;
    }

    public OkHttpProxyInfo getRequestHttpProxy() {
        return httpProxy;
    }

    /**
     * 初始化 OkHttp3
     */
    @PostConstruct
    public void initHttp() {
        WxPayConfig wxPayConfig = getConfig();
        //设置代理
        if (wxPayConfig.getHttpProxyHost() != null && wxPayConfig.getHttpProxyPort() > 0) {
            httpProxy = OkHttpProxyInfo.httpProxy(wxPayConfig.getHttpProxyHost(), wxPayConfig.getHttpProxyPort(),
                    wxPayConfig.getHttpProxyUsername(), wxPayConfig.getHttpProxyPassword());
        }

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (httpProxy != null) {
            clientBuilder.proxy(getRequestHttpProxy().getProxy());

            //设置授权
            clientBuilder.authenticator((route, response) -> {
                String credential = Credentials.basic(httpProxy.getProxyUsername(), httpProxy.getProxyPassword());
                return response.request().newBuilder().header("Authorization", credential).build();
            });
        }

        if (null != wxPayConfig.getSslContext()) {
            httpClient = clientBuilder
                    .sslSocketFactory(wxPayConfig.getSslContext().getSocketFactory(), new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    })
                    .build();
        } else {
            httpClient = clientBuilder.build();
        }
    }

    @Override
    public byte[] postForBytes(String url, String requestStr, boolean useKey) throws WxPayException {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.get("application/xml"), requestStr);
            Request request = new Request.Builder().url(url).post(requestBody).build();
            Response response = getRequestHttpClient().newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            final String responseString = new String(Base64.getDecoder().decode(responseBytes), StandardCharsets.UTF_8);
            log.info("\n【请求地址】：{}\n【请求数据】：{}\n【响应数据(Base64编码后)】：{}", url, requestStr, responseString);
            if (this.getConfig().isIfSaveApiData()) {
                wxApiData.set(new WxPayApiData(url, requestStr, responseString, null));
            }
            return responseBytes;
        } catch (Exception e) {
            log.error("\n【请求地址】：{}\n【请求数据】：{}\n【异常信息】：{}", url, requestStr, e.getMessage());
            wxApiData.set(new WxPayApiData(url, requestStr, null, e.getMessage()));
            throw new WxPayException(e.getMessage(), e);
        }
    }

    @Override
    public String post(String url, String requestStr, boolean useKey) throws WxPayException {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.get("application/xml"), requestStr);
            Request request = new Request.Builder().url(url).post(requestBody).build();
            Response response = getRequestHttpClient().newCall(request).execute();
            String responseString = response.body().string();
            if (StringUtils.isBlank(responseString)) {
                throw new WxPayException("响应信息为空");
            }

            log.info("\n【请求地址】：{}\n【请求数据】：{}\n【响应数据】：{}", url, requestStr, responseString);
            if (this.getConfig().isIfSaveApiData()) {
                wxApiData.set(new WxPayApiData(url, requestStr, responseString, null));
            }
            return responseString;
        } catch (Exception e) {
            log.error("\n【请求地址】：{}\n【请求数据】：{}\n【异常信息】：{}", url, requestStr, e.getMessage());
            wxApiData.set(new WxPayApiData(url, requestStr, null, e.getMessage()));
            throw new WxPayException(e.getMessage(), e);
        }
    }
}
