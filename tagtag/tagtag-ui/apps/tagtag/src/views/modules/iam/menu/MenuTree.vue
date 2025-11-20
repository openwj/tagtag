<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { Tree as ATree, Spin as ASpin } from 'ant-design-vue';

import { getMenuPage } from '#/api/modules/iam/menu';

const loading = ref(false);
const treeData = ref<any[]>([]);
const selectedKeys = ref<string[]>([]);

const emit = defineEmits<{ (e: 'select', parentId: number | null): void }>();

/**
 * 加载全部菜单作为可选父级节点
 * 使用服务端限制的最大分页容量（200），循环聚合所有数据
 */
async function loadAllMenus() {
  loading.value = true;
  try {
    const pageSize = 200;
    let pageNo = 1;
    const all: any[] = [];
    let total = 0;
    // 分页聚合
    while (true) {
      const page = { pageNo, pageSize };
      const data = await getMenuPage({}, page);
      const list = (data?.records ?? data?.list ?? []) as any[];
      total = Number(data?.total ?? total);
      all.push(...list);
      if (list.length < pageSize) break;
      if (total && all.length >= total) break;
      pageNo += 1;
    }
    treeData.value = [
      {
        title: '全部菜单',
        key: 'ALL',
        selectable: true,
        children: all.map((m: any) => ({ title: String(m.menuName), key: String(m.id), isLeaf: true })),
      },
    ];
    selectedKeys.value = ['ALL'];
  } finally {
    loading.value = false;
  }
}

/**
 * 选择父级节点
 * @param keys 选中的节点key列表
 */
function handleSelect(keys: any[]) {
  selectedKeys.value = keys as string[];
  const k = selectedKeys.value[0];
  const parentId = !k || k === 'ALL' ? null : Number(k);
  emit('select', parentId);
}

onMounted(() => {
  loadAllMenus();
});
</script>

<template>
  <ASpin :spinning="loading">
    <ATree :tree-data="treeData" :selected-keys="selectedKeys" block-node show-line @select="handleSelect" />
  </ASpin>
</template>