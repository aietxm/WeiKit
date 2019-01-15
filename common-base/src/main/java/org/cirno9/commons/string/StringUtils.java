package org.cirno9.commons.string;

/**
 * Created by lixuhui on 2019/1/15.
 */
public abstract class StringUtils {

    /**
     * 判空
     * @param string target to be judged
     * @return TRUE if this string is empty
     */
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * 判非空
     * @param string target to be judged
     * @return TRUE if this string is not empty
     */
    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    /**
     * 判空
     * @param stringBuilder target to be judged
     * @return TRUE if this stringBuilder is empty
     */
    public static boolean isEmpty(StringBuilder stringBuilder) {
        return stringBuilder == null || stringBuilder.length() == 0;
    }

    /**
     * 判非空
     * @param stringBuilder target to be judged
     * @return TRUE if this stringBuilder is not empty
     */
    public static boolean isNotEmpty(StringBuilder stringBuilder) {
        return !isEmpty(stringBuilder);
    }


}
