import router from "@/router";
// 注意：这里要和你的 userStore.ts 导出名称保持一致（如果还是 userLoginUserStore 就不改）
import { userLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";
import type { RouteLocationNormalized, NavigationGuardNext } from "vue-router";
// 只保留一份导入（删除冗余的 AccessEnum/accessEnum 导入）
import ACCESS_ENUM from "@/access/accessEnum";

// 核心修复：提取 ACCESS_ENUM 常量对象的类型
// keyof typeof ACCESS_ENUM 获取对象的所有键，再通过 typeof 拿到对应值的类型
type AccessType = (typeof ACCESS_ENUM)[keyof typeof ACCESS_ENUM];

// 标记是否正在获取用户信息，避免重复请求
let isFetchingUser = false;

// 路由前置守卫：权限校验
router.beforeEach(
  async (
    to: RouteLocationNormalized,
    from: RouteLocationNormalized,
    next: NavigationGuardNext
  ) => {
    // 1. 初始化Pinia store
    const loginUserStore = userLoginUserStore();
    let { loginUser } = loginUserStore;

    // 2. 仅当用户信息未初始化且未在请求中时，获取用户信息
    const isUserInfoUninitialized = !loginUser?.userRole;
    if (isUserInfoUninitialized && !isFetchingUser) {
      isFetchingUser = true;
      try {
        await loginUserStore.fetchLoginUser();
        loginUser = loginUserStore.loginUser;
      } catch (error) {
        console.error("获取用户信息失败：", error);
        // 兜底为未登录状态（类型匹配）
        loginUser = {
          userName: "未登录",
          userRole: ACCESS_ENUM.NOT_LOGIN,
        };
      } finally {
        isFetchingUser = false;
      }
    }

    // 3. 获取当前页面需要的权限（核心修复：用 AccessType 替代直接用 ACCESS_ENUM 作为类型）
    const needAccess = (to.meta?.access as AccessType) ?? ACCESS_ENUM.NOT_LOGIN;

    // 4. 权限校验核心逻辑
    if (
      needAccess !== ACCESS_ENUM.NOT_LOGIN &&
      loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
    ) {
      next({
        path: "/user/login",
        query: { redirect: to.fullPath },
      });
      return;
    }

    if (!checkAccess(loginUser, needAccess)) {
      next("/noAuth");
      return;
    }

    next();
  }
);
