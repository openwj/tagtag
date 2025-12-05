<script lang="ts" setup>
import type { MessageItem } from '#/api/modules/system/message';

import { onMounted, ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  Avatar,
  Button,
  Card,
  message as AntMessage,
  Popconfirm,
  Skeleton,
  Space,
  Tag,
  Tooltip,
} from 'ant-design-vue';

import { deleteMessage, getMessage, markMessageRead } from '#/api/modules/system/message';
import { MessageTypeMap } from './data';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const message = ref<MessageItem | null>(null);

const loadDetail = async () => {
  const id = route.params.id;
  if (!id) return;
  loading.value = true;
  try {
    const res = await getMessage(id as string);
    message.value = res;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleBack = () => {
  router.back();
};

const handleRead = async () => {
  if (!message.value || message.value.isRead) return;
  try {
    await markMessageRead(message.value.id);
    message.value.isRead = true;
    AntMessage.success('已标记为已读');
  } catch {
    AntMessage.error('操作失败');
  }
};

const handleDelete = async () => {
  if (!message.value) return;
  try {
    await deleteMessage(message.value.id);
    AntMessage.success('删除成功');
    router.back();
  } catch {
    AntMessage.error('删除失败');
  }
};

const typeInfo = computed(() => {
  if (!message.value?.type) return { color: 'default', text: '未知' };
  return MessageTypeMap[message.value.type] || { color: 'default', text: message.value.type };
});

onMounted(() => {
  loadDetail();
});
</script>

<template>
  <Page title="消息详情" @back="handleBack">
    <template #extra>
      <Space>
        <Button v-if="message && !message.isRead" @click="handleRead">
          <span class="icon-[lucide--check] mr-1"></span>
          标记已读
        </Button>
        <Popconfirm
          title="确定删除此消息?"
          ok-text="确定"
          cancel-text="取消"
          @confirm="handleDelete"
        >
          <Button danger>
            <span class="icon-[lucide--trash-2] mr-1"></span>
            删除
          </Button>
        </Popconfirm>
      </Space>
    </template>
    <div class="p-4">
      <Card :loading="loading" :bordered="false" class="shadow-sm rounded-xl overflow-hidden">
        <template v-if="message">
          <!-- 顶部主要信息 -->
          <div class="border-b border-gray-100 dark:border-gray-800 p-6 pb-5">
            <div class="flex items-start gap-5">
              <Avatar :src="message.avatar" :size="64" class="flex-shrink-0 shadow-md border-2 border-white dark:border-gray-700">
                {{ message.senderName?.charAt(0)?.toUpperCase() || 'S' }}
              </Avatar>
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between mb-2">
                  <h1 class="text-2xl font-bold text-gray-900 dark:text-gray-100 truncate pr-4">{{ message.title }}</h1>
                  <Tag :color="message.isRead ? 'default' : 'red'" class="mr-0 px-3 py-0.5 text-sm rounded-full border-none">
                    {{ message.isRead ? '已读' : '未读' }}
                  </Tag>
                </div>

                <div class="flex flex-wrap items-center gap-6 text-gray-500 dark:text-gray-400 text-sm">
                  <div class="flex items-center gap-1.5">
                    <span class="icon-[lucide--user] text-base opacity-70"></span>
                    <span>{{ message.senderName || '系统' }}</span>
                  </div>
                  <div class="flex items-center gap-1.5">
                    <span class="icon-[lucide--clock] text-base opacity-70"></span>
                    <Tooltip :title="message.createTime">
                      <span>{{ message.createTime }}</span>
                    </Tooltip>
                  </div>
                  <div class="flex items-center gap-1.5">
                    <span class="icon-[lucide--tag] text-base opacity-70"></span>
                    <Tag :color="typeInfo.color" :bordered="false" class="m-0 px-2 text-xs">
                      {{ typeInfo.text }}
                    </Tag>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 消息内容 -->
          <div class="p-8">
            <div class="text-base leading-8 text-gray-700 dark:text-gray-300 whitespace-pre-wrap font-normal">
              {{ message.content }}
            </div>
          </div>

          <!-- 底部接收人信息（弱化显示） -->
          <div class="px-8 pb-8 flex justify-end">
             <div class="text-xs text-gray-400 flex items-center gap-1.5 bg-gray-50 dark:bg-gray-800 px-3 py-1 rounded-full">
                <span class="icon-[lucide--send] text-xs"></span>
                <span>接收人: {{ message.receiverName || '未知' }}</span>
             </div>
          </div>

        </template>
        <template v-else>
          <Skeleton active avatar :paragraph="{ rows: 4 }" />
        </template>
      </Card>
    </div>
  </Page>
</template>
