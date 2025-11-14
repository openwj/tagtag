package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 集合工具
 */
@UtilityClass
public class CollectionUtils {

    /**
     * 集合是否为空（null 或 size==0）
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 集合是否非空
     * @param collection 集合
     * @return 是否非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 空集合返回不可变空列表，非空原样返回
     * @param list 列表
     * @param <T> 元素类型
     * @return 安全列表
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 计算安全长度（null 返回 0）
     * @param collection 集合
     * @return 长度
     */
    public static int nullSafeSize(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * 将 Iterable 转换为列表
     * @param iterable 可迭代
     * @param <T> 元素类型
     * @return 列表
     */
    public static <T> List<T> toList(Iterable<T> iterable) {
        if (iterable == null) return Collections.emptyList();
        List<T> result = new ArrayList<>();
        for (T t : iterable) {
            result.add(t);
        }
        return result;
    }

    /**
     * 映射列表元素
     * @param list 源列表
     * @param mapper 映射函数
     * @param <T> 源类型
     * @param <R> 目标类型
     * @return 映射后的列表
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 过滤列表元素
     * @param list 源列表
     * @param predicate 过滤条件
     * @param <T> 元素类型
     * @return 过滤后的列表
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 将列表按固定大小分片（最后一片可能不足）
     * @param list 源列表
     * @param size 片大小（>=1）
     * @param <T> 元素类型
     * @return 分片列表
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null || list.isEmpty() || size <= 0) {
            return Collections.emptyList();
        }
        int total = list.size();
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < total; i += size) {
            result.add(new ArrayList<>(list.subList(i, Math.min(i + size, total))));
        }
        return result;
    }
}
