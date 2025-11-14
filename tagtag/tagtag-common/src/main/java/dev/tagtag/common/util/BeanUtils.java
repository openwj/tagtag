package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Bean属性拷贝工具
 */
@UtilityClass
public class BeanUtils {

    /**
     * 内部泛型拷贝实现（支持控制是否仅拷贝非空属性）
     * @param source 源对象
     * @param target 目标对象
     * @param nonNullOnly 是否仅拷贝非空属性
     * @param <S> 源类型
     * @param <T> 目标类型
     */
    private static <S, T> void doCopy(S source, T target, boolean nonNullOnly) {
        if (source == null || target == null) {
            return;
        }
        try {
            PropertyDescriptor[] srcProps = Introspector.getBeanInfo(source.getClass(), Object.class).getPropertyDescriptors();
            PropertyDescriptor[] tgtProps = Introspector.getBeanInfo(target.getClass(), Object.class).getPropertyDescriptors();
            java.util.Map<String, PropertyDescriptor> tgtMap = new java.util.HashMap<>();
            for (PropertyDescriptor pd : tgtProps) {
                tgtMap.put(pd.getName(), pd);
            }
            for (PropertyDescriptor sp : srcProps) {
                PropertyDescriptor tp = tgtMap.get(sp.getName());
                if (tp == null) continue;
                Method read = sp.getReadMethod();
                Method write = tp.getWriteMethod();
                if (read == null || write == null) continue;
                Object val = read.invoke(source);
                if (!nonNullOnly || val != null) {
                    write.invoke(target, val);
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException("Bean 属性解析失败", e);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Bean 属性拷贝失败", e);
        }
    }

    /**
     * 泛型重载：拷贝到已存在目标对象并返回目标（便于链式使用）
     * @param source 源对象
     * @param target 目标对象
     * @param <S> 源类型
     * @param <T> 目标类型
     * @return 目标对象
     */
    /**
     * 将源对象同名属性拷贝到目标对象（浅拷贝，允许覆盖 null）
     * @param source 源对象
     * @param target 目标对象
     * @param <S> 源类型
     * @param <T> 目标类型
     * @return 目标对象
     */
    public static <S, T> T copyTo(S source, T target) {
        doCopy(source, target, false);
        return target;
    }

    /**
     * 拷贝非空属性（源属性为 null 时跳过）并返回目标对象
     * @param source 源对象
     * @param target 目标对象
     * @param <S> 源类型
     * @param <T> 目标类型
     * @return 目标对象
     */
    public static <S, T> T copyNonNullTo(S source, T target) {
        doCopy(source, target, true);
        return target;
    }

    /**
     * 使用 Supplier 创建目标对象并拷贝（避免反射构造）
     * @param source 源对象
     * @param supplier 目标对象提供者
     * @param <S> 源类型
     * @param <T> 目标类型
     * @return 新对象
     */
    public static <S, T> T copyToNew(S source, Supplier<T> supplier) {
        if (source == null || supplier == null) {
            return null;
        }
        T target = supplier.get();
        doCopy(source, target, false);
        return target;
    }


    /**
     * 使用 Supplier 批量创建并拷贝列表元素（避免反射构造）
     * @param sources 源列表
     * @param supplier 目标对象提供者
     * @param <S> 源类型
     * @param <T> 目标类型
     * @return 目标列表
     */
    public static <S, T> List<T> copyList(Collection<S> sources, Supplier<T> supplier) {
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(sources.size());
        for (S s : sources) {
            T t = supplier.get();
            doCopy(s, t, false);
            result.add(t);
        }
        return result;
    }
}
