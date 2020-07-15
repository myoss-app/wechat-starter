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

package app.myoss.wechat.miniapp.autoconfigure;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.google.gson.annotations.Expose;

import app.myoss.cloud.core.lang.json.JsonApi;
import app.myoss.cloud.core.spring.context.SpringContextHolder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序的属性配置
 *
 * @author Jerry.Chen
 * @since 2019年1月24日 上午11:15:42
 */
@EqualsAndHashCode
@Getter
@Setter
@ConfigurationProperties(prefix = "wechat.mini-app")
public class WeChatMiniAppProperties {
    /**
     * 是否启用
     */
    private boolean                    enabled;
    /**
     * 微信小程序的属性配置，可以设置1个
     */
    private WeChatMiniApp              config;
    /**
     * 微信小程序的属性配置，可以设置多个
     */
    private Map<String, WeChatMiniApp> configs;

    /**
     * 获取微信小程序的属性配置，根据 customAppId
     *
     * @param customAppId 用户自定义AppID
     * @return 微信小程序的属性配置
     */
    public WeChatMiniApp getByCustomAppId(String customAppId) {
        if (StringUtils.isBlank(customAppId)) {
            return null;
        }
        if (config != null && customAppId.equals(config.getCustomAppId())) {
            return config;
        }
        for (WeChatMiniApp weChatMiniApp : configs.values()) {
            if (customAppId.equals(weChatMiniApp.getCustomAppId())) {
                return weChatMiniApp;
            }
        }
        return null;
    }

    /**
     * 获取微信小程序的属性配置，根据 originalId
     *
     * @param originalId 原始ID(originalID)
     * @return 微信小程序的属性配置
     */
    public WeChatMiniApp getByOriginalId(String originalId) {
        if (StringUtils.isBlank(originalId)) {
            return null;
        }
        if (config != null && originalId.equals(config.getOriginalId())) {
            return config;
        }
        for (WeChatMiniApp weChatMiniApp : configs.values()) {
            if (originalId.equals(weChatMiniApp.getAppId())) {
                return weChatMiniApp;
            }
        }
        return null;
    }

    /**
     * 获取微信小程序的属性配置，根据 appId
     *
     * @param appId 开发者ID(AppID)
     * @return 微信小程序的属性配置
     */
    public WeChatMiniApp getByAppId(String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }
        if (config != null && appId.equals(config.getAppId())) {
            return config;
        }
        for (WeChatMiniApp weChatMiniApp : configs.values()) {
            if (appId.equals(weChatMiniApp.getAppId())) {
                return weChatMiniApp;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return JsonApi.toJson(this);
    }

    /**
     * 微信小程序的属性配置
     */
    @EqualsAndHashCode
    @Getter
    @Setter
    public static class WeChatMiniApp {
        /**
         * 微信小程序的名称，用于识别当前配置属于哪个小程序
         */
        private String appName;
        /**
         * 用户自定义AppID
         */
        private String customAppId;
        /**
         * 原始ID
         */
        private String originalId;
        /**
         * AppID(小程序ID)
         */
        private String appId;
        /**
         * AppSecret(小程序密钥)
         */
        @JsonProperty(access = Access.WRITE_ONLY)
        @Expose(serialize = false)
        @JSONField(serialize = false)
        private String appSecret;
        /**
         * 服务器配置，令牌
         */
        @JsonProperty(access = Access.WRITE_ONLY)
        @Expose(serialize = false)
        @JSONField(serialize = false)
        private String token;
        /**
         * 服务器配置，EncodingAESKey
         */
        @JsonProperty(access = Access.WRITE_ONLY)
        @Expose(serialize = false)
        @JSONField(serialize = false)
        private String encodingAesKey;
        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;

        /**
         * 获取微信小程序的 Spring Bean 对象，根据 customAppId
         *
         * @param clazz type the bean must match
         * @param <T> bean class type
         * @return bean instance
         */
        public <T> T getSpringBeanByCustomAppId(Class<T> clazz) {
            String name = customAppId + clazz.getSimpleName();
            return SpringContextHolder.getBean(name, clazz);
        }

        @Override
        public String toString() {
            return JsonApi.toJson(this);
        }
    }
}
