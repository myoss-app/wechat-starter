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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序自动配置
 *
 * @author Jerry.Chen
 * @since 2019年1月24日 上午10:46:07
 */
@EnableConfigurationProperties(WeChatMiniAppProperties.class)
@ConditionalOnProperty(prefix = "wechat.mini-app", value = "enabled", matchIfMissing = false)
@Configuration
public class WeChatMiniAppAutoConfiguration {
}
