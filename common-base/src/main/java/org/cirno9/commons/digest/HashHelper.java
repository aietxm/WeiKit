package org.cirno9.commons.digest;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/2 20:38
 * @description:
 **/
public interface HashHelper {
    byte[] hash(Object object);
    int outLength();
}
