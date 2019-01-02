package org.cirno9.commons.cache;

import org.cirno9.commons.cache.loadBalance.ServerNode;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/2 21:16
 * @description: do nothing
 **/
public class DefaultCacheListener implements CacheListener {
    @Override
    public void onGet(ServerNode serverNode, Object o) {

    }

    @Override
    public void onSet(ServerNode serverNode, Object o, Object o2) {

    }

    @Override
    public void onHit(ServerNode serverNode, Object o, Object o2) {

    }

    @Override
    public void onMiss(ServerNode serverNode, Object o) {

    }
}
