import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],

  // Set the base path for GitHub Pages
  base: '/MindfulNet-AI/', 

  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // This is only for local dev
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },

});