import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Vite base config. Backend wiring stays deferred; this only prepares the app shell.
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5173,
  },
});
