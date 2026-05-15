import { reactRouter } from "@react-router/dev/vite";
import { defineConfig } from "vite";

export default defineConfig({
  base: "/new/",

  plugins: [reactRouter()],

  resolve: {
    tsconfigPaths: true,
  },

  server: {
    proxy: {
      "/api": {
        target: "https://localhost:8443",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});