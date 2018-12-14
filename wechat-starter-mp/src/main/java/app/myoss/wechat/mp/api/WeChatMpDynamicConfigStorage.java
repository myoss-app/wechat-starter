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

package app.myoss.wechat.mp.api;

import java.util.concurrent.locks.Lock;

/**
 * 微信公众号 "动态配置"（如：access_token）存储接口
 *
 * @author Jerry.Chen
 * @since 2018年8月28日 下午4:04:57
 */
public interface WeChatMpDynamicConfigStorage {
    /**
     * 获取 access_token 值，请使用定时任务调用此接口进行刷新 access_token 值
     *
     * @param forceRefresh 是否强制刷新
     * @return access_token 值
     */
    String getAccessToken(boolean forceRefresh);

    /**
     * 获取 access_token 值
     *
     * @return access_token 值
     */
    String getAccessToken();

    /**
     * 判断 access_token 是否过期
     *
     * @return true: 过期; false: 未过期
     */
    boolean isAccessTokenExpired();

    /**
     * 获取 access_token 过期时间
     *
     * @return 过期时间，单位：秒
     */
    long getExpiresTime();

    /**
     * 更新 access_token 值
     *
     * @param accessToken 新的 access_token 值
     * @param expiresInSeconds 过期时间，单位：秒
     */
    void updateAccessToken(String accessToken, int expiresInSeconds);

    /**
     * 强制将 access_token 过期掉
     */
    void expireAccessToken();

    /**
     * 获取 redis lock 锁
     *
     * @param key 锁的名字
     * @return redis lock 锁实例
     */
    Lock getLock(String key);

    /**
     * 获取 jsapi_ticket 值，请使用定时任务调用此接口进行刷新 jsapi_ticket 值
     *
     * @param forceRefresh 是否强制刷新
     * @return access_token 值
     */
    String getJsapiTicket(boolean forceRefresh);

    /**
     * 获取 jsapi_ticket 值
     *
     * @return jsapi_ticket 值
     */
    String getJsapiTicket();

    /**
     * 判断 jsapi_ticket 是否过期
     *
     * @return true: 过期; false: 未过期
     */
    boolean isJsapiTicketExpired();

    /**
     * 强制将 jsapi_ticket 过期掉
     */
    void expireJsapiTicket();

    /**
     * 更新 jsapi_ticket 值
     *
     * @param jsapiTicket 新的 jsapi_ticket 值
     * @param expiresInSeconds 过期时间，单位：秒
     */
    void updateJsapiTicket(String jsapiTicket, int expiresInSeconds);
}
