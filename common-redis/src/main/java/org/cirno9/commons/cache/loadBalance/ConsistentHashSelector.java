package org.cirno9.commons.cache.loadBalance;


import org.cirno9.commons.digest.HashHelper;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/1 16:22
 * @description: 一致性hash
 **/
public final class ConsistentHashSelector<T> implements Selector<ServerNode<T>> {

    /*
    Vnode num
     */
    private final int replicaNum;

    /*
    Use treemap to store vnode
     */
    private final SortedMap<Long, ServerNode<T>> virtualNodes = new TreeMap<>();

    /*
    Hash implement
     */
    private final HashHelper hashHelper;


    public ConsistentHashSelector(int replicaNum, Collection<ServerNode<T>> servers, HashHelper hashHelper)  {
        this.replicaNum = replicaNum;
        this.hashHelper = hashHelper;
        int hashLen = hashHelper.outLength()/32;
        for(ServerNode<T> t: servers){
            for(int i=0;i<replicaNum/hashLen;i++){
                byte[] digest= hashHelper.hash(t.toString()+i);
                for(int h=0;h<hashLen;h++){
                    long m = inerHash(digest,h);
                    virtualNodes.put(m,t);
                }

            }
        }

    }

    private long inerHash(byte[] digest, int number) {
        return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                | (digest[number * 4] & 0xFF))
                & 0xFFFFFFFFL;
    }

    public ServerNode<T> select(Object key){
            byte[] digest = hashHelper.hash(key);
            return inerSelect(inerHash(digest,0));
    }

    private ServerNode<T> inerSelect(long m){
        ServerNode<T> serverNode;
        Long key = m;
        if(!virtualNodes.containsKey(key)){
            SortedMap<Long,ServerNode<T>> tail = virtualNodes.tailMap(key);
            if(tail.isEmpty()){
                key = virtualNodes.firstKey();
            }else{
                key = tail.firstKey();
            }
        }
        serverNode = virtualNodes.get(key);
        return serverNode;
    }

    public int getSize(){
        return virtualNodes.size();
    }
}
