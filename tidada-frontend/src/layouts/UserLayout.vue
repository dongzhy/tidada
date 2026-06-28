<script setup lang="ts"></script>

<template>
  <div id="userLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <div>题答答AI答题应用平台</div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content"><router-view /></a-layout-content>
      <a-layout-footer class="footer"> by@zhy </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#userLayout .header {
  margin-top: 16px;
  font-size: 21px;
  text-align: center;
}

#userLayout .content {
  margin-bottom: 28px;
  padding: 20px;
}

.footer {
  padding: 16px;
  text-align: center;
}
</style>
