<script lang="ts" setup>
import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import DictData from './components/DictData.vue';
import DictType from './components/DictType.vue';

const currentDictType = ref<any>(null);
</script>

<template>
  <Page auto-content-height>
    <div class="flex h-full gap-4">
      <!-- 左侧：字典类型 -->
      <DictType class="w-5/12" @select="(row) => (currentDictType = row)" />

      <!-- 右侧：字典数据 -->
      <DictData class="w-0 flex-1" :dict-type="currentDictType" />
    </div>
  </Page>
</template>
