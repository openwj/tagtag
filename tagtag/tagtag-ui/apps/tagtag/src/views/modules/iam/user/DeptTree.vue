<script lang="ts" setup>
import type { DataNode } from 'ant-design-vue/es/tree';

import { computed, onMounted, ref } from 'vue';

import {
  Button as AButton,
  Empty as AEmpty,
  Input as AInput,
  Tree as ATree,
} from 'ant-design-vue';

import { getDeptTree } from '#/api/modules/iam/dept';

// 定义部门数据接口
interface DeptItem {
  id: string;
  parentId: string;
  name: string;
  code?: string;
  remark?: string;
  leader?: string;
  phone?: string;
  sort?: number;
  status?: number;
  createTime?: string;
  updateTime?: string;
  children: DeptItem[];
}

// API直接返回DeptItem[]数组，不需要ApiResponse接口

const emit = defineEmits(['select']);

// 原始数据和处理后的树数据
const originalData = ref<DeptItem[]>([]);
const treeData = ref<DataNode[]>([]);
const searchValue = ref('');
const expandedKeys = ref<string[]>([]);
const autoExpandParent = ref(true);

/**
 * 获取所有节点的key
 * @param data 树数据
 * @returns 所有节点key数组
 */
function getAllKeys(data: DataNode[]): string[] {
  const keys: string[] = [];

  function traverse(nodes: DataNode[]) {
    if (!nodes) return;
    nodes.forEach((node) => {
      keys.push(String(node.key));
      if (node.children) {
        traverse(node.children);
      }
    });
  }

  traverse(data);
  return keys;
}

/**
 * 搜索匹配的节点
 * @param data 树数据
 * @param searchKey 搜索关键字
 * @returns 匹配的节点key数组
 */
function getMatchedKeys(data: DeptItem[], searchKey: string): string[] {
  const keys: string[] = [];

  function traverse(nodes: DeptItem[]) {
    if (!nodes) return;
    nodes.forEach((node) => {
      if (node.name && node.name.toLowerCase().includes(searchKey.toLowerCase())) {
        keys.push(node.id);
      }
      if (node.children) {
        traverse(node.children);
      }
    });
  }

  traverse(data);
  return keys;
}

/**
 * 获取父节点路径
 * @param data 树数据
 * @param targetKey 目标节点key
 * @returns 父节点路径数组
 */
function getParentKeys(data: DeptItem[], targetKey: string): string[] {
  const parentKeys: string[] = [];

  function findParent(
    nodes: DeptItem[],
    key: string,
    parents: string[],
  ): boolean {
    if (!nodes) return false;

    for (const node of nodes) {
      if (node.id === key) {
        parentKeys.push(...parents);
        return true;
      }
      if (
        node.children &&
        findParent(node.children, key, [...parents, node.id])
      ) {
        return true;
      }
    }
    return false;
  }

  findParent(data, targetKey, []);
  return parentKeys;
}

// 计算搜索后的展开节点
const computedExpandedKeys = computed(() => {
  if (!searchValue.value) {
    return expandedKeys.value;
  }

  const matchedKeys = getMatchedKeys(originalData.value, searchValue.value);
  const allParentKeys = new Set<string>();

  matchedKeys.forEach((key) => {
    const parentKeys = getParentKeys(originalData.value, key);
    parentKeys.forEach((parentKey) => allParentKeys.add(parentKey));
  });

  return [...allParentKeys];
});

/**
 * 转换API数据为树组件所需的格式
 * @param data API返回的原始数据
 * @returns 转换后的树数据
 */
function transformData(data: DeptItem[]): DataNode[] {
  if (!data || !Array.isArray(data)) {
    return [];
  }

  return data.map((item) => {
    const transformedItem: DataNode = {
      key: item.id,
      title: item.name,
      // 保留原始数据以便在模板中使用
      ...item,
      // 递归转换子节点
      children: item.children ? transformData(item.children) : undefined,
    };

    return transformedItem;
  });
}

