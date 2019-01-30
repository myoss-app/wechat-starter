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

import java.io.IOException;
import java.util.concurrent.locks.Lock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.chanjar.weixin.common.WxType;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;
import me.chanjar.weixin.mp.enums.TicketType;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 微信公众号API的实现类，使用 OkHttp3 发起请求
 *
 * @author Jerry.Chen
 * @since 2018年8月28日 下午4:39:26
 */
public class WeChatMpServiceOkHttpImpl extends WxMpServiceOkHttpImpl {
    private static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * 获取 access_token 值
     *
     * @param forceRefresh 是否强制刷新
     * @return access_token 值
     */
    @Override
    public String getAccessToken(boolean forceRefresh) throws WxErrorException {
        this.log.debug("WeChatMpServiceOkHttpImpl getAccessToken is running");
        WxMpConfigStorage wxMpConfigStorage = getWxMpConfigStorage();
        if (!forceRefresh && !wxMpConfigStorage.isAccessTokenExpired()) {
            return wxMpConfigStorage.getAccessToken();
        }

        Lock lock = wxMpConfigStorage.getAccessTokenLock();
        try {
            lock.lock();
            String url = String.format(WxMpService.GET_ACCESS_TOKEN_URL, this.getWxMpConfigStorage().getAppId(),
                    this.getWxMpConfigStorage().getSecret());
            Request request = new Request.Builder().url(url).get().build();
            Response response = getRequestHttpClient().newCall(request).execute();
            assert response.body() != null;
            String resultContent = response.body().string();
            WxError error = WxError.fromJson(resultContent, WxType.MP);
            if (error.getErrorCode() != 0) {
                throw new WxErrorException(error);
            }
            WxAccessToken accessToken = WxAccessToken.fromJson(resultContent);
            wxMpConfigStorage.updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
            return accessToken.getAccessToken();
        } catch (IOException e) {
            this.log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        return null;
    }

    /**
     * 获取 ticket 值
     *
     * @param type ticket类型
     * @param forceRefresh 是否强制刷新
     * @return ticket 值
     */
    @Override
    public String getTicket(TicketType type, boolean forceRefresh) throws WxErrorException {
        WxMpConfigStorage wxMpConfigStorage = this.getWxMpConfigStorage();
        if (!forceRefresh && !wxMpConfigStorage.isTicketExpired(type)) {
            return wxMpConfigStorage.getTicket(type);
        }

        Lock lock = wxMpConfigStorage.getTicketLock(type);
        try {
            lock.lock();
            String responseContent = execute(SimpleGetRequestExecutor.create(this),
                    WxMpService.GET_TICKET_URL + type.getCode(), null);
            JsonObject tmpJsonObject = JSON_PARSER.parse(responseContent).getAsJsonObject();
            String ticket = tmpJsonObject.get("ticket").getAsString();
            int expiresInSeconds = tmpJsonObject.get("expires_in").getAsInt();
            wxMpConfigStorage.updateTicket(type, ticket, expiresInSeconds);
            return ticket;
        } finally {
            lock.unlock();
        }
    }
}
