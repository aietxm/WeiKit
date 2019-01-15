package org.cirno9.commons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lixuhui on 2019/1/15.
 */
public abstract class CollectionUtils {

    /**
     * 判空
     * @param collection target to be judged
     * @return TRUE if this collection is empty
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判空
     * @param map target to be judged
     * @return TRUE if this map is empty
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判非空
     * @param collection target to be judged
     * @return TRUE if this collection is not empty
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    /**
     * 判非空
     * @param map target to be judged
     * @return TRUE if this map is not empty
     */
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    /**
     * 在过滤null对象的情况下add
     * collection为null 或item为null的情况下不做任何操作
     * @param collection 要添加的目标集合
     * @param item 添加的对象
     * @param <T>
     */
    public static <T> void addWithoutNull(Collection<T> collection, T item) {
        if (!isEmpty(collection) && item != null) {
            collection.add(item);
        }
    }

    /**
     * 获取第一个对象，如果没有内容，返回null
     * @param iterable
     * @param <T>
     * @return The first item in iterable, Null if iterable is empty
     */
    public static <T> T getFirst(Iterable<T> iterable) {
        if (iterable != null) {
            return iterable.iterator().next();
        }
        return null;
    }

    /**
     * 获取最后一个对象，如果没有内容，返回null
     * @param iterable
     * @param <T>
     * @return The last item in iterable, Null if iterable is empty
     */
    public static <T> T getLast(Iterable<T> iterable) {
        T result = null;
        if (iterable != null) {
            Iterator<T> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                result = iterator.next();
            }
        }
        return result;
    }

}
