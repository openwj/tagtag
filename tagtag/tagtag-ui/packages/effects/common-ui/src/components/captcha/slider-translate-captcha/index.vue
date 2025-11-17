<script setup lang="ts">
import type {
  CaptchaVerifyPassingData,
  SliderCaptchaActionType,
  SliderRotateVerifyPassingData,
  SliderTranslateCaptchaProps,
} from '../types';

import {
  computed,
  onMounted,
  reactive,
  ref,
  unref,
  useTemplateRef,
  watch,
} from 'vue';

import { $t } from '@vben/locales';

import SliderCaptcha from '../slider-captcha/index.vue';

const props = withDefaults(defineProps<SliderTranslateCaptchaProps & {
  /** 是否启用服务端校验 */
  serverVerify?: boolean;
  /** 是否点击后再加载挑战 */
  lazyLoad?: boolean;
  /** 服务端挑战初始化函数 */
  fetchChallenge?: () => Promise<{
    src: string;
    diffDistance?: number;
    pieceX: number;
    pieceY: number;
    challengeId: string;
  }>;
  /** 服务端校验函数 */
  verifyChallenge?: (payload: {
    challengeId: string;
    pieceX: number;
    moveX: number;
    time: number;
  }) => Promise<boolean>;
  /** 由服务端下发的挑战ID（可选） */
  challengeId?: string;
  /** 由服务端下发的拼图X坐标（可选） */
  pieceX?: number;
  /** 由服务端下发的拼图Y坐标（可选） */
  pieceY?: number;
}>(), {
  defaultTip: '',
  canvasWidth: 420,
  canvasHeight: 280,
  squareLength: 42,
  circleRadius: 10,
  src: '',
  diffDistance: 3,
  lazyLoad: true,
});

const emit = defineEmits<{
  success: [CaptchaVerifyPassingData];
  verify: [
    {
      challengeId: string;
      pieceX: number;
      moveX: number;
      time: number;
    },
  ];
}>();

const PI: number = Math.PI;
enum CanvasOpr {
  // eslint-disable-next-line no-unused-vars
  Clip = 'clip',
  // eslint-disable-next-line no-unused-vars
  Fill = 'fill',
}

const modalValue = defineModel<boolean>({ default: false });

const slideBarRef = useTemplateRef<SliderCaptchaActionType>('slideBarRef');
const puzzleCanvasRef = useTemplateRef<HTMLCanvasElement>('puzzleCanvasRef');
const pieceCanvasRef = useTemplateRef<HTMLCanvasElement>('pieceCanvasRef');

const state = reactive({
  dragging: false,
  startTime: 0,
  endTime: 0,
  pieceX: 0,
  pieceY: 0,
  moveDistance: 0,
  isPassing: false,
  showTip: false,
  challengeId: '',
  src: '',
  diffDistance: 3,
  initialized: false,
});

const left = ref('0');

const pieceStyle = computed(() => {
  return {
    left: left.value,
  };
});

/**
 * 设置拼图块左偏移
 * @function setLeft
 */
function setLeft(val: string) {
  left.value = val;
}

const verifyTip = computed(() => {
  return state.isPassing
    ? $t('ui.captcha.sliderTranslateSuccessTip', [
        ((state.endTime - state.startTime) / 1000).toFixed(1),
      ])
    : $t('ui.captcha.sliderTranslateFailTip');
});
/**
 * 记录拖动开始时间
 * @function handleStart
 */
function handleStart() {
  state.startTime = Date.now();
}

/**
 * 处理滑块拖动过程
 * @function handleDragBarMove
 */
function handleDragBarMove(data: SliderRotateVerifyPassingData) {
  state.dragging = true;
  const { moveX } = data;
  state.moveDistance = moveX;
  setLeft(`${moveX}px`);
}

/**
 * 处理拖动结束，支持前端/服务端两种校验
 * @function handleDragEnd
 */
async function handleDragEnd() {
  const { pieceX } = state;
  const localDiff = props.diffDistance ?? state.diffDistance ?? 3;

  // 服务端联动校验优先
  if (props.serverVerify && typeof props.verifyChallenge === 'function') {
    const time = Date.now() - state.startTime;
    const payload = {
      challengeId: state.challengeId || props.challengeId || '',
      pieceX,
      moveX: state.moveDistance,
      time: Number((time / 1000).toFixed(1)),
    };
    emit('verify', payload);
    const passed = await props.verifyChallenge(payload);
    if (passed) {
      checkPass();
    } else {
      setLeft('0');
      state.moveDistance = 0;
    }
  } else {
    // 前端本地校验
    if (Math.abs(pieceX - state.moveDistance) >= localDiff) {
      setLeft('0');
      state.moveDistance = 0;
    } else {
      checkPass();
    }
  }
  state.showTip = true;
  state.dragging = false;
}

