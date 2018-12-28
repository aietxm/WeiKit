package value;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author: xuemin5
 * @date: Create at 2018/12/19 17:25
 * @description: 值检查器
 *
 *
 * 可以自定义检查项，默认提供常用的检查器
 *
 * 代码参考唯评会Vjtools:valueValidator
 *
 * STRING_EMPTY_VA : 字符串为空
 *
 * OBJECT_NONULL_VA : 对象不为空
 *
 * INTEGER_GT_ZERO_VA : Integer 不为空且大于0
 *
 * LONG_GT_ZERO_VA : LONG 不为空且大于0
 *
 **/
public class ValueValidator {

    public interface Validator<T> {

        boolean valid(T value);

        Validator<String> STRING_NOEMPTY_VA = value -> value != null && !value.isEmpty();

        Validator<Object> OBJECT_NONULL_VA = Objects::nonNull;

        Validator<Integer> INTEGER_GT_ZERO_VA = value -> value != null && value > 0;

        Validator<Long> LONG_GT_ZERO_VA = value -> value != null && value > 0;

    }

    /**
     * 值检查
     * @param value
     * @param defaultValue
     * @param validator
     * @param <T>
     * @return
     */

    public static <T> T checkValue(T value, T defaultValue, Validator<T> validator) {
        if (validator.valid(value)) {
            return value;
        }

        return defaultValue;
    }

    /**
     * 单参数函数结果检查
     * @param function
     * @param args
     * @param defaultValue
     * @param validator
     * @param <T>
     * @return
     */
    public static <T> T checkAndGet(Function<Object, T> function, Object args, T defaultValue, Validator<T> validator) {
        return checkValue(function.apply(args), defaultValue, validator);
    }

    /**
     * 无参函数结果检查
     * @param function
     * @param defaultValue
     * @param validator
     * @param <T>
     * @return
     */
    public static <T> T checkAndGet(Function<Object, T> function, T defaultValue, Validator<T> validator) {
        return checkAndGet(function, null, defaultValue, validator);
    }

}


