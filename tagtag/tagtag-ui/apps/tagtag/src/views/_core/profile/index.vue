<script setup lang="ts">
import { ref } from 'vue';
import { useUserStore } from '@vben/stores';
import ProfileBase from './base-setting.vue';
import ProfilePasswordSetting from './password-setting.vue';
import { Icon } from '@iconify/vue';

const userStore = useUserStore();
const activeTab = ref('basic');

const tabs = [
  {
    label: '基本设置',
    value: 'basic',
    icon: 'lucide:user',
    desc: '管理您的个人资料信息'
  },
  {
    label: '安全设置',
    value: 'password',
    icon: 'lucide:lock',
    desc: '修改密码与账户安全'
  },
];
</script>
<template>
  <div class="p-4 md:p-6">
    <div class="mb-6">
      <h1 class="text-2xl font-bold tracking-tight">{{ $t('page.auth.profile.title') }}</h1>
      <p class="text-muted-foreground text-sm mt-1">
        管理您的个人信息、隐私和安全设置
      </p>
    </div>

    <div class="flex flex-col gap-6 lg:flex-row lg:gap-10">
      <!-- 侧边导航 -->
      <aside class="w-full lg:w-64 flex-none">
         <nav class="bg-card border-border flex flex-row space-x-1 overflow-x-auto rounded-lg border p-2 shadow-sm lg:flex-col lg:space-x-0 lg:space-y-1">
            <button
              v-for="tab in tabs"
              :key="tab.value"
              @click="activeTab = tab.value"
              :class="[
                'flex items-center gap-3 rounded-md px-4 py-3 text-sm font-medium transition-all duration-200',
                'flex-1 justify-center whitespace-nowrap lg:w-full lg:justify-start',
                activeTab === tab.value
                  ? 'bg-primary text-primary-foreground shadow-sm'
                  : 'text-muted-foreground hover:bg-muted hover:text-foreground'
              ]"
            >
              <Icon :icon="tab.icon" class="size-4" />
              {{ tab.label }}
            </button>
         </nav>

         <!-- 简略信息卡片 (可选) -->
         <div class="bg-card border-border mt-6 rounded-lg border p-4 shadow-sm hidden lg:block">
            <div class="flex items-center gap-3">
               <div class="size-10 rounded-full bg-primary/10 flex items-center justify-center text-primary font-bold">
                 {{ userStore.userInfo?.realName?.charAt(0)?.toUpperCase() || 'U' }}
               </div>
               <div class="overflow-hidden">
                 <div class="truncate font-medium text-sm">{{ userStore.userInfo?.realName }}</div>
                 <div class="truncate text-xs text-muted-foreground">{{ userStore.userInfo?.username }}</div>
               </div>
            </div>
         </div>
      </aside>

      <!-- 内容区域 -->
      <main class="flex-1 min-w-0">
        <Transition name="fade-slide" mode="out-in">
          <ProfileBase v-if="activeTab === 'basic'" />
          <ProfilePasswordSetting v-else-if="activeTab === 'password'" />
        </Transition>
      </main>
    </div>
  </div>
</template>

<style scoped>
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
