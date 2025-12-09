<script lang="ts" setup>
import type { DataNode } from 'ant-design-vue/es/tree';

import { ref, watch } from 'vue';

import { Icon } from '@iconify/vue';
import {
  Button,
  Dropdown,
  Input,
  Menu,
  Spin,
  Tag,
  Tooltip,
  Tree,
} from 'ant-design-vue';

import { getMenuTree } from '#/api/modules/iam/menu';

interface Props {
  roleId?: string;
  value?: string[];
}

interface Emits {
  (e: 'update:value', value: string[]): void;
  (e: 'change', value: string[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  roleId: undefined,
  value: () => [],
});

const emit = defineEmits<Emits>();

// 树形数据
const treeData = ref<DataNode[]>([]);
const rawTreeData = ref<DataNode[]>([]);
const loading = ref(false);
const checkedKeys = ref<string[]>([]);
const expandedKeys = ref<string[]>([]);
// 控制 Tree 的 checkStrictly 属性
const checkStrictly = ref(false);
const storageKeyCheckStrictly = 'permissionTree.checkStrictly';
const searchText = ref('');

/**
 * 切换父子联动/独立，并持久化到本地存储
 */
const toggleCheckStrictly = () => {
  checkStrictly.value = !checkStrictly.value;
  localStorage.setItem(
    storageKeyCheckStrictly,
    JSON.stringify(checkStrictly.value),
  );
};


// 字段映射 - 使用TreeDataNode格式，key和title已直接映射
const fieldNames = {
  children: 'children',
  title: 'title',
  key: 'key',
};

/**
 * 将后端菜单节点转换为 Tree DataNode
 */
const convertToTreeData = (menuData: any[]): DataNode[] => {
  return menuData.map((node) => ({
    key: String(node.id),
    title: node.menuName,
    menuType: node.menuType,
    menuCode: node.menuCode,
    children: node.children ? convertToTreeData(node.children) : undefined,
  }));
};

// 获取所有节点key
/**
 * 获取所有节点 key（深度优先遍历）
 */
const getAllKeys = (nodes: DataNode[]): string[] => {
  const keys: string[] = [];
  const traverse = (nodes: DataNode[]) => {
    nodes.forEach((node) => {
      keys.push(String(node.key));
      if (node.children && node.children.length > 0) {
        traverse(node.children);
      }
    });
  };
  traverse(nodes);
  return keys;
};

/**
 * 获取所有父节点 key
 */
const getAllParentKeys = (nodes: DataNode[]): string[] => {
  const keys: string[] = [];
  const traverse = (nodes: DataNode[]) => {
    nodes.forEach((node) => {
      if (node.children && node.children.length > 0) {
        keys.push(String(node.key));
        traverse(node.children);
      }
    });
  };
  traverse(nodes);
  return keys;
};

// 获取当前树中存在的节点key
/**
 * 过滤出当前树中有效的选中 key
 */
const getValidKeys = (keys: string[]): string[] => {
  const allKeys = getAllKeys(treeData.value);
  return keys.filter((key) => allKeys.includes(String(key)));
};

// 加载菜单树数据
/**
 * 加载菜单树（仅启用项），并在数据变更时校验选中项有效性
 */
const loadMenuTree = async () => {
  try {
    loading.value = true;
    const response = await getMenuTree({ status: 1 });
    const converted = convertToTreeData(response || []);
    rawTreeData.value = converted;
    treeData.value = applyFilter(converted, searchText.value);

    if (Array.isArray(props.value) && props.value.length > 0) {
      let validKeys = getValidKeys(props.value);
      if (!checkStrictly.value) {
        const parentKeys = getAllParentKeys(treeData.value);
        validKeys = validKeys.filter((k) => !parentKeys.includes(k));
      }
      checkedKeys.value = validKeys;
      expandAll();
    } else {
      expandAll();
    }
  } finally {
    loading.value = false;
  }
};

/**
 * 展开全部节点
 */
const expandAll = () => {
  expandedKeys.value = getAllKeys(treeData.value);
};

/**
 * 收起全部节点
 */
const collapseAll = () => {
  expandedKeys.value = [];
};

/**
 * 全选所有权限
 */
const handleSelectAll = () => {
  checkedKeys.value = getAllKeys(treeData.value);
  // 立即同步到父组件
  emit('update:value', checkedKeys.value);
  emit('change', checkedKeys.value);
};

/**
 * 取消全选（清空）
 */
const handleUnselectAll = () => {
  checkedKeys.value = [];
  // 立即同步到父组件
  emit('update:value', checkedKeys.value);
  emit('change', checkedKeys.value);
};

/**
 * 处理选择变化（适配 antd Tree 返回形态）
 */
const handleCheck = (checked: any, e: any) => {
  // Ant Design Vue Tree组件check事件返回{checked, halfChecked}对象
  // 当 checkStrictly=false 时，checked 是数组，且 e.halfCheckedKeys 包含半选节点
  let validKeys: string[] = [];

  if (Array.isArray(checked)) {
    // 联动模式：包含全选节点和半选节点
    const halfChecked = e?.halfCheckedKeys || [];
    validKeys = [...checked, ...halfChecked];
  } else {
    // 独立模式：只包含选中的节点
    validKeys = checked?.checked || [];
  }

  // 过滤掉空值并转为字符串
  validKeys = validKeys.filter(Boolean).map(String);

  // 关键修复：必须通过 emit('update:value') 更新父组件的绑定值
  emit('update:value', validKeys);
  emit('change', validKeys);
};

// 保证外部 value 变化时 checkedKeys 跟随变化
watch(
  () => props.value,
  (val) => {
    if (!Array.isArray(val)) {
      checkedKeys.value = [];
      return;
    }
    let keys = [...val];
    // 如果树已加载且是联动模式，进行过滤
    if (treeData.value.length > 0 && !checkStrictly.value) {
      const parentKeys = getAllParentKeys(treeData.value);
      // 过滤掉父节点，防止 antd Tree 报 missing follow keys 警告
      keys = keys.filter((k) => !parentKeys.includes(k));
    }
    checkedKeys.value = keys;
  },
  { immediate: true },
);

// 监听 checkStrictly 变化，切换时处理 checkedKeys
watch(checkStrictly, (val) => {
  if (val) {
    // 切换到独立模式：这里如果需要恢复父节点比较复杂，暂保持现状
    // 通常用户切换到独立模式是为了精细控制，保持现状是安全的
  } else {
    // 切换到联动模式：剔除父节点
    const parentKeys = getAllParentKeys(treeData.value);
    checkedKeys.value = checkedKeys.value.filter(
      (k) => !parentKeys.includes(k),
    );
  }
});

/**
 * 过滤树数据（按名称/编码模糊匹配）
 */
function applyFilter(source: DataNode[], keyword: string): DataNode[] {
  const k = keyword.trim().toLowerCase();
  if (!k) return source;
  const match = (n: any) =>
    String(n.title).toLowerCase().includes(k) ||
    String(n.menuCode || '')
      .toLowerCase()
      .includes(k);
  const loop = (nodes: DataNode[]): DataNode[] => {
    const res: DataNode[] = [];
    for (const node of nodes) {
      const children = node.children
        ? loop(node.children as DataNode[])
        : undefined;
      if (match(node) || (children && children.length > 0)) {
        res.push({ ...node, children });
      }
    }
    return res;
  };
  return loop(source);
}

// 初始化持久化的联动设置
const saved = localStorage.getItem(storageKeyCheckStrictly);
if (saved !== null) {
  checkStrictly.value = JSON.parse(saved) === true;
}

// 初始化加载
loadMenuTree();

// 搜索输入监听
watch(
  () => searchText.value,
  (val) => {
    treeData.value = applyFilter(rawTreeData.value, val);
  },
);
</script>

<template>
  <div class="flex h-full w-full flex-col p-2">
    <div class="mb-2 flex shrink-0 items-center justify-between">
      <span class="text-sm font-medium">权限分配</span>
      <div class="ml-4 flex flex-1 items-center justify-end gap-2">
        <Input
          v-model:value="searchText"
          size="small"
          placeholder="搜索菜单/编码"
          class="max-w-xs"
        />
        <Tooltip :title="checkStrictly ? '父子独立' : '父子联动'">
          <Button size="small" @click="toggleCheckStrictly">
            <Icon
              :icon="checkStrictly ? 'lucide:git-fork' : 'lucide:git-merge'"
            />
          </Button>
        </Tooltip>
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
        <Dropdown.Button size="small" placement="bottomRight">
          <template #overlay>
            <Menu>
              <Menu.Item key="selectAll" @click="handleSelectAll">
                全选
              </Menu.Item>
              <Menu.Item key="unselectAll" @click="handleUnselectAll">
                取消全选
              </Menu.Item>
            </Menu>
          </template>
          <template #icon>
            <Icon icon="lucide:chevrons-down" />
          </template>
          更多
        </Dropdown.Button>
      </div>
    </div>

    <div class="flex-1 overflow-y-auto p-1">
      <Spin :spinning="loading">
        <Tree
          v-model:checked-keys="checkedKeys"
          v-model:expanded-keys="expandedKeys"
          :checkable="true"
          :check-strictly="checkStrictly"
          :field-names="fieldNames"
          :tree-data="treeData"
          :selectable="false"
          check-on-click-node
          @check="handleCheck"
        >
          <template #title="{ title, menuType, menuCode }">
            <div class="flex items-center gap-2">
              <span>{{ title }}</span>
              <Tag
                v-if="menuType === 0"
                color="processing"
                :bordered="false"
                class="m-0"
              >
                目录
              </Tag>
              <Tag
                v-else-if="menuType === 1"
                color="success"
                :bordered="false"
                class="m-0"
              >
                菜单
              </Tag>
              <Tag
                v-else-if="menuType === 2"
                color="warning"
                :bordered="false"
                class="m-0"
              >
                按钮
              </Tag>
              <span class="text-xs text-gray-400">{{ menuCode }}</span>
            </div>
          </template>
        </Tree>
      </Spin>
    </div>
  </div>
</template>
