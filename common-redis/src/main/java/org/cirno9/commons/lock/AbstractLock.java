package org.cirno9.commons.lock;

import org.cirno9.commons.value.ValueValidator;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author: xuemin5
 * @date: Create at 2018/12/27 21:04
 * @description:
 **/
public abstract class AbstractLock implements Lock, Serializable {

    private static long DEFAULT_LEASE_TIME = 60 * 1000;

    private Long leaseTime;

    private boolean locked = false;


    public void setLeaseTime(Long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public void lock() {
        try {
            lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean tryLock() {
        try {
            return tryLock(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }


    public void lockInterruptibly() throws InterruptedException {
        locked = acquire(ValueValidator.checkValue(leaseTime,DEFAULT_LEASE_TIME,ValueValidator.Validator.LONG_GT_ZERO_VA), TimeUnit.MILLISECONDS);
    }


    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long waitTime = unit.toMillis(time);
        if (waitTime <= 0) {
            return false;
        }

        while (true) {
            long current = System.currentTimeMillis();
            locked = acquire(ValueValidator.checkValue(leaseTime,DEFAULT_LEASE_TIME,ValueValidator.Validator.LONG_GT_ZERO_VA), TimeUnit.MILLISECONDS);
            if (locked) {
                return true;
            }
            if (waitTime <= 0) {
                return false;
            }
            long elapsed = System.currentTimeMillis() - current;
            waitTime -= elapsed;
        }

    }

    public void unlock() {
        lease();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    protected abstract boolean acquire(long leaseTime, TimeUnit unit) throws InterruptedException;

     protected abstract void lease();

     /*
     Do nothing!
      */
    public Condition newCondition() {
        return null;
    }
}
