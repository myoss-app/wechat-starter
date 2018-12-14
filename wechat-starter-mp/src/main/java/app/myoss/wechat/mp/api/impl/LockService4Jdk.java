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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import app.myoss.cloud.cache.lock.LockService;
import app.myoss.cloud.core.exception.BizRuntimeException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * JDK Lock 接口使用 "缓存锁服务接口" 做代理
 *
 * @author Jerry.Chen
 * @since 2018年12月14日 下午12:24:38
 */
@Setter
@Getter
@RequiredArgsConstructor
public class LockService4Jdk implements Lock {
    /**
     * 锁的名字
     */
    @NonNull
    private String      key;
    /**
     * 缓存锁服务接口
     */
    @NonNull
    private LockService lockService;
    /**
     * 重试几次去获取锁
     */
    private int         tryLockTimes = 100;

    @Override
    public void lock() {
        boolean isGetLock = tryLock();
        if (!isGetLock) {
            // 进行多次重试获取锁
            for (int i = 1; i < tryLockTimes; i++) {
                LockService.sleep(10L);
                isGetLock = tryLock();
                if (isGetLock) {
                    break;
                }
            }
        }
        if (!isGetLock) {
            throw new BizRuntimeException("lock failed, key: " + key);
        }
    }

    @Override
    public void lockInterruptibly() {
        lockService.releaseLock(key);
    }

    @Override
    public boolean tryLock() {
        return lockService.getLock(key, 300);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        return lockService.getLock(key, Long.valueOf(unit.toMillis(time)).intValue());
    }

    @Override
    public void unlock() {
        lockService.releaseLock(key);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
