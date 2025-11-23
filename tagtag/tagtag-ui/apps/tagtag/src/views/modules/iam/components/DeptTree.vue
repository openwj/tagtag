<script lang="ts" setup>
import type { DataNode } from 'ant-design-vue/es/tree';

import { ref, onMounted, watch } from 'vue';
import { Tree, Input, Tooltip, Button } from 'ant-design-vue';
import { Icon } from '@iconify/vue';

import { getDeptTree } from '#/api/modules/iam/dept';

interface Props {
  selectedId?: number;
  status?: number;
}

interface Emits {
  (e: 'update:selectedId', value: number | undefined): void;
  (e: 'change', value: number | undefined): void;
}

const props = withDefaults(defineProps<Props>(), {
  selectedId: undefined,
  status: undefined,
});

const emit = defineEmits<Emits>();

// 树形数据与字段映射
const rawTreeData = ref<DataNode[]>([]);
const treeData = ref<DataNode[]>([]);
const fieldNames = { children: 'children', title: 'name', key: 'id' } as const;
const selectedKeys = ref<string[]>(['__ALL__']);
const expandedKeys = ref<string[]>([]);
const searchText = ref('');

/**
 * 加载部门树并注入“全部”根节点
 */
async function loadDeptTree() {
  const resp = await getDeptTree(props.status ? { status: props.status } : {});
  const children = Array.isArray(resp) ? resp : [];
  const root = [{ id: '__ALL__', name: '全部', children } as any];
  rawTreeData.value = root as unknown as DataNode[];
  treeData.value = applyFilter(rawTreeData.value, searchText.value);
  // 初始化选中态：优先使用外部传入的 selectedId
  if (typeof props.selectedId === 'number') {
    selectedKeys.value = [String(props.selectedId)];
  } else {
    selectedKeys.value = ['__ALL__'];
  }
}

/**
 * 处理树的选择事件并向外输出选中部门ID
 * @param payload Antd Tree select 事件入参：数组或对象
 */
function handleSelect(payload: any) {
  const keys: Array<string | number> = Array.isArray(payload)
    ? payload
    : Array.isArray(payload?.selectedKeys)
      ? payload.selectedKeys
      : [];
  const key = keys[0];
  selectedKeys.value = keys.map((k) => String(k));
  const selectedId = key === '__ALL__' || key === undefined ? undefined : Number(key);
  emit('update:selectedId', selectedId);
  emit('change', selectedId);
}

/**
 * 获取所有节点 key（深度优先）
 */
function getAllKeys(nodes: DataNode[]): string[] {
  const keys: string[] = [];
  const traverse = (arr: DataNode[]) => {
    arr.forEach((n) => {
      keys.push(String((n as any).key ?? (n as any).id));
      if (n.children && n.children.length) traverse(n.children as DataNode[]);
    });
  };
  traverse(nodes);
  return keys;
}

/**
 * 展开全部节点
 */
function expandAll() {
  expandedKeys.value = getAllKeys(treeData.value);
}

/**
 * 收起全部节点
 */
function collapseAll() {
  expandedKeys.value = [];
}

/**
 * 过滤树数据（按部门名称模糊匹配）
 */
function applyFilter(source: DataNode[], keyword: string): DataNode[] {
  const k = String(keyword || '').trim().toLowerCase();
  if (!k) return source;
  const match = (n: any) => String(n.name || n.title).toLowerCase().includes(k);
  const loop = (nodes: DataNode[]): DataNode[] => {
    const res: DataNode[] = [];
    for (const node of nodes) {
      const children = node.children ? loop(node.children as DataNode[]) : undefined;
      if (match(node) || (children && children.length)) {
        res.push({ ...(node as any), children } as DataNode);
      }
    }
    return res;
  };
  return loop(source);
}

// 外部选中值变化时同步 selectedKeys
watch(
  () => props.selectedId,
  (val) => {
    selectedKeys.value = typeof val === 'number' ? [String(val)] : ['__ALL__'];
  },
);

onMounted(() => {
  loadDeptTree();
});

// 搜索输入监听
watch(
  () => searchText.value,
  (val) => {
    treeData.value = applyFilter(rawTreeData.value, val);
  },
);
</script>

<template>
  <div>
    <div class="mb-3 flex items-center gap-2">
      <Input v-model:value="searchText" size="small" placeholder="搜索部门" class="flex-1" />
      <Tooltip title="展开全部">
        <Button size="small" @click="expandAll">
          <Icon icon="lucide:chevrons-down" />
        </Button>
      </Tooltip>
      <Tooltip title="收起全部">
        <Button size="small" @click="collapseAll">
          <Icon icon="lucide:chevrons-up" />
        </Button>
      </Tooltip>
    </div>
    <Tree
      v-model:selected-keys="selectedKeys"
      v-model:expanded-keys="expandedKeys"
      :field-names="fieldNames"
      :tree-data="treeData"
      :selectable="true"
      @select="handleSelect"
    />
  </div>
</template>
