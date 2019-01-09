package org.cirno9.commons.collections;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/9 17:43
 * @description: 2元组结构
 **/
public class Tuple <A,B>{
    private  A key;
    private  B value;

    public Tuple(A a,  B b){
        this.key = a;
        this.value = b;
    }

    public A getKey() {
        return key;
    }

    public void setKey(A key) {
        this.key = key;
    }

    public void setValue(B value) {
        this.value = value;
    }

    public B getValue() {
        return value;
    }


}
