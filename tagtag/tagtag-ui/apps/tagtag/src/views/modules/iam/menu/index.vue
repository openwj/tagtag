<script lang="ts" setup>
import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';

import { Icon } from '@iconify/vue';
import {
  Button as AButton,
  Divider as ADivider,
  Popconfirm as APopconfirm,
  Switch as ASwitch,
  Tag as ATag,
  Tooltip as ATooltip,
  message,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  batchDeleteMenu,
  batchUpdateMenuStatus,
  deleteMenu,
  getMenuTree,
  updateMenuStatus,
} from '#/api/modules/iam/menu';

import { columns, searchFormSchema } from './data';
import FormModal from './FormModal.vue';

const formOptions: VbenFormProps = {
  // 默认折叠
  schema: searchFormSchema,
  // 控制表单是否显示折叠按钮
  showCollapseButton: true,
  // 默认折叠状态
  collapsed: true,
};

const gridOptions: VxeGridProps = {
  columns,
  height: 'auto',
  pagerConfig: {
    enabled: false,
  },
  exportConfig: {},
  // importConfig: {},
  resizeConfig: {},
  proxyConfig: {
    ajax: {
      // eslint-disable-next-line no-empty-pattern
      query: async ({}, formValues) => {
        const data = await getMenuTree(formValues);
        return { items: data };
      },
    },
  },
  toolbarConfig: {
    custom: true,
    export: true,
    // import: true,
    refresh: true,
    zoom: true,
  },
  treeConfig: {
    // parentField: 'parentId',
    rowField: 'id',
    expandAll: true,
  },
};

const [Grid, gridApi] = useVbenVxeGrid({ formOptions, gridOptions });

const [VFormModal, formModalApi] = useVbenModal({
  connectedComponent: FormModal,
  draggable: true,
});

// 加载状态管理
const loading = ref(false);
const batchLoading = ref(false);

const handleAdd = (row: Record<string, any>) => {
  const values = { isUpdate: false, parentId: '' };
  if (row?.id) {
    values.parentId = row.id;
  }
  // 处理新增
  formModalApi.setData({
    // 表单值
    values,
  });
  formModalApi.open();
};

/**
 * 检查菜单是否有子菜单
 * @param menuId 菜单ID
 */
const hasChildren = (menuId: string) => {
  const allData = gridApi.grid.getTableData().fullData;
  return allData.some((item: any) => item.parentId === menuId);
};

/**
 * 处理删除
 * @param id 菜单ID
 */
const handleDelete = async (id: string) => {
  // 检查是否有子菜单
  if (hasChildren(id)) {
    message.warning('该菜单下存在子菜单，请先删除子菜单');
    return;
  }

  loading.value = true;
  try {
    await deleteMenu(id);
    message.success('删除成功');
    gridApi.reload();
  } finally {
    loading.value = false;
  }
};

const handleEdit = (row: Record<string, any>) => {
  // 处理编辑
  formModalApi.setData({
    // 表单值
    values: { ...row, isUpdate: true },
  });
  formModalApi.open();
};

const handleSuccess = () => {
  gridApi.reload();
};

/**
 * 获取选中的行数据
 */
const getSelectedRows = () => {
  return gridApi.grid.getCheckboxRecords();
};

/**
 * 批量删除菜单
 */
const handleBatchDelete = async () => {
  const selectedRows = getSelectedRows();
  if (selectedRows.length === 0) {
    message.warning('请选择要删除的菜单');
    return;
  }

  // 检查选中的菜单是否有子菜单
  const hasChildrenMenus = selectedRows.some((row) => hasChildren(row.id));
  if (hasChildrenMenus) {
    message.warning('选中的菜单中存在有子菜单的项，请先删除子菜单');
    return;
  }

  batchLoading.value = true;

  const ids = selectedRows.map((row) => row.id);
  await batchDeleteMenu(ids);
  message.success(`成功删除 ${selectedRows.length} 个菜单`);
  gridApi.reload();

  batchLoading.value = false;
};

/**
 * 批量启用菜单
 */
const handleBatchEnable = async () => {
  const selectedRows = getSelectedRows();
  if (selectedRows.length === 0) {
    message.warning('请选择要启用的菜单');
    return;
  }

  batchLoading.value = true;

  const ids = selectedRows.map((row) => row.id);
  await batchUpdateMenuStatus(ids, false); // false表示不禁用，即启用
  message.success(`成功启用 ${selectedRows.length} 个菜单`);
  gridApi.reload();

  batchLoading.value = false;
};

/**
 * 批量禁用菜单
 */
const handleBatchDisable = async () => {
  const selectedRows = getSelectedRows();
  if (selectedRows.length === 0) {
    message.warning('请选择要禁用的菜单');
    return;
  }

  batchLoading.value = true;

  const ids = selectedRows.map((row) => row.id);
  await batchUpdateMenuStatus(ids, true); // true表示禁用
  message.success(`成功禁用 ${selectedRows.length} 个菜单`);
  gridApi.reload();

  batchLoading.value = false;
};

/**
 * 处理状态切换
 * @param row 行数据
 * @param checked 是否选中
 */
const handleStatusChange = async (
  row: Record<string, any>,
  checked: boolean,
) => {
  try {
    // 设置加载状态
    row.statusLoading = true;

    // 调用API更新状态
    await updateMenuStatus(row.id, !checked); // 转换为disabled布尔值

    // 更新本地状态
    row.status = checked ? 1 : 0;

    message.success(`菜单${checked ? '启用' : '禁用'}成功`);
  } finally {
    // 清除加载状态
    row.statusLoading = false;
  }
};
</script>

