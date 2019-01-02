package org.cirno9.commons.digest;

import java.security.MessageDigest;

/**
 * @author: xuemin5
 * @date: Create at 2019/1/2 20:32
 * @description:
 **/
public class Md5Hash implements HashHelper {

    private static String ALGORITHM = "MD5";

    @Override
    public byte[] hash(Object object) {
        return md5(String.valueOf(object));
    }

    @Override
    public int outLength() {
        return 128;
    }


    public static byte[] md5(String s){
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance(ALGORITHM);
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            return md;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
