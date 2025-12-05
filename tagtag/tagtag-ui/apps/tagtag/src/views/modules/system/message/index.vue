<script lang="ts" setup>
import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';
import type { MessageItem } from '#/api/modules/system/message';

import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  Button,
  Dropdown,
  Menu,
  message,
  Modal,
  Popconfirm,
  Tag,
  Tooltip,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteMessage,
  deleteMessageBatch,
  getAllMessagePage,
  markMessageReadBatch,
  markMessageUnreadBatch,
} from '#/api/modules/system/message';

import { columns, MessageTypeMap, searchFormSchema } from './data';

const router = useRouter();

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
      result: 'list', // 对应 PageResult.list
      total: 'total', // 对应 PageResult.total
    },
    ajax: {
      /**
       * 按统一分页契约获取消息分页数据（支持检索映射）
       * @param page 分页参数（pageNo/pageSize/sortFields）
       * @param formValues 查询条件（如标题/内容/状态/时间范围）
       * @returns 消息列表与总条数
       */
      query: async ({ page }, formValues) => {
        const query: Record<string, any> = { ...formValues };
        if (formValues?.dateRange?.length === 2) {
          query.startDate = formValues.dateRange[0];
          query.endDate = formValues.dateRange[1];
        }
        // 管理员视角：查看所有用户的消息
        const { list, total } = await getAllMessagePage(query, page);
        return { list, total };
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

/**
 * 获取选中的行数据
 */
const getSelectedRows = () => {
  return gridApi.grid?.getCheckboxRecords() || [];
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

/**
 * 查看详情
 */
const handleView = (row: MessageItem) => {
  // 管理员查看详情可能不需要标记已读，或者跳转到专门的管理详情页
  // 暂时复用详情页，但需要注意权限控制
  router.push({ name: 'MessageCenterDetail', params: { id: row.id } });
};
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="消息管理">
      <template #toolbar-tools>
        <div class="flex items-center gap-2">
          <Dropdown.Button class="px-2">
            批量操作
            <template #overlay>
              <Menu>
                <Menu.Item
                  key="batch-delete"
                  v-access:code="'message:delete'"
                  @click="
                    () =>
                      Modal.confirm({
                        title: '批量删除',
                        content: '确定删除选中的消息吗？',
                        okText: '确定',
                        cancelText: '取消',
                        async onOk() {
                          const rows = getSelectedRows();
                          if (rows.length === 0) {
                            message.warning('请勾选要操作的消息');
                            return;
                          }
                          const ids = rows.map((r: any) => r.id);
                          await deleteMessageBatch(ids);
                          message.success('批量删除成功');
                          gridApi.grid?.clearCheckboxRow();
                          gridApi.reload();
                        },
                      })
                  "
                >
                  <span class="icon-[lucide--trash-2] mr-1"></span>
                  删除
                </Menu.Item>
                <Menu.Item
                  key="batch-read"
                  @click="
                    async () => {
                      const rows = getSelectedRows();
                      if (rows.length === 0) {
                        message.warning('请勾选要操作的消息');
                        return;
                      }
                      const ids = rows.map((r: any) => r.id);
                      await markMessageReadBatch(ids);
                      message.success('已标记为已读');
                      gridApi.grid?.clearCheckboxRow();
                      gridApi.reload();
                    }
                  "
                >
                  <span class="icon-[lucide--check-circle] mr-1"></span>
                  标记已读
                </Menu.Item>
                <Menu.Item
                  key="batch-unread"
                  @click="
                    async () => {
                      const rows = getSelectedRows();
                      if (rows.length === 0) {
                        message.warning('请勾选要操作的消息');
                        return;
                      }
                      const ids = rows.map((r: any) => r.id);
                      await markMessageUnreadBatch(ids);
                      message.success('已标记为未读');
                      gridApi.grid?.clearCheckboxRow();
                      gridApi.reload();
                    }
                  "
                >
                  <span class="icon-[lucide--circle] mr-1"></span>
                  标记未读
                </Menu.Item>
              </Menu>
            </template>
          </Dropdown.Button>
        </div>
      </template>

      <template #title="{ row }">
        <div class="flex items-center gap-2">
          <Button type="link" size="small" @click="handleView(row)">
            {{ row.title }}
          </Button>
        </div>
      </template>

      <template #type="{ row }">
        <Tag :color="MessageTypeMap[row.type]?.color || 'default'" :bordered="false">
          {{ MessageTypeMap[row.type]?.text || row.type }}
        </Tag>
      </template>

      <template #date="{ row }">
        <span class="text-gray-500">{{ row.createTime }}</span>
      </template>

      <template #action="{ row }">
        <div class="flex items-center justify-center gap-2">
          <Tooltip title="查看详情">
            <Button type="link" size="small" @click="handleView(row)">
              <span class="icon-[lucide--eye] text-lg"></span>
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
                size="small"
                danger
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
