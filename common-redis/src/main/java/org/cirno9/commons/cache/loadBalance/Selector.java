package org.cirno9.commons.cache.loadBalance;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/1 17:23
 * @description:
 **/
public interface Selector<T> {
    T select(Object o);
}
