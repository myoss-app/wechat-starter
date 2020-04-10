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

package app.myoss.wechat.pay.autoconfigure;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import app.myoss.cloud.core.spring.context.SpringContextHolder;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信支付的属性配置
 *
 * @author Jerry.Chen
 * @since 2019年10月14日 下午12:47:07
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "wechat.pay")
public class WeChatPayProperties {
    /**
     * 是否启用
     */
    private boolean               enabled;
    /**
     * 微信支付的属性配置，可以设置1个
     */
    private WeChatMp              config;
    /**
     * 微信支付的属性配置，可以设置多个
     */
    private Map<String, WeChatMp> configs;

    /**
     * 获取微信支付的属性配置，根据 customAppId
     *
     * @param customAppId 用户自定义AppID
     * @return 微信支付的属性配置
     */
    public WeChatMp getByCustomAppId(String customAppId) {
        if (StringUtils.isBlank(customAppId)) {
            return null;
        }
        if (config != null && customAppId.equals(config.getCustomAppId())) {
            return config;
        }
        for (WeChatMp weChatMp : configs.values()) {
            if (customAppId.equals(weChatMp.getCustomAppId())) {
                return weChatMp;
            }
        }
        return null;
    }

    /**
     * 获取微信支付的属性配置，根据 originalId
     *
     * @param originalId 原始ID(originalID)
     * @return 微信支付的属性配置
     */
    public WeChatMp getByOriginalId(String originalId) {
        if (StringUtils.isBlank(originalId)) {
            return null;
        }
        if (config != null && originalId.equals(config.getOriginalId())) {
            return config;
        }
        for (WeChatMp weChatMp : configs.values()) {
            if (originalId.equals(weChatMp.getAppId())) {
                return weChatMp;
            }
        }
        return null;
    }

    /**
     * 获取微信支付的属性配置，根据 appId
     *
     * @param appId 开发者ID(AppID)
     * @return 微信支付的属性配置
     */
    public WeChatMp getByAppId(String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }
        if (config != null && appId.equals(config.getAppId())) {
            return config;
        }
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
     * 微信支付的属性配置
     */
    @Getter
    @Setter
    public static class WeChatMp {
        /**
         * 公众号/小程序的名称，用于识别当前配置属于哪个公众号/小程序
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
        /**
         * 商户号.
         */
        @JSONField(serialize = false)
        private String mchId;
        /**
         * 商户密钥.
         */
        @JSONField(serialize = false)
        private String mchKey;
        /**
         * 服务商模式下的子商户号.
         */
        private String subMchId;
        /**
         * 微信支付异步回掉地址，通知url必须为直接可访问的url，不能携带参数.
         */
        private String notifyUrl;
        /**
         * 交易类型.
         *
         * <pre>
         * JSAPI--公众号支付
         * NATIVE--原生扫码支付
         * APP--app支付
         * </pre>
         */
        private String tradeType;
        /**
         * 微信支付，证书路径
         */
        private String keyPath;
        /**
         * 服务商模式下的子场景
         */
        private String subAppId;

        /**
         * 获取微信支付的 Spring Bean 对象，根据 customAppId
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
            return JSON.toJSONString(this);
        }
    }
}
