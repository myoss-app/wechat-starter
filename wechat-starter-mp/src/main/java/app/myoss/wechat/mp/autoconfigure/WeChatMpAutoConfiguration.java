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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * 微信公众号自动配置
 *
 * @author Jerry.Chen
 * @since 2018年8月27日 下午6:33:07
 */
@EnableConfigurationProperties(WeChatMpProperties.class)
@ConditionalOnProperty(prefix = "wechat.mp", value = "enabled", matchIfMissing = false)
@Configuration
public class WeChatMpAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public WxMpService wxMpService(WeChatMpProperties weChatMpProperties) {
        List<WeChatMpProperties.WeChatMp> configs = new ArrayList<>();
        WeChatMpProperties.WeChatMp config = weChatMpProperties.getConfig();
        if (null != config) {
            configs.add(config);
        }
        Map<String, WeChatMpProperties.WeChatMp> configMap = weChatMpProperties.getConfigs();
        if (null != configMap) {
            for (Map.Entry<String, WeChatMpProperties.WeChatMp> entry : configMap.entrySet()) {
                configs.add(entry.getValue());
            }
        }
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setMultiConfigStorages(configs.stream().map(a -> {
            WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
            configStorage.setAppId(a.getAppId());
            configStorage.setSecret(a.getAppSecret());
            configStorage.setToken(a.getToken());
            configStorage.setAesKey(a.getEncodingAesKey());
            return configStorage;
        }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));
        return wxMpService;
    }
}
