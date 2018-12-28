package value;

import com.sun.istack.internal.NotNull;

import java.util.function.Function;

/**
 * @author: xuemin5
 * @date: Create at 2018/12/19 17:12
 * @description:
 *
 * 表达式校验
 *
 **/
public class Expressions {


    public static void checkExpression(boolean expression){
        if(!expression){
            throw new IllegalArgumentException();
        }
    }


    public static void checkExpression(boolean expression, @NotNull String errorMessage){
        if(!expression){
            throw new IllegalArgumentException(errorMessage);
        }
    }


}

