import type { RouteRecordRaw } from "vue-router";
import ACCESS_ENUM from "@/access/accessEnum";
import AppStatisticPage from "@/statistic/AppStatisticPage.vue";

// ✅ 核心修复1：所有组件统一懒加载，避免循环依赖
export const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "主页",
    component: () => import("../views/HomePage.vue"),
  },
  {
    path: "/add/app",
    name: "创建应用",
    component: () => import("@/views/AddAppPage.vue"),
  },
  {
    path: "/add/app/:appId",
    name: "修改应用",
    props: true,
    component: () => import("@/views/AddAppPage.vue"),
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/app_statistic",
    name: "应用统计",
    component: () => import("../../src/statistic/AppStatisticPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/add/question/:appId",
    name: "创建题目",
    component: () => import("@/views/AddQuestionPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/scoring_result/:appId",
    name: "创建评分",
    component: () => import("@/views/AddScoringResultPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/noAuth",
    name: "无权限",
    component: () => import("@/views/noAuthPage.vue"),
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/admin/user",
    name: "用户管理",
    component: () => import("@/views/admin/AdminUserPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/app",
    name: "应用管理",
    props: true,
    component: () => import("@/views/admin/AdminAppPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/question",
    name: "题目管理",
    component: () => import("@/views/admin/AdminQuestionPage.vue"),
    props: true,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/scoring_result",
    name: "评分管理",
    component: () => import("@/views/admin/AdminScoringResultPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/user_answer",
    name: "回答管理",
    component: () => import("@/views/admin/AdminUserAnswerPage.vue"),
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/hide",
    name: "隐藏页面",
    component: () => import("../views/HomePage.vue"),
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/app/detail/:id",
    name: "应用详情",
    component: () => import("@/views/AppDetailPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/user",
    name: "用户",
    component: () => import("@/layouts/UserLayout.vue"),
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: () => import("@/views/user/UserLoginPage.vue"),
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: () => import("@/views/user/UserRegisterPage.vue"),
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  // ✅ 核心修复3：答题相关组件改为懒加载
  {
    path: "/answer/do/:appId",
    name: "答题",
    component: () => import("@/views/answer/DoAnswerPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/result/:id",
    name: "答题结果",
    component: () => import("@/views/answer/AnswerResultPage.vue"),
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/my",
    name: "我的答题",
    component: () => import("@/views/answer/MyAnswerPage.vue"),
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
];
