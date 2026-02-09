import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
    dedupe: ['react', 'react-dom'],
  },
  build: {
    // Code splitting configuration
    rollupOptions: {
      output: {
        manualChunks(id) {
          // Feature chunks based on path (lazy loaded routes) - check first
          if (id.includes('/features/products/')) {
            return 'products';
          }
          if (id.includes('/features/rawMaterials/')) {
            return 'raw-materials';
          }
          if (id.includes('/features/production/')) {
            return 'production';
          }
          // Vendor chunks - separate large libraries
          if (id.includes('node_modules')) {
            // CRITICAL: React core libraries MUST be in main bundle (index.js)
            // This ensures React is ALWAYS loaded first, before any other chunk
            // Return undefined to include React in the main bundle
            // This prevents useSyncExternalStore errors by ensuring React is available
            // before any other library tries to use it
            if (
              id.includes('react') ||
              id.includes('react-dom') ||
              id.includes('react-router') ||
              id.includes('react-redux') ||
              id.includes('scheduler')
            ) {
              // Return undefined to include in main bundle, ensuring it loads first
              return undefined;
            }
            // Redux - depends on React, must come after React (which is in main bundle)
            if (id.includes('@reduxjs') || id.includes('redux')) {
              return 'redux-vendor';
            }
            // MUI and Emotion - keep together but separate from React
            // These depend on React but React is already in main bundle
            if (id.includes('@mui') || id.includes('@emotion')) {
              return 'mui-vendor';
            }
            // All other node_modules - EXCLUDE anything that might depend on React
            // This is critical: vendor chunk must NOT contain React dependencies
            // Only include libraries that are completely independent of React
            if (
              !id.includes('react') &&
              !id.includes('react-dom') &&
              !id.includes('react-router') &&
              !id.includes('react-redux') &&
              !id.includes('scheduler') &&
              !id.includes('@reduxjs') &&
              !id.includes('redux') &&
              !id.includes('@mui') &&
              !id.includes('@emotion')
            ) {
              return 'vendor';
            }
          }
        },
      },
    },
    // Optimize chunk size
    chunkSizeWarningLimit: 1000,
    // Enable source maps for production debugging (optional)
    sourcemap: false,
  },
  // Optimize dependencies
  optimizeDeps: {
    include: ['react', 'react-dom', 'react-router-dom', '@mui/material', '@reduxjs/toolkit'],
    exclude: [],
    esbuildOptions: {
      target: 'es2020',
    },
  },
})
