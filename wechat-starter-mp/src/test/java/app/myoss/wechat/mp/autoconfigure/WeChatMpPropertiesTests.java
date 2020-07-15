/*
 * Copyright 2018-2020 https://github.com/myoss
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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import app.myoss.cloud.core.lang.json.JsonApi;
import app.myoss.wechat.mp.autoconfigure.WeChatMpProperties.WeChatMp;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link WeChatMpProperties} 测试类
 *
 * @author Jerry.Chen
 * @since 2020年7月15日 上午10:11:10
 */
@Slf4j
public class WeChatMpPropertiesTests {
    @Test
    public void test1() {
        WeChatMpProperties weChatMp = new WeChatMpProperties();
        WeChatMp config = new WeChatMp();
        config.setAppId("appId");
        config.setAppName("my wechat app");
        config.setAppSecret("wechat app secret");
        config.setCustomAppId("10001");
        config.setEncodingAesKey("aes key");
        config.setOriginalId("original id");
        config.setToken("token value");
        weChatMp.setConfig(config);
        String json = weChatMp.toString();
        log.info(json);

        Assert.assertFalse(StringUtils.contains(json, "appSecret"));
        Assert.assertFalse(StringUtils.contains(json, "token"));
        Assert.assertFalse(StringUtils.contains(json, "encodingAesKey"));

        WeChatMpProperties target = JsonApi.fromJson(json, WeChatMpProperties.class);
        Assert.assertNull(target.getConfig().getAppSecret());
        Assert.assertNull(target.getConfig().getToken());
        Assert.assertNull(target.getConfig().getEncodingAesKey());
    }

    @Test
    public void test2() {
        WeChatMpProperties weChatMp = new WeChatMpProperties();
        WeChatMp config = new WeChatMp();
        config.setAppId("appId");
        config.setAppName("my wechat app");
        config.setAppSecret("wechat app secret");
        config.setCustomAppId("10001");
        config.setEncodingAesKey("aes key");
        config.setOriginalId("original id");
        config.setToken("token value");
        weChatMp.setConfig(config);
        String json = "{\"enabled\":false,\"config\":{\"appName\":\"my wechat app\",\"customAppId\":\"10001\",\"originalId\":\"original id\",\"appId\":\"appId\",\"appSecret\":\"wechat app secret\",\"token\":\"token value\",\"encodingAesKey\":\"aes key\"}}";

        WeChatMpProperties target = JsonApi.fromJson(json, WeChatMpProperties.class);
        Assert.assertNotNull(target.getConfig().getAppSecret());
        Assert.assertNotNull(target.getConfig().getToken());
        Assert.assertNotNull(target.getConfig().getEncodingAesKey());
        Assert.assertEquals(weChatMp, target);
    }
}
