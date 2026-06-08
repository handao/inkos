import { defineConfig } from 'vite'
import { URL, fileURLToPath } from 'url'
import pkg from '@dcloudio/vite-plugin-uni'
const uni = pkg.default || pkg

export default defineConfig({
  plugins: [uni()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('src', import.meta.url)),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "@/theme/variables.scss";',
        api: 'legacy',
      },
    },
  },
  server: {
    port: 4568,
    proxy: {
      '/api': {
        target: 'http://localhost:4569',
        changeOrigin: true,
      },
    },
  },
})
