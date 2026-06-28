<template>
  <a-row class="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :default-selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="titleBar">
            <div class="title">题答答</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibalRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu></a-col
    >
    <a-col flex="100px">
      <div v-if="loginUserStore.loginUser.id">
        {{ loginUserStore.loginUser.userName ?? "无名" }}
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div></a-col
    >
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { userLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";
const router = useRouter();

const loginUserStore = userLoginUserStore();
//当前选中菜单项
const selectedKeys = ref(["/"]);
//路由跳转时，自动更新选中的菜单项
router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});
//点击菜单跳转
const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};
//展示菜单栏的路由数组
const visibalRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    //根据权限过滤菜单
    return checkAccess(loginUserStore.loginUser, item.meta?.access as string);
  });
});
</script>

<style scoped>
#globalHeader {
}

#globalHeader .header {
  margin-bottom: 16px;
  box-shadow: #eee 1px 1px 5px;
}
.title {
  color: black;
}
</style>
