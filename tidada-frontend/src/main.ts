import { createApp } from "vue";
import ArcoVue from "@arco-design/web-vue";
import App from "./App.vue";
import "@arco-design/web-vue/dist/arco.css";
import router from "@/router";
import "@/access";
import { createPinia } from "pinia";
const pinia = createPinia();

createApp(App).use(router).use(pinia).use(ArcoVue).mount("#app");
