package org.cirno9.commons;

import org.cirno9.commons.value.Expressions;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机内容生成工具
 * Created by lixuhui on 2019/1/17.
 */
public abstract class RandomUtils {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private static final String CHARS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARS_UPPER = CHARS_LOWER.toUpperCase();
    private static final String DIGITS = "0123456789";
    private static final byte[] HEX_LOWER = (DIGITS + "abcdef").getBytes();
    private static final byte[] HEX_UPPER = (DIGITS + "ABCDEF").getBytes();

    /**
     * 生成随机整数
     * @return 随机整数
     */
    public static int randomInt() {
        return RANDOM.nextInt();
    }

    /**
     * 生成大于等于0，小于bound的随机整数
     * @param bound 输出的最大值（不包含 excluded），需要正整数
     * @return [0, bound)
     */
    public static int randomInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * 生成大于等于fromClose，小于toOpen的随机整数
     * @param fromClose 输出的最小值（包含 included）
     * @param toOpen 输出的最大值（不包含 excluded），值需要大于fromClose
     * @return [from, to)
     */
    public static int randomInt(int fromClose, int toOpen) {
        int bound = toOpen - fromClose;
        Expressions.checkExpression(bound > 0, "bound must be positive");
        int randomInt = randomInt(bound);
        return randomInt + fromClose;
    }

    /**
     * 生成由大小写字母和数字组成的，长度为length的随机字符串
     * @param length 输出的长度，需要大于等于0
     * @return [a-zA-Z0-9]{length}
     */
    public static String randomString(int length) {
        return randomString(length, true, true, true);
    }

    /**
     * 生成定制组成部分的，长度为length的随机字符串
     * 小写字母、大写字母、数字中至少包含一种，最多三种
     * @param length 输出的长度
     * @param includeLowerCase 输出是否包含小写字母
     * @param includeUpperCase 输出是否包含大写字母
     * @param includeDigits 输出是否包含数字
     * @return 定制的随机字符串
     */
    public static String randomString(int length, boolean includeLowerCase, boolean includeUpperCase, boolean includeDigits) {
        Expressions.checkExpression(length >= 0);
        Expressions.checkExpression(includeLowerCase || includeUpperCase || includeDigits, "should at least include one type");

        String sourceString = "";
        if (includeLowerCase) {
            sourceString += CHARS_LOWER;
        }
        if (includeUpperCase) {
            sourceString += CHARS_UPPER;
        }
        if (includeDigits) {
            sourceString += DIGITS;
        }
        byte[] outputBytes = new byte[length];
        int bound = sourceString.length();
        if (length > 1024) {
            // 如果需要较长结果，转成数组，减少越界判断
            byte[] sourceBytes = sourceString.getBytes();
            for (int i = 0; i < length; i++) {
                outputBytes[i] = sourceBytes[randomInt(bound)];
            }
        } else {
            // 短结果不转换了
            for (int i = 0; i < length; i++) {
                outputBytes[i] = (byte) sourceString.charAt(randomInt(bound));
            }

        }
        return new String(outputBytes);
    }

    /**
     * 生成由[0-9a-f]组成，长度为length的16进制随机字符串
     * @param length 输出的长度
     * @param upperCase 是否使用大写字母
     * @return [0-9a-f]{length}
     */
    public static String randomHexString(int length, boolean upperCase) {
        Expressions.checkExpression(length >= 0);

        int bound = 16;
        byte[] outputBytes = new byte[length];

        byte[] sourceBytes;
        if (upperCase) {
            sourceBytes = HEX_UPPER;
        } else {
            sourceBytes = HEX_LOWER;
        }
        for (int i = 0; i < length; i++) {
            outputBytes[i] = sourceBytes[randomInt(bound)];
        }
        return new String(outputBytes);
    }

}
