import { defineConfig } from '@vben/vite-config';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      server: {
        proxy: {
          '/api': {
            changeOrigin: true,
            // 开发环境代理到后端服务
            target: 'http://localhost:8080',
            ws: true,
          },
        },
      },
    },
  };
});
