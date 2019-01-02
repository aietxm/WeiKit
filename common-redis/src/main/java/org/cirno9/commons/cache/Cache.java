package org.cirno9.commons.cache;


/**
 * @author: xuemin5
 * @date: Create at 2019/1/2 19:54
 * @description:
 **/
public interface Cache<K,V>  {

    V get(K k, V defaultValue);

    void set(K k, V v);


}
