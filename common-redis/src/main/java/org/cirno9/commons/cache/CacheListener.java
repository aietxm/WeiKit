package org.cirno9.commons.cache;

import org.cirno9.commons.cache.loadBalance.ServerNode;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/2 21:02
 * @description:
 **/
public interface CacheListener<K,V> {

    void onGet(ServerNode serverNode, K k );

    void onSet(ServerNode serverNode, K k , V v);

    void onHit(ServerNode serverNode, K k , V v);

    void onMiss(ServerNode serverNode, K k);


}
