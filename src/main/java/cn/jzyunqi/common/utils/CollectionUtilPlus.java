package cn.jzyunqi.common.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public final class CollectionUtilPlus {

    /**
     * 私有构造函数,防止误用
     */
    private CollectionUtilPlus() {

    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param <T>        类型
     * @return true：为空 false：不为空
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 集合
     * @param <T>        类型
     * @return true:不为空 false:为空
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return CollectionUtils.isNotEmpty(collection);
    }

    /**
     * 判断是否为一个非空的数组
     *
     * @param array 数组
     * @param <T>   类型
     * @return true:不为空 false:为空
     */
    public static <T> boolean isNotNullOrEmptyArray(T[] array) {
        return !isEmptyArray(array);
    }

    /**
     * 判断是否为一个空数组
     *
     * @param array 数组
     * @param <T>   类型
     * @return true:空 false:非空
     */
    public static <T> boolean isEmptyArray(T[] array) {
        return CollectionUtils.sizeIsEmpty(array);
    }

    /**
     * 合并所有集合
     *
     * @param lists 要合并的集合
     * @return List
     */
    public static <T> List<T> mergeAll(List<T>... lists) {
        List<T> mergedList = new ArrayList<T>();
        for (int i = 0, len = lists.length; i < len; i++) {
            List<T> list = lists[i];
            if (list != null && !list.isEmpty()) {
                for (int j = 0, lenj = list.size(); j < lenj; j++) {
                    T obj = list.get(j);
                    if (obj != null) {
                        mergedList.add(obj);
                    }
                }
            }
        }
        return mergedList;
    }

    /**
     * 用数组创建一个ArrayList
     *
     * @param objs 元素
     * @param <T>  元素的类型
     * @return ArrayList
     */
    public static <T> List<T> asList(T... objs) {
        if (objs == null) {
            return Collections.EMPTY_LIST;
        }
        List<T> list = new ArrayList<>();
        Collections.addAll(list, objs);
        return list;
    }

    /**
     * 数组包含
     *
     * @param array 数组
     * @param val   被包含值
     * @param <T>   类型
     * @return 是否包含
     */
    public static <T> boolean contains(T[] array, T val) {
        for (T t : array) {
            if (val.equals(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 连接数组
     *
     * @param words     数组
     * @param seperator 分隔符
     * @return 连接后的字符串
     */
    public static String join(Object[] words, String seperator) {
        StringBuilder sb = new StringBuilder();
        if (words != null) {
            for (int i = 0; i < words.length; i++) {
                sb.append(words[i]);
                if (i < words.length - 1) {
                    sb.append(seperator);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 连接集合
     *
     * @param collection 集合
     * @param seperator  分隔符
     * @return 连接后的字符串
     */
    public static String join(Collection<?> collection, String seperator) {
        Object[] objs = new Object[collection.size()];
        collection.toArray(objs);
        return join(objs, seperator);
    }

    /**
     * 复制一个数组
     *
     * @param sourceArray 要复制的目标数组
     * @param <T>         要复制的数组泛型类型
     * @return 新的数组
     */
    public static <T> T[] copy(T[] sourceArray) {
        if (sourceArray == null) {
            return null;
        }
        return Arrays.copyOf(sourceArray, sourceArray.length);
    }

    /**
     * 判断集合中是否存在指定元素
     *
     * @param input     集合
     * @param predicate 对比
     * @param <T>       T
     * @return true:存在 false:不存在
     */
    public static <T> boolean exists(Iterable<T> input, Predicate<? super T> predicate) {
        return predicate != null && IterableUtils.matchesAny(input, predicate);
    }

    /**
     * 反转list中元素顺序
     *
     * @param list List
     */
    public static void reverse(List<?> list) {
        Collections.reverse(list);
    }

    /**
     * 集合排序
     *
     * @param list 集合
     * @param c    Comparator
     * @param <T>  类型
     */
    public static <T> void sort(List<T> list, Comparator<? super T> c) {
        if (isNotEmpty(list)) {
            list.sort(c);
        }
    }

    /**
     * 去除List中的重复元素
     *
     * @param collection 源集合
     * @param <T>        类型
     * @return 去重后的集合
     */
    public static <T> List<T> removeRepeat(Collection<T> collection) {
        List<T> newList = new ArrayList<T>();
        if (isNotEmpty(collection)) {
            for (T t : collection) {
                if (!newList.contains(t)) {
                    newList.add(t);
                }
            }
        }
        return newList;
    }
}
