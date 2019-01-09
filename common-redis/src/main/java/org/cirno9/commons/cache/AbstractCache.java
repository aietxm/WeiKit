package org.cirno9.commons.cache;

import org.cirno9.commons.cache.loadBalance.Selector;
import org.cirno9.commons.cache.loadBalance.ServerNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/2 20:14
 * @description:
 **/
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    private Selector<ServerNode> selector;

    private List<CacheListener<K, V>> listenerList = new ArrayList<>();


    public void register(CacheListener<K, V> listener) {
        listenerList.add(listener);
    }

    public void unRegister(CacheListener<K, V> listener) {
        listenerList.remove(listener);
    }


    @Override
    public V get(K k, V defaultValue) {
        ServerNode t = selector.select(k);
        V re;
        if (t != null) {
            re = doGet(t, k);
            listenerList.forEach(item -> item.onGet(t, k));
            if (re != null) {
                listenerList.forEach(item -> item.onHit(t, k, re));
                return re;
            }
        }
        listenerList.forEach(item -> item.onMiss(t, k));
        return null;
    }

    @Override
    public void set(K k, V v) {
        ServerNode t = selector.select(k);

        if (t != null) {
            doSet(t, k, v);
            listenerList.forEach(item -> item.onSet(t, k, v));
        }
    }


    abstract boolean doSet(ServerNode serverNode, K k, V v);

    abstract V doGet(ServerNode serverNode, K k);
}
