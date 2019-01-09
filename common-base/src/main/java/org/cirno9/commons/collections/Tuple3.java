package org.cirno9.commons.collections;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/9 17:47
 * @description: 3元组结构
 **/
public class Tuple3<A,B,C> extends Tuple<A,B> {

    private  C field;
    public Tuple3(A a, B b, C c) {
        super(a, b);
        this.field = c;
    }

    public C getField() {
        return field;
    }

    public void setField(C field) {
        this.field = field;
    }
}