/**
 * 获取部门树数据
 */
async function fetch() {
  const response = await getDeptTree({});

  // API直接返回DeptItem[]数组
  const rawData: DeptItem[] = response as DeptItem[];

  // 保存原始数据
  originalData.value = rawData;

  // 转换数据格式
  const transformedData = transformData(rawData);
  treeData.value = transformedData;

  // 默认展开所有节点
  expandedKeys.value = getAllKeys(transformedData);
}

/**
 * 处理节点选择
 * @param keys 选中的节点key数组
 */
function handleSelect(keys: any[]) {
  const stringKeys = keys.map(key => String(key));
  emit('select', stringKeys[0]);
}

/**
 * 处理节点展开/收起
 * @param keys 展开的节点key数组
 */
function handleExpand(keys: any[]) {
  expandedKeys.value = keys.map(key => String(key));
  autoExpandParent.value = false;
}

/**
 * 全部展开
 */
function expandAll() {
  expandedKeys.value = getAllKeys(transformData(originalData.value));
}

/**
 * 全部收起
 */
function collapseAll() {
  expandedKeys.value = [];
}

/**
 * 清空搜索
 */
function clearSearch() {
  searchValue.value = '';
}

onMounted(() => {
  fetch();
});
</script>

<template>
  <div class="bg-card flex h-full flex-col rounded-md p-3">
    <!-- 标题 -->
    <div class="mb-3 pl-1 text-[1rem] font-medium">部门列表</div>

    <!-- 搜索框 -->
    <div class="mb-3">
      <AInput
        v-model:value="searchValue"
        placeholder="搜索部门名称"
        allow-clear
        class="w-full"
        @clear="clearSearch"
      >
        <template #prefix>
          <span class="icon-[material-symbols--search] text-gray-400"></span>
        </template>
      </AInput>
    </div>

    <!-- 操作按钮 -->
    <div class="mb-3 flex gap-2">
      <AButton
        size="small"
        type="text"
        @click="expandAll"
        class="flex items-center gap-1 px-2"
      >
        <span class="icon-[material-symbols--expand-more]"></span>
        全部展开
      </AButton>
      <AButton
        size="small"
        type="text"
        @click="collapseAll"
        class="flex items-center gap-1 px-2"
      >
        <span class="icon-[material-symbols--expand-less]"></span>
        全部收起
      </AButton>
    </div>

    <hr class="mb-3" />

    <!-- 部门树 -->
    <div class="flex-1 overflow-auto">
      <ATree
        v-if="treeData && treeData.length > 0"
        :tree-data="treeData"
        :expanded-keys="computedExpandedKeys"
        :auto-expand-parent="autoExpandParent"
        show-line
        class="py-2"
        @select="handleSelect"
        @expand="handleExpand"
      >
        <!-- 自定义节点标题，支持搜索高亮 -->
        <template #title="nodeData">
          <!-- 从节点数据中获取部门名称 -->
          <span
            v-if="
              searchValue &&
              nodeData.title &&
              nodeData.title
                .toLowerCase()
                .includes(searchValue.toLowerCase())
            "
          >
            <template
              v-for="(part, index) in nodeData.title.split(
                new RegExp(`(${searchValue})`, 'gi'),
              )"
              :key="index"
            >
              <span
                v-if="part.toLowerCase() === searchValue.toLowerCase()"
                class="rounded bg-yellow-200 px-1 text-yellow-800"
              >
                {{ part }}
              </span>
              <span v-else>{{ part }}</span>
            </template>
          </span>
          <span v-else>{{ nodeData.title || '未知部门' }}</span>
        </template>
      </ATree>

      <!-- 空状态 -->
      <AEmpty
        v-if="!treeData || treeData.length === 0"
        description="暂无部门数据"
      />
    </div>
  </div>
</template>
