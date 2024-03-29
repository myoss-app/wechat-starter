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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WeChatPayServiceOkHttpImpl;

import app.myoss.wechat.pay.autoconfigure.WeChatPayProperties.WeChatMp;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信支付自动配置
 *
 * @author Jerry.Chen
 * @since 2019年10月14日 下午12:47:07
 */
@Slf4j
@EnableConfigurationProperties(WeChatPayProperties.class)
@ConditionalOnProperty(prefix = "wechat.pay", value = "enabled", matchIfMissing = false)
@Configuration
public class WeChatPayAutoConfiguration {
    /**
     * 初始化微信支付API的Service
     *
     * @param weChatPayProperties 微信支付配置
     * @return 微信支付API的Service
     */
    @ConditionalOnMissingBean
    @Bean
    public WxPayService wxPayService(WeChatPayProperties weChatPayProperties) {
        List<WeChatMp> configs = new ArrayList<>();
        WeChatMp config = weChatPayProperties.getConfig();
        if (null != config) {
            configs.add(config);
        }
        Map<String, WeChatMp> configMap = weChatPayProperties.getConfigs();
        if (null != configMap) {
            for (Map.Entry<String, WeChatMp> entry : configMap.entrySet()) {
                configs.add(entry.getValue());
            }
        }
        WxPayService wxPayService = new WeChatPayServiceOkHttpImpl();
        wxPayService.setMultiConfig(configs.stream().map(item -> {
            WxPayConfig payConfig = new WxPayConfig();
            payConfig.setAppId(item.getAppId());
            payConfig.setMchId(item.getMchId());
            payConfig.setMchKey(item.getMchKey());
            payConfig.setNotifyUrl(item.getNotifyUrl());
            payConfig.setTradeType(item.getTradeType());
            payConfig.setKeyPath(item.getKeyPath());
            payConfig.setSubMchId(item.getSubMchId());
            payConfig.setSubAppId(item.getSubAppId());
            if (StringUtils.isNotBlank(item.getKeyPath())) {
                try {
                    payConfig.setSslContext(payConfig.initSSLContext());
                } catch (WxPayException e) {
                    log.warn("获取SslContext异常 => appId: {}", item.getAppId(), e);
                }
            }
            return payConfig;
        }).collect(Collectors.toMap(WxPayConfig::getAppId, a -> a, (o, n) -> o)));
        return wxPayService;
    }
}
