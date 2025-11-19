<script setup lang="ts">
import { computed, onMounted, ref, useId, watch } from 'vue';

import { Input as VbenInput } from '@vben-core/shadcn-ui';

/**
 * 图片验证码输入框属性
 * @property src 初始验证码图片地址
 * @property fetchImage 点击图片时异步拉取新验证码
 * @property disabled 是否禁用输入
 * @property placeholder 输入框占位符
 */
const {
  src = '',
  fetchImage = undefined,
  disabled = false,
  placeholder = '',
} = defineProps<{
  disabled?: boolean;
  fetchImage?: () => Promise<{ captchaId?: string; src: string }>;
  placeholder?: string;
  src?: string;
}>();

/**
 * 对外事件
 * @event refresh 当不存在 fetchImage 时，通知外部刷新
 * @event complete 当用户按下 Enter 完成输入时触发
 * @event ready 当成功拉取到验证码并完成渲染时触发（用于父组件感知就绪态）
 */
const emit = defineEmits<{ complete: []; ready: []; refresh: [] }>();

/**
 * v-model：验证码对象模型（包含 code 与 captchaId）
 */
const model = defineModel<{ captchaId?: string; code: string }>({
  default: { code: '' },
});

/**
 * 代理 code 的双向绑定（映射到对象模型中）
 */
const codeModel = computed<string>({
  get() {
    return model.value?.code ?? '';
  },
  set(v: string) {
    model.value = { ...(model.value || { code: '' }), code: v };
  },
});

const imgSrc = ref(src);
const id = useId();

/**
 * 是否存在有效图片地址（非空）
 */
const hasImage = computed(
  () => typeof imgSrc.value === 'string' && imgSrc.value.length > 0,
);

/**
 * 初始化与 props 同步：当传入的 src 变动时更新显示
 */
watch(
  () => src,
  (v) => {
    if (typeof v === 'string') imgSrc.value = v;
  },
  { immediate: true },
);

/**
 * 刷新验证码：优先调用 fetchImage，否则对外发出 refresh 事件
 * 成功后写入 model.captchaId，保证提交可携带验证码标识
 */
async function handleRefresh() {
  try {
    if (typeof fetchImage === 'function') {
      const res = await fetchImage();
      imgSrc.value = res?.src || '';
      const id = res?.captchaId || '';
      model.value = { ...(model.value || { code: '' }), captchaId: id };
    } else {
      emit('refresh');
    }
  } catch {
    // 拉取异常时清空图片地址，避免显示裂开图片
    imgSrc.value = '';
  }
}

/**
 * 图片加载失败处理：清空图片地址，避免显示裂开图片
 */
function handleImgError() {
  imgSrc.value = '';
}

/**
 * 处理回车提交：向外派发 complete 事件
 * @param e 键盘事件
 */
function handleEnter(e: KeyboardEvent) {
  if (e.key === 'Enter') emit('complete');
}

/**
 * 组件挂载时若无图片地址则尝试刷新
 */
onMounted(() => {
  if (!imgSrc.value) handleRefresh();
});
</script>

<template>
  <div class="flex w-full items-center gap-2">
    <VbenInput
      :id="id"
      v-model="codeModel"
      :disabled="disabled"
      :placeholder="placeholder"
      class="flex-1"
      @keydown="handleEnter"
    />
    <img
      v-if="hasImage"
      :src="imgSrc"
      alt="captcha"
      class="border-border h-10 w-24 cursor-pointer select-none rounded-md border object-cover"
      @error="handleImgError"
      @click="handleRefresh"
      @touchstart.passive="handleRefresh"
    />
    <div
      v-else
      class="border-border flex h-10 w-24 cursor-pointer select-none items-center justify-center rounded-md border text-xs"
      @click="handleRefresh"
      @touchstart.passive="handleRefresh"
    >
      刷新验证码
    </div>
  </div>
</template>
