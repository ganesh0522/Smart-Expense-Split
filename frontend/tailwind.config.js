/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#2563eb",
        success: "#22c55e",
        danger: "#ef4444",
      },
    },
  },
  plugins: [],
};