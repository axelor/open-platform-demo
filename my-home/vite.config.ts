import react from "@vitejs/plugin-react";
import path from "node:path";
import { defineConfig } from "vite";

export default defineConfig({
  base: "./",
  plugins: [react()],
  define: {
    __WS_BASE__: JSON.stringify(
      process.env.NODE_ENV === "production" ? "" : "../",
    ),
  },
  optimizeDeps: {
    entries: ["src/**/*.{ts,js,tsx,jsx,css,scss,html}"],
  },
  resolve: {
    alias: [
      {
        find: /^~(.*)/,
        replacement: "$1",
      },
      {
        find: /^@\/(.*)/,
        replacement: path.join(import.meta.dirname, "src", "$1"),
      },
    ],
  },
  build: {
    target: ["es2022"],
    rolldownOptions: {
      checks: {
        // Don't report plugin timings (mostly CSS processing of @axelor/ui styles)
        bundlerTimings: false,
      },
      output: {
        codeSplitting: {
          // Keep @axelor/ui dependencies in the "lib" chunk instead of pulling them into "axelor-ui"
          includeDependenciesRecursively: false,
          minSize: 100000, // 100KB global minimum chunk size to avoid small artifacts
          groups: [
            {
              name: "react",
              test: /[\\/]node_modules[\\/](react|react-dom|scheduler)[\\/]/,
              priority: 20,
            },
            {
              name: "axelor-ui",
              test: /[\\/]node_modules[\\/]@axelor[\\/]ui[\\/]/,
              priority: 15,
            },
            {
              name: "lib",
              test: /[\\/]node_modules[\\/]/,
              priority: 10,
            },
          ],
        },
      },
    },
  },
});
