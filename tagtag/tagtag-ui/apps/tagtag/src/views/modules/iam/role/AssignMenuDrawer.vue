<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';

import { message, Transfer as ATransfer } from 'ant-design-vue';

import { assignRoleMenus, listRoleMenus } from '#/api/modules/iam/role';
import { getMenuPage } from '#/api/modules/iam/menu';

const roleId = ref<number | null>(null);
const roleName = ref<string>('');
const allMenus = ref<Array<{ key: string; title: string }>>([]);
const targetKeys = ref<string[]>([]);

/**
 * 角色菜单分配抽屉
 * 左右穿梭框选择菜单，提交后覆盖式分配
 */
const [Drawer, drawerApi] = useVbenDrawer({
  onCancel() {
    drawerApi.close();
  },
  /**
   * 提交：将选中的菜单ID列表分配给角色
   */
  onConfirm: async () => {
    if (!roleId.value) return;
    const menuIds = targetKeys.value.map((k) => Number(k));
    await assignRoleMenus(roleId.value, menuIds);
    message.success('菜单分配成功');
    drawerApi.close();
  },
  /**
   * 打开时加载所有菜单与当前角色已分配菜单
   */
  onOpenChange: async (isOpen: boolean) => {
    if (!isOpen) return;
    const { values } = drawerApi.getData<Record<string, any>>();
    roleId.value = Number(values?.roleId ?? 0) || null;
    roleName.value = String(values?.roleName ?? '');

    drawerApi.setState({ title: `为角色【${roleName.value}】分配菜单` });

    // 加载全部菜单（按服务端最大分页限制聚合）
    const pageSize = 200;
    let pageNo = 1;
    const all: any[] = [];
    let total = 0;
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
    allMenus.value = all.map((m: any) => ({ key: String(m.id), title: String(m.menuName) }));

    // 加载已分配菜单
    const assigned = await listRoleMenus(roleId.value!);
    const assignedIds = (assigned ?? []).map((m: any) => String(m.id));
    targetKeys.value = assignedIds;
  },
});
</script>

<template>
  <Drawer>
    <div class="p-4">
      <ATransfer
        :data-source="allMenus"
        :titles="['可选菜单', '已分配']"
        :render="(item: any) => item.title"
        :target-keys="targetKeys"
        @change="(nextTargetKeys: any) => { targetKeys.value = nextTargetKeys as string[]; }"
      />
    </div>
  </Drawer>
  
</template>