/**
 * 校验通过，写入通过态与耗时
 * @function checkPass
 */
function checkPass() {
  state.isPassing = true;
  state.endTime = Date.now();
}

watch(
  () => state.isPassing,
  (isPassing) => {
    if (isPassing) {
      const { endTime, startTime } = state;
      const time = (endTime - startTime) / 1000;
      emit('success', { isPassing, time: time.toFixed(1) });
    }
    modalValue.value = isPassing;
  },
);

/**
 * 重置画布
 * @function resetCanvas
 */
function resetCanvas() {
  const { canvasWidth, canvasHeight } = props;
  const puzzleCanvas = unref(puzzleCanvasRef);
  const pieceCanvas = unref(pieceCanvasRef);
  if (!puzzleCanvas || !pieceCanvas) return;
  pieceCanvas.width = canvasWidth;
  const puzzleCanvasCtx = puzzleCanvas.getContext('2d');
  // Canvas2D: Multiple readback operations using getImageData
  // are faster with the willReadFrequently attribute set to true.
  // See: https://html.spec.whatwg.org/multipage/canvas.html#concept-canvas-will-read-frequently (anonymous)
  const pieceCanvasCtx = pieceCanvas.getContext('2d', {
    willReadFrequently: true,
  });
  if (!puzzleCanvasCtx || !pieceCanvasCtx) return;
  puzzleCanvasCtx.clearRect(0, 0, canvasWidth, canvasHeight);
  pieceCanvasCtx.clearRect(0, 0, canvasWidth, canvasHeight);
}

/**
 * 初始化画布并加载图片
 * @function initCanvas
 */
async function initCanvas() {
  const { canvasWidth, canvasHeight, squareLength, circleRadius, src } = props;
  const puzzleCanvas = unref(puzzleCanvasRef);
  const pieceCanvas = unref(pieceCanvasRef);
  if (!puzzleCanvas || !pieceCanvas) return;
  const puzzleCanvasCtx = puzzleCanvas.getContext('2d');
  // Canvas2D: Multiple readback operations using getImageData
  // are faster with the willReadFrequently attribute set to true.
  // See: https://html.spec.whatwg.org/multipage/canvas.html#concept-canvas-will-read-frequently (anonymous)
  const pieceCanvasCtx = pieceCanvas.getContext('2d', {
    willReadFrequently: true,
  });
  if (!puzzleCanvasCtx || !pieceCanvasCtx) return;
  const img = new Image();
  // 解决跨域
  img.crossOrigin = 'Anonymous';
  // 优先使用服务器下发的挑战资源
  if (typeof props.fetchChallenge === 'function') {
    try {
      const challenge = await props.fetchChallenge();
      state.src = challenge.src || src;
      state.diffDistance = challenge.diffDistance ?? props.diffDistance ?? 3;
      state.pieceX = challenge.pieceX;
      state.pieceY = challenge.pieceY;
      state.challengeId = challenge.challengeId;
    } catch {
      state.src = src;
    }
  } else {
    state.src = src;
  }
  img.src = state.src;
  img.addEventListener('load', () => {
    draw(puzzleCanvasCtx, pieceCanvasCtx);
    puzzleCanvasCtx.drawImage(img, 0, 0, canvasWidth, canvasHeight);
    pieceCanvasCtx.drawImage(img, 0, 0, canvasWidth, canvasHeight);
    const pieceLength = squareLength + 2 * circleRadius + 3;
    const sx = state.pieceX;
    const sy = state.pieceY - 2 * circleRadius - 1;
    const imageData = pieceCanvasCtx.getImageData(
      sx,
      sy,
      pieceLength,
      pieceLength,
    );
    pieceCanvas.width = pieceLength;
    pieceCanvasCtx.putImageData(imageData, 0, sy);
    setLeft('0');
    state.initialized = true;
  });
}

/**
 * 生成指定范围随机数
 * @function getRandomNumberByRange
 */
function getRandomNumberByRange(start: number, end: number) {
  return Math.round(Math.random() * (end - start) + start);
}

/**
 * 绘制拼图
 * @function draw
 */
function draw(ctx1: CanvasRenderingContext2D, ctx2: CanvasRenderingContext2D) {
  const { canvasWidth, canvasHeight, squareLength, circleRadius } = props;
  // 如果服务端下发了拼图坐标，优先使用服务端坐标
  if (!props.pieceX || !props.pieceY) {
    state.pieceX = getRandomNumberByRange(
      squareLength + 2 * circleRadius,
      canvasWidth - (squareLength + 2 * circleRadius),
    );
    state.pieceY = getRandomNumberByRange(
      3 * circleRadius,
      canvasHeight - (squareLength + 2 * circleRadius),
    );
  } else {
    state.pieceX = props.pieceX;
    state.pieceY = props.pieceY;
  }
  drawPiece(ctx1, state.pieceX, state.pieceY, CanvasOpr.Fill);
  drawPiece(ctx2, state.pieceX, state.pieceY, CanvasOpr.Clip);
}

/**
 * 绘制拼图切块
 * @function drawPiece
 */
function drawPiece(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  opr: CanvasOpr,
) {
  const { squareLength, circleRadius } = props;
  ctx.beginPath();
  ctx.moveTo(x, y);
  ctx.arc(
    x + squareLength / 2,
    y - circleRadius + 2,
    circleRadius,
    0.72 * PI,
    2.26 * PI,
  );
  ctx.lineTo(x + squareLength, y);
  ctx.arc(
    x + squareLength + circleRadius - 2,
    y + squareLength / 2,
    circleRadius,
    1.21 * PI,
    2.78 * PI,
  );
  ctx.lineTo(x + squareLength, y + squareLength);
  ctx.lineTo(x, y + squareLength);
  ctx.arc(
    x + circleRadius - 2,
    y + squareLength / 2,
    circleRadius + 0.4,
    2.76 * PI,
    1.24 * PI,
    true,
  );
  ctx.lineTo(x, y);
  ctx.lineWidth = 2;
  ctx.fillStyle = 'rgba(255, 255, 255, 0.7)';
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.7)';
  ctx.stroke();
  opr === CanvasOpr.Clip ? ctx.clip() : ctx.fill();
  ctx.globalCompositeOperation = 'destination-over';
}

/**
 * 恢复初始状态并重新初始化画布
 * @function resume
 */
function resume() {
  state.showTip = false;
  const basicEl = unref(slideBarRef);
  if (!basicEl) {
    return;
  }
  state.dragging = false;
  state.isPassing = false;
  state.pieceX = 0;
  state.pieceY = 0;

  basicEl.resume();
  resetCanvas();
  initCanvas();
}

/**
 * 组件挂载后初始化
 */
onMounted(() => {
  if (!props.lazyLoad) {
    initCanvas();
  }
});
</script>

<template>
  <div class="relative flex flex-col items-center">
    <div
      class="border-border relative flex cursor-pointer overflow-hidden border shadow-md"
    >
      <template v-if="state.initialized">
        <canvas
          ref="puzzleCanvasRef"
          :width="canvasWidth"
          :height="canvasHeight"
          @click="resume"
        ></canvas>
        <canvas
          ref="pieceCanvasRef"
          :width="canvasWidth"
          :height="canvasHeight"
          :style="pieceStyle"
          class="absolute"
          @click="resume"
        ></canvas>
      </template>
      <template v-else>
        <div
          :style="{ width: `${canvasWidth}px`, height: `${canvasHeight}px` }"
          class="bg-background-deep flex-center w-full"
          @click="handleInitByClick"
        >
          {{ $t('ui.captcha.clickToLoad') || '点击加载验证码' }}
        </div>
      </template>
      <div
        class="h-15 absolute bottom-3 left-0 z-10 block w-full text-center text-xs leading-[30px] text-white"
      >
        <div
          v-if="state.showTip"
          :class="{
            'bg-success/80': state.isPassing,
            'bg-destructive/80': !state.isPassing,
          }"
        >
          {{ verifyTip }}
        </div>
        <div v-if="!state.dragging" class="bg-black/30">
          {{ defaultTip || $t('ui.captcha.sliderTranslateDefaultTip') }}
        </div>
      </div>
    </div>
    <SliderCaptcha
      ref="slideBarRef"
      v-model="modalValue"
      class="mt-5"
      is-slot
      @end="handleDragEnd"
      @move="handleDragBarMove"
      @start="handleStart"
    >
      <template v-for="(_, key) in $slots" :key="key" #[key]="slotProps">
        <slot :name="key" v-bind="slotProps"></slot>
      </template>
    </SliderCaptcha>
  </div>
</template>
/**
 * 点击占位后初始化挑战
 * @function handleInitByClick
 */
function handleInitByClick() {
  if (!state.initialized) {
    initCanvas();
  }
}
