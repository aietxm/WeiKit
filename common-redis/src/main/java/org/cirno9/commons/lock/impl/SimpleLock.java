package org.cirno9.commons.lock.impl;

import org.cirno9.commons.lock.AbstractLock;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuemin5
 * @date: Create at 2018/12/28 15:37
 * @description: 简单互斥锁实现
 **/
public class SimpleLock extends AbstractLock {

    private Jedis jedis;

    private String lockKey;

    private String requestId;


    private static final Long SUCCESS = 1L;


    private static String LEASE_SCRIPT = " if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end;";

    private static String LOCK_SCRIPT = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then return redis.call('pexpire',KEYS[1],ARGV[2]) else return 0 end;";


    public SimpleLock(Jedis jedis, String lockKey, String requestId) {
        this.jedis = jedis;
        setLockey(lockKey);
        this.requestId = requestId;
    }


    public SimpleLock(Jedis jedis, String lockKey) {
        this.jedis = jedis;
        setLockey(lockKey);
        this.requestId = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getLockey() {
        return lockKey;
    }

    public void setLockey(String lockey) {
        this.lockKey = "RedisLockKey." + lockey;
    }


    private String getRequestId() {
        return requestId + Thread.currentThread().getId();
    }


    protected boolean acquire(long leaseTime, TimeUnit unit) throws InterruptedException {
        long time = unit.toMillis(leaseTime);
        if (time <= 0) {
            return false;
        }


        if (jedis != null) {
            List<String> argvs = new ArrayList<>(2);
            argvs.add(getRequestId());
            argvs.add(String.valueOf(time));
            Object re = jedis.eval(LOCK_SCRIPT, Collections.singletonList(lockKey), argvs);
            if (SUCCESS.equals(re)) {
                setLocked(true);
                return true;
            }
        }
        return false;


    }


    protected void lease() {


        if (jedis != null) {
            Object result = jedis.eval(LEASE_SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(getRequestId()));
            if (SUCCESS.equals(result)) {
                setLocked(false);
            }
        }


    }
}
