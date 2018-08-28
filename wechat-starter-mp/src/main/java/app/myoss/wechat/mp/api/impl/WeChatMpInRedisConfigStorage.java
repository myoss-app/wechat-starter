/*
 * Copyright 2018-2018 https://github.com/myoss
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

package app.myoss.wechat.mp.api.impl;

import java.io.File;
import java.util.concurrent.locks.Lock;

import app.myoss.wechat.mp.api.WeChatMpDynamicConfigStorage;
import app.myoss.wechat.mp.autoconfigure.WeChatMpProperties.WeChatMp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;

/**
 * 基于 Redis 的微信配置 provider
 *
 * @author Jerry.Chen
 * @since 2018年8月28日 下午3:48:00
 */
@Getter
@AllArgsConstructor
public class WeChatMpInRedisConfigStorage implements WxMpConfigStorage {
    /**
     * 微信公众号的属性配置
     */
    private WeChatMp                     weChatMp;
    /**
     * 微信公众号 "动态配置"（如：access_token）存储服务
     */
    private WeChatMpDynamicConfigStorage weChatMpDynamicConfigStorage;

    @Override
    public String getAccessToken() {
        return weChatMpDynamicConfigStorage.getAccessToken();
    }

    @Override
    public Lock getAccessTokenLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAccessTokenExpired() {
        return weChatMpDynamicConfigStorage.isAccessTokenExpired();
    }

    @Override
    public void expireAccessToken() {
        weChatMpDynamicConfigStorage.expireAccessToken();
    }

    @Override
    public void updateAccessToken(WxAccessToken accessToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresInSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getJsapiTicket() {
        return null;
    }

    @Override
    public Lock getJsapiTicketLock() {
        return null;
    }

    @Override
    public boolean isJsapiTicketExpired() {
        return false;
    }

    @Override
    public void expireJsapiTicket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCardApiTicket() {
        return null;
    }

    @Override
    public Lock getCardApiTicketLock() {
        return null;
    }

    @Override
    public boolean isCardApiTicketExpired() {
        return false;
    }

    @Override
    public void expireCardApiTicket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCardApiTicket(String cardApiTicket, int expiresInSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAppId() {
        return weChatMp.getAppId();
    }

    @Override
    public String getSecret() {
        return weChatMp.getAppSecret();
    }

    @Override
    public String getToken() {
        return weChatMp.getToken();
    }

    @Override
    public String getAesKey() {
        return weChatMp.getEncodingAesKey();
    }

    @Override
    public String getTemplateId() {
        return null;
    }

    @Override
    public long getExpiresTime() {
        return 0;
    }

    @Override
    public String getOauth2redirectUri() {
        return null;
    }

    @Override
    public String getHttpProxyHost() {
        return null;
    }

    @Override
    public int getHttpProxyPort() {
        return 0;
    }

    @Override
    public String getHttpProxyUsername() {
        return null;
    }

    @Override
    public String getHttpProxyPassword() {
        return null;
    }

    @Override
    public File getTmpDirFile() {
        return null;
    }

    @Override
    public ApacheHttpClientBuilder getApacheHttpClientBuilder() {
        return null;
    }

    @Override
    public boolean autoRefreshToken() {
        return true;
    }
}
