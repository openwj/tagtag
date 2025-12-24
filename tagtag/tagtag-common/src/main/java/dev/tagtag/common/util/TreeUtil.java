package dev.tagtag.common.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 通用树结构工具类
 */
public class TreeUtil {

    /**
     * 将扁平列表转换为树结构
     * 
     * @param list 扁平列表
     * @param getId 获取节点ID的函数
     * @param getParentId 获取父节点ID的函数
     * @param setChildren 设置子节点的函数
     * @param <T> 节点类型
     * @return 树形结构列表
     */
    public static <T> List<T> buildTree(List<T> list, 
                                        Function<T, Long> getId, 
                                        Function<T, Long> getParentId, 
                                        BiConsumer<T, List<T>> setChildren) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 构建ID到节点的映射
        Map<Long, T> nodeMap = list.stream()
                .collect(Collectors.toMap(getId, node -> node));

        // 构建树形结构
        List<T> rootNodes = new ArrayList<>();
        Map<Long, List<T>> childrenMap = new HashMap<>();
        
        // 1. 首先构建父ID到子节点的映射
        for (T node : list) {
            Long parentId = getParentId.apply(node);
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }
        
        // 2. 然后为每个节点设置子节点
        for (T node : list) {
            Long id = getId.apply(node);
            List<T> children = childrenMap.get(id);
            if (children != null && !children.isEmpty()) {
                setChildren.accept(node, children);
            }
        }
        
        // 3. 最后收集根节点
        for (T node : list) {
            Long parentId = getParentId.apply(node);
            if (parentId == null || parentId == 0L || !nodeMap.containsKey(parentId)) {
                // 根节点或父节点不存在，作为根节点处理
                rootNodes.add(node);
            }
        }

        return rootNodes;
    }
    
    /**
     * 将扁平列表转换为树结构，并支持排序
     * 
     * @param list 扁平列表
     * @param getId 获取节点ID的函数
     * @param getParentId 获取父节点ID的函数
     * @param getChildren 获取子节点的函数
     * @param setChildren 设置子节点的函数
     * @param comparator 排序比较器
     * @param <T> 节点类型
     * @return 树形结构列表
     */
    public static <T> List<T> buildTree(List<T> list, 
                                        Function<T, Long> getId, 
                                        Function<T, Long> getParentId, 
                                        Function<T, List<T>> getChildren, 
                                        BiConsumer<T, List<T>> setChildren, 
                                        Comparator<T> comparator) {
        List<T> tree = buildTree(list, getId, getParentId, setChildren);
        if (comparator != null) {
            sortTree(tree, comparator, getChildren, setChildren);
        }
        return tree;
    }

    /**
     * 递归排序树节点
     * 
     * @param nodes 节点列表
     * @param comparator 比较器
     * @param getChildren 获取子节点的函数
     * @param setChildren 设置子节点的函数
     * @param <T> 节点类型
     */
    public static <T> void sortTree(List<T> nodes, 
                                   Comparator<? super T> comparator, 
                                   Function<T, List<T>> getChildren, 
                                   BiConsumer<T, List<T>> setChildren) {
        if (nodes == null || nodes.isEmpty() || comparator == null) {
            return;
        }

        // 排序当前层
        nodes.sort(comparator);

        // 递归排序子节点
        for (T node : nodes) {
            List<T> children = getChildren.apply(node);
            if (children != null && !children.isEmpty()) {
                sortTree(children, comparator, getChildren, setChildren);
            }
        }
    }
}
