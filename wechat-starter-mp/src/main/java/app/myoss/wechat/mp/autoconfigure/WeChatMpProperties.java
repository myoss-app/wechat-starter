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

package app.myoss.wechat.mp.autoconfigure;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信公众号的属性配置
 *
 * @author Jerry.Chen
 * @since 2018年8月27日 下午6:20:54
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "wechat.mp")
public class WeChatMpProperties {
    /**
     * 是否启用
     */
    private boolean               enabled;
    /**
     * 微信公众号的属性配置，可以设置多个
     */
    private Map<String, WeChatMp> configs;

    /**
     * 获取微信公众号的属性配置，根据 AppId
     *
     * @param appId 开发者ID(AppID)
     * @return 微信公众号的属性配置
     */
    public WeChatMp getByAppId(String appId) {
        for (WeChatMp weChatMp : configs.values()) {
            if (appId.equals(weChatMp.getAppId())) {
                return weChatMp;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * 微信公众号的属性配置
     */
    @Getter
    @Setter
    public static class WeChatMp {
        /**
         * 开发者ID(AppID)
         */
        private String appId;
        /**
         * 开发者密码
         */
        @JSONField(serialize = false)
        private String appSecret;
        /**
         * 服务器配置，令牌
         */
        @JSONField(serialize = false)
        private String token;
        /**
         * 服务器配置，EncodingAESKey
         */
        @JSONField(serialize = false)
        private String encodingAesKey;

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }
}
