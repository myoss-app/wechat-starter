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

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import com.github.myoss.phoenix.core.cache.lock.LockService;
import com.github.myoss.phoenix.core.cache.lock.functions.LockFunction;
import com.github.myoss.phoenix.core.cache.lock.functions.LockFunctionGeneric;
import com.github.myoss.phoenix.core.exception.BizRuntimeException;

import app.myoss.wechat.mp.api.WeChatMpDynamicConfigStorage;
import app.myoss.wechat.mp.autoconfigure.WeChatMpProperties.WeChatMp;
import lombok.Getter;
import me.chanjar.weixin.common.WxType;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * 微信公众号 "动态配置"（如：access_token）使用 Redis 存储
 *
 * @author Jerry.Chen
 * @since 2018年8月28日 上午11:01:48
 */
@Getter
public class WeChatMpDynamicConfigInRedisStorage implements WeChatMpDynamicConfigStorage {
    private static final String ACCESS_TOKEN_KEY = "wechat_mp_access_token_";
    /**
     * 微信公众号的属性配置
     */
    private WeChatMp            weChatMp;
    /**
     * Http RestTemplate
     */
    private RestTemplate        restTemplate;
    /**
     * 缓存锁服务接口
     */
    private LockService         lockService;
    /**
     * 锁的过期时间
     */
    private int                 lockTime;
    /**
     * Spring RedisTemplate
     */
    private StringRedisTemplate redisTemplate;
    /**
     * 每个公众号生成独有的存储key
     */
    private String              accessTokenKey;

    /**
     * 微信公众号 "动态配置"（如：access_token）使用 Redis 存储
     *
     * @param weChatMp 微信公众号的属性配置
     * @param restTemplate Http RestTemplate
     * @param lockService 缓存锁服务接口
     * @param lockTime 锁的过期时间
     * @param redisTemplate Spring RedisTemplate
     */
    public WeChatMpDynamicConfigInRedisStorage(WeChatMp weChatMp, RestTemplate restTemplate, LockService lockService,
                                               int lockTime, StringRedisTemplate redisTemplate) {
        this.weChatMp = weChatMp;
        this.restTemplate = restTemplate;
        this.lockService = lockService;
        this.lockTime = lockTime;
        this.redisTemplate = redisTemplate;
        this.accessTokenKey = ACCESS_TOKEN_KEY.concat(weChatMp.getAppId());
    }

    @Override
    public String getAccessToken(boolean forceRefresh) {
        if (!forceRefresh && !isAccessTokenExpired()) {
            return this.redisTemplate.opsForValue().get(this.accessTokenKey);
        }

        String lockKey = "getAccessTokenLockKey" + this.accessTokenKey;
        return lockService.executeByLock(lockKey, getLockTime(), new LockFunctionGeneric<String>() {
            @Override
            public String onLockSuccess() {
                WeChatMp weChatMp = getWeChatMp();
                String url = String.format(WxMpService.GET_ACCESS_TOKEN_URL, weChatMp.getAppId(),
                        weChatMp.getAppSecret());
                String resultContent = restTemplate.getForObject(url, String.class);
                WxError error = WxError.fromJson(resultContent, WxType.MP);
                if (error.getErrorCode() != 0) {
                    throw new BizRuntimeException(error.toString());
                }
                WxAccessToken accessToken = WxAccessToken.fromJson(resultContent);
                updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
                return accessToken.getAccessToken();
            }

            @Override
            public String onLockFailed() {
                throw new BizRuntimeException("getAccessToken lock failed");
            }
        });
    }

    @Override
    public String getAccessToken() {
        return getAccessToken(false);
    }

    @Override
    public boolean isAccessTokenExpired() {
        Long expire = this.redisTemplate.getExpire(this.accessTokenKey);
        return expire == null || expire < 2;
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresInSeconds) {
        String lockKey = "updateAccessTokenLockKey" + this.accessTokenKey;
        lockService.executeByLock(lockKey, getLockTime(), new LockFunction() {
            @Override
            public void onLockSuccess() {
                redisTemplate.opsForValue().set(accessTokenKey, accessToken, expiresInSeconds - 200, TimeUnit.SECONDS);
            }

            @Override
            public void onLockFailed() {
                throw new BizRuntimeException("updateAccessToken lock failed");
            }
        });
    }

    @Override
    public void expireAccessToken() {
        String lockKey = "expireAccessTokenLockKey" + this.accessTokenKey;
        lockService.executeByLock(lockKey, getLockTime(), new LockFunction() {
            @Override
            public void onLockSuccess() {
                redisTemplate.expire(accessTokenKey, 0, TimeUnit.SECONDS);
            }

            @Override
            public void onLockFailed() {
                throw new BizRuntimeException("expireAccessToken lock failed");
            }
        });
    }
}
