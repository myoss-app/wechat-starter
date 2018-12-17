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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.springframework.data.redis.core.StringRedisTemplate;

import app.myoss.cloud.cache.lock.LockService;
import app.myoss.cloud.cache.lock.functions.LockFunction;
import app.myoss.cloud.core.exception.BizRuntimeException;
import app.myoss.wechat.mp.api.WeChatMpDynamicConfigStorage;
import app.myoss.wechat.mp.autoconfigure.WeChatMpProperties.WeChatMp;
import lombok.Getter;

/**
 * 微信公众号 "动态配置"（如：access_token）使用 Redis 存储
 *
 * @author Jerry.Chen
 * @since 2018年8月28日 上午11:01:48
 */
@Getter
public class WeChatMpDynamicConfigInRedisStorage implements WeChatMpDynamicConfigStorage {
    private static final String          ACCESS_TOKEN_KEY    = "wechat_mp_access_token_";
    private static final String          JSAPI_TICKET_KEY    = "wechat_mp_jsapi_ticket_";
    private static final String          CARD_API_TICKET_KEY = "wechat_card_api_ticket_";

    /**
     * 微信公众号的属性配置
     */
    private WeChatMp                     weChatMp;
    /**
     * 缓存锁服务接口
     */
    private LockService                  lockService;
    /**
     * 锁的过期时间
     */
    private int                          lockTime;
    /**
     * Spring RedisTemplate
     */
    private StringRedisTemplate          redisTemplate;
    /**
     * access_token 每个公众号生成独有的存储key
     */
    private String                       accessTokenKey;
    /**
     * jsapi_ticket 每个公众号生成独有的存储key
     */
    private String                       jsapiTicketKey;
    /**
     * 卡券api_ticket，每个公众号生成独有的存储key
     */
    private String                       cardApiTicketKey;
    private Map<String, LockService4Jdk> lockService4JdkMap  = new ConcurrentHashMap<>();

    /**
     * 微信公众号 "动态配置"（如：access_token）使用 Redis 存储
     *
     * @param weChatMp 微信公众号的属性配置
     * @param lockService 缓存锁服务接口
     * @param lockTime 锁的过期时间
     * @param redisTemplate String RedisTemplate
     */
    public WeChatMpDynamicConfigInRedisStorage(WeChatMp weChatMp, LockService lockService, int lockTime,
                                               StringRedisTemplate redisTemplate) {
        this.weChatMp = weChatMp;
        this.lockService = lockService;
        this.lockTime = lockTime;
        this.redisTemplate = redisTemplate;
        this.accessTokenKey = ACCESS_TOKEN_KEY.concat(weChatMp.getAppId());
        this.jsapiTicketKey = JSAPI_TICKET_KEY.concat(weChatMp.getAppId());
        this.cardApiTicketKey = CARD_API_TICKET_KEY.concat(weChatMp.getAppId());
    }

    @Override
    public String getAccessToken() {
        return this.redisTemplate.opsForValue().get(this.accessTokenKey);
    }

    @Override
    public boolean isAccessTokenExpired() {
        Long expire = this.redisTemplate.getExpire(this.accessTokenKey);
        return (expire == null || expire < 2);
    }

    @Override
    public long getExpiresTime() {
        Long expire = this.redisTemplate.getExpire(this.accessTokenKey);
        return (expire != null ? expire : -2);
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresInSeconds) {
        String lockKey = "updateAccessTokenLockKey_" + this.accessTokenKey;
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
        String lockKey = "expireAccessTokenLockKey_" + this.accessTokenKey;
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

    @Override
    public Lock getLock(String key) {
        return lockService4JdkMap.computeIfAbsent(key, s -> new LockService4Jdk(key, lockService));
    }

    @Override
    public String getJsapiTicket() {
        return this.redisTemplate.opsForValue().get(this.jsapiTicketKey);
    }

    @Override
    public boolean isJsapiTicketExpired() {
        Long expire = this.redisTemplate.getExpire(this.jsapiTicketKey);
        return (expire == null || expire < 2);
    }

    @Override
    public long getJsapiTicketExpiresTime() {
        Long expire = this.redisTemplate.getExpire(this.jsapiTicketKey);
        return (expire != null ? expire : -2);
    }

    @Override
    public void expireJsapiTicket() {
        String lockKey = "expireJsapiTicketLockKey_" + this.jsapiTicketKey;
        lockService.executeByLock(lockKey, getLockTime(), new LockFunction() {
            @Override
            public void onLockSuccess() {
                redisTemplate.expire(jsapiTicketKey, 0, TimeUnit.SECONDS);
            }

            @Override
            public void onLockFailed() {
                throw new BizRuntimeException("expireJsapiTicket lock failed");
            }
        });
    }

    @Override
    public void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        String lockKey = "updateJsapiTicketLockKey_" + this.jsapiTicketKey;
        lockService.executeByLock(lockKey, getLockTime(), new LockFunction() {
            @Override
            public void onLockSuccess() {
                redisTemplate.opsForValue().set(jsapiTicketKey, jsapiTicket, expiresInSeconds - 200, TimeUnit.SECONDS);
            }

            @Override
            public void onLockFailed() {
                throw new BizRuntimeException("updateJsapiTicket lock failed");
            }
        });
    }

    @Override
    public String getCardApiTicket() {
        return this.redisTemplate.opsForValue().get(this.cardApiTicketKey);
    }

    @Override
    public boolean isCardApiTicketExpired() {
        Long expire = this.redisTemplate.getExpire(this.cardApiTicketKey);
        return (expire == null || expire < 2);
    }

    @Override
    public long getCardApiTicketExpiresTime() {
        Long expire = this.redisTemplate.getExpire(this.cardApiTicketKey);
        return (expire != null ? expire : -2);
    }

    @Override
    public void expireCardApiTicket() {
        String lockKey = "expireCardApiTicketLockKey_" + this.cardApiTicketKey;
        lockService.executeByLock(lockKey, getLockTime(), new LockFunction() {
            @Override
            public void onLockSuccess() {
                redisTemplate.expire(cardApiTicketKey, 0, TimeUnit.SECONDS);
            }

            @Override
            public void onLockFailed() {
                throw new BizRuntimeException("expireCardApiTicket lock failed");
            }
        });
    }

    @Override
    public void updateCardApiTicket(String cardApiTicket, int expiresInSeconds) {
        String lockKey = "updateCardApiTicketLockKey_" + this.cardApiTicketKey;
        lockService.executeByLock(lockKey, getLockTime(), new LockFunction() {
            @Override
            public void onLockSuccess() {
                redisTemplate.opsForValue()
                        .set(cardApiTicketKey, cardApiTicket, expiresInSeconds - 200, TimeUnit.SECONDS);
            }

            @Override
            public void onLockFailed() {
                throw new BizRuntimeException("updateCardApiTicket lock failed");
            }
        });
    }
}
