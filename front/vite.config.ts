import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

export default defineConfig({
  plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
  server: {
    host: true,
    port: 5173,
    allowedHosts: ["localhost", "frontend", "0.0.0.0"],
    watch: {
      usePolling: true,
    },
    hmr: {
      port: 5173,
    },
  },
});