<template>
  <Page auto-content-height>
    <Grid
      :all-tree-expand="true"
      table-title="菜单信息"
      table-title-help="系统菜单信息"
    >
      <template #toolbar-tools>
        <div class="flex items-center gap-3">
          <AButton class="flex items-center" type="primary" @click="handleAdd">
            <template #icon>
              <span class="icon-[lucide--plus] mr-1"></span>
            </template>
            新增
          </AButton>

          <APopconfirm
            title="确定要删除选中的菜单吗？"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleBatchDelete"
          >
            <AButton class="flex items-center" danger :loading="batchLoading">
              <template #icon>
                <span class="icon-[lucide--trash-2] mr-1"></span>
              </template>
              批量删除
            </AButton>
          </APopconfirm>

          <APopconfirm
            title="确定要启用选中的菜单吗？"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleBatchEnable"
          >
            <AButton class="flex items-center" :loading="batchLoading">
              <template #icon>
                <span class="icon-[lucide--check-circle] mr-1"></span>
              </template>
              批量启用
            </AButton>
          </APopconfirm>

          <APopconfirm
            title="确定要禁用选中的菜单吗？"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleBatchDisable"
          >
            <AButton class="flex items-center" :loading="batchLoading">
              <template #icon>
                <span class="icon-[lucide--x-circle] mr-1"></span>
              </template>
              批量禁用
            </AButton>
          </APopconfirm>
        </div>
      </template>
      <template #icon="{ row }">
        <div class="group flex cursor-help items-center justify-center">
          <div v-if="row.icon">
            <!-- 主图标 -->
            <!-- <Icon :icon="row.icon" /> -->
            <Icon :icon="row.icon" color="orange" v-if="row.menuType === 0" />
            <Icon :icon="row.icon" color="blue" v-if="row.menuType === 1" />
            <Icon :icon="row.icon" color="green" v-if="row.menuType === 2" />
          </div>
          <div v-else>
            <Icon
              icon="lucide:folder"
              color="orange"
              v-if="row.menuType === 0"
            />
            <Icon
              icon="lucide:file-text"
              color="blue"
              v-if="row.menuType === 1"
            />
            <Icon icon="lucide:zap" color="green" v-if="row.menuType === 2" />
          </div>
        </div>
      </template>
      <template #status="{ row }">
        <ASwitch
          :checked="row.status === 1"
          :loading="row.statusLoading"
          checked-children="启用"
          un-checked-children="禁用"
          @change="
            (checked) =>
              handleStatusChange(row, checked === true || checked === 1)
          "
        />
      </template>
      <template #type="{ row }">
        <ATag color="orange" v-if="row.menuType === 0">
          <span class="icon-[lucide--folder] mr-1"></span>
          目录
        </ATag>
        <ATag color="blue" v-else-if="row.menuType === 1">
          <span class="icon-[lucide--file-text] mr-1"></span>
          菜单
        </ATag>
        <ATag color="green" v-else-if="row.menuType === 2">
          <span class="icon-[lucide--zap] mr-1"></span>
          按钮
        </ATag>
      </template>
      <template #external="{ row }">
        <ATag color="red" v-if="row.isExternal === 1" class="flex items-center">
          <span class="icon-[lucide--external-link] mr-1"></span>
          外链
        </ATag>
        <ATag color="default" v-else class="flex items-center">
          <span class="icon-[lucide--home] mr-1"></span>
          内部
        </ATag>
      </template>

      <template #toolbar-actions> </template>
      <template #action="{ row }">
        <div class="flex items-center justify-center gap-0.5">
          <ATooltip title="新增子菜单">
            <AButton
              class="flex h-6 w-6 items-center justify-center p-0 transition-transform hover:scale-105"
              shape="circle"
              size="small"
              type="primary"
              @click="handleAdd(row)"
            >
              <template #icon>
                <div class="icon-[lucide--plus] text-xs"></div>
              </template>
            </AButton>
          </ATooltip>

          <ADivider type="vertical" class="mx-1 h-4" />

          <ATooltip title="编辑菜单">
            <AButton
              class="flex h-6 w-6 items-center justify-center p-0 transition-transform hover:scale-105"
              ghost
              shape="circle"
              size="small"
              type="primary"
              @click="handleEdit(row)"
            >
              <template #icon>
                <div class="icon-[lucide--edit] text-xs"></div>
              </template>
            </AButton>
          </ATooltip>

          <ADivider type="vertical" class="mx-1 h-4" />

          <APopconfirm
            cancel-text="取消"
            ok-text="确定"
            placement="left"
            :title="`确定要删除菜单 '${row.menuName}' 吗？`"
            @confirm="handleDelete(row.id)"
          >
            <ATooltip title="删除菜单">
              <AButton
                class="flex h-6 w-6 items-center justify-center p-0 transition-transform hover:scale-105"
                danger
                ghost
                shape="circle"
                size="small"
                type="primary"
                :loading="loading"
              >
                <template #icon>
                  <div class="icon-[lucide--trash-2] text-xs"></div>
                </template>
              </AButton>
            </ATooltip>
          </APopconfirm>
        </div>
      </template>
    </Grid>

    <VFormModal class="sm:w-full md:w-1/2" @success="handleSuccess" />
  </Page>
</template>
