<script lang="ts" setup>
import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  Button,
  message,
  Modal,
  Popconfirm,
  Tag,
  Tooltip,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  clearAllMessages,
  deleteMessage,
  getMessagePage,
  markAllMessageRead,
  markMessageRead,
  type MessageItem,
} from '#/api/modules/system/message';

import { columns, searchFormSchema } from './data';

const formOptions: VbenFormProps = {
  collapsed: true,
  schema: searchFormSchema,
  showCollapseButton: true,
  commonConfig: {
    labelWidth: 60,
  },
};

const gridOptions: VxeGridProps = {
  stripe: true,
  checkboxConfig: {
    highlight: true,
  },
  columns,
  height: 'auto',
  keepSource: true,
  emptyText: '暂无消息',
  pagerConfig: {
    enabled: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100],
    layouts: [
      'PrevPage',
      'JumpNumber',
      'NextPage',
      'FullJump',
      'Sizes',
      'Total',
    ],
    perfect: true,
  },
  proxyConfig: {
    enabled: true,
    autoLoad: true,
    response: {
      result: 'records', // 对应 PageResult.records
      total: 'total', // 对应 PageResult.total
    },
    ajax: {
      query: async ({ page }, formValues) => {
        // 转换参数结构
        const res = await getMessagePage({
          page: page.currentPage,
          size: page.pageSize,
          ...formValues,
        });
        // 后端返回的 message 对应前端 content
        // 后端返回的 date 对应前端 createTime
        // 这里做一下适配
        const records = res.records.map((item: any) => ({
          ...item,
          content: item.message,
          createTime: item.date,
        }));
        return { records, total: res.total };
      },
    },
  },
  toolbarConfig: {
    custom: true,
    export: true,
    refresh: true,
    zoom: true,
  },
};

const [Grid, gridApi] = useVbenVxeGrid({ formOptions, gridOptions });

// 批量操作加载状态
const batchLoading = ref(false);

/**
 * 获取选中的行数据
 */
const getSelectedRows = () => {
  return gridApi.grid?.getCheckboxRecords() || [];
};

// 标记已读
const handleRead = async (row: MessageItem) => {
  if (row.isRead) return;
  try {
    await markMessageRead(row.id);
    row.isRead = true;
    message.success('已标记为已读');
    // 刷新当前行样式
    gridApi.reload();
  } catch {
    message.error('操作失败');
  }
};

// 全部已读
const handleReadAll = async () => {
  try {
    await markAllMessageRead();
    message.success('全部已读');
    gridApi.reload();
  } catch {
    message.error('操作失败');
  }
};

// 删除消息
const handleDelete = async (id: number | string) => {
  try {
    await deleteMessage(id);
    message.success('删除成功');
    gridApi.reload();
  } catch {
    message.error('删除失败');
  }
};

// 清空消息
const handleClear = async () => {
  try {
    await clearAllMessages();
    message.success('清空成功');
    gridApi.reload();
  } catch {
    message.error('清空失败');
  }
};
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="我的消息">
      <template #toolbar-tools>
        <div class="flex items-center gap-2">
          <Button @click="handleReadAll">全部已读</Button>
          <Popconfirm
            title="确定清空所有消息吗？"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleClear"
          >
            <Button danger>清空所有</Button>
          </Popconfirm>
        </div>
      </template>

      <template #title="{ row }">
        <div class="flex items-center gap-2">
          <span :class="{ 'font-bold': !row.isRead }">
            {{ row.title }}
          </span>
          <Tag color="red" v-if="!row.isRead" :bordered="false">未读</Tag>
        </div>
      </template>

      <template #createTime="{ row }">
        <span class="text-gray-500">{{ row.createTime }}</span>
      </template>

      <template #action="{ row }">
        <div class="flex items-center justify-center gap-2">
          <Tooltip title="标记已读" v-if="!row.isRead">
            <Button
              type="link"
              size="small"
              @click="handleRead(row)"
              v-access:code="'message:read'"
            >
              <span class="icon-[lucide--check] text-lg"></span>
            </Button>
          </Tooltip>
          <Popconfirm
            title="确定删除此消息?"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleDelete(row.id)"
          >
            <Tooltip title="删除">
              <Button
                type="link"
                danger
                size="small"
                v-access:code="'message:delete'"
              >
                <span class="icon-[lucide--trash-2] text-lg"></span>
              </Button>
            </Tooltip>
          </Popconfirm>
        </div>
      </template>
    </Grid>
  </Page>
</template>
