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

import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;

/**
 * 微信公众号API的实现类，使用 OkHttp3 发起请求
 *
 * @author Jerry.Chen
 * @since 2018年8月28日 下午4:39:26
 */
public class WeChatMpServiceOkHttpImpl extends WxMpServiceOkHttpImpl {
    /**
     * 获取 access_token 值，使用外部的定时任务去刷新 access_token 值
     *
     * @param forceRefresh 这里不会去强制刷新，使用定时任务统一去触发
     * @return access_token 值
     */
    @Override
    public String getAccessToken(boolean forceRefresh) {
        return getWxMpConfigStorage().getAccessToken();
    }

    /**
     * 获取 jsapi_ticket 值，使用外部的定时任务去刷新 jsapi_ticket 值
     *
     * @param forceRefresh 这里不会去强制刷新，使用定时任务统一去触发
     * @return jsapi_ticket 值
     */
    @Override
    public String getJsapiTicket(boolean forceRefresh) {
        return getWxMpConfigStorage().getJsapiTicket();
    }
}
