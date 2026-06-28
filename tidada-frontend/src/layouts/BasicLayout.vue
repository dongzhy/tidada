<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>

<template>
  <a-layout style="height: 100vh">
    <a-layout-header class="header"><global-header /></a-layout-header>
    <a-layout-content class="content"><router-view /></a-layout-content>
    <a-layout-footer class="footer"> by@zhy </a-layout-footer>
  </a-layout>
</template>

<style scoped>
#basicLayout {
}

#basicLayout .content {
  max-width: 1200px;
  width: 100%;
  box-sizing: border-box;
  margin: 0 auto 28px;
  padding: 20px;
}

.footer {
  padding: 16px;
  text-align: center;
}
</style>
