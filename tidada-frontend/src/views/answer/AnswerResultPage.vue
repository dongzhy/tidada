<template>
  <div class="answerResultPage">
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col flex="auto" class="content-wrapper">
          <h2>{{ data.resultName || "未命名应用" }}</h2>
          <p>结果描述：{{ data.resultDesc || "无描述" }}</p>
          <p>结果ID：{{ data.resultId || "无ID" }}</p>
          <!-- 🔧 修复：空值兜底，避免访问 undefined 的属性 -->
          <p>应用ID: {{ data.appId || "未知" }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[data.appType ?? 0] || "未设置" }}</p>
          <p>
            评分策略：{{
              APP_SCORING_STRATEGY_MAP[data.scoringStrategy ?? 0] || "未设置"
            }}
          </p>
          <p>
            <a-space>
              作者：
              <div :style="{ display: 'flex', alignItems: 'center' }">
                <a-avatar
                  :size="24"
                  :image-url="data.user?.userAvatar"
                  style="margin-right: 8px"
                />
                <a-typography-text>
                  {{ data.user?.userName ?? "无名" }}
                </a-typography-text>
              </div>
            </a-space>
          </p>
          <p>
            创建时间：{{
              data.createTime
                ? dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss")
                : "未知"
            }}
          </p>
          <a-space size="medium">
            <a-button type="primary" :href="`/answer/do/${data.appId}`">
              去答题
            </a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
          <a-image
            style="width: 100%"
            :src="data.resultPicture || '默认图标地址'"
            fallback="默认图标地址"
          ></a-image>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { defineProps, withDefaults } from "vue";
import message from "@arco-design/web-vue/es/message";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { userLoginUserStore } from "@/store/userStore";
import { getUserAnswerVoByIdUsingGet } from "@/api/userAnswerController";
import { APP_TYPE_MAP } from "@/constant/app";
import { APP_SCORING_STRATEGY_MAP } from "@/constant/app"; // 确保导入
import API from "@/api";

// 1. Props 定义
interface Props {
  id: string;
}
const props = withDefaults(defineProps<Props>(), {
  id: "",
});

// 2. 用户状态
const loginUserStore = userLoginUserStore();
const loginUserId = loginUserStore.loginUser?.id || "";

// 3. 初始化数据（确保所有字段都有默认值，避免 undefined）
const data = ref<API.UserAnswerVO>({
  resultName: "",
  resultDesc: "",
  appId: "",
  appType: 0,
  scoringStrategy: 0,
  user: { userName: "无名", userAvatar: "" },
  createTime: "",
  resultPicture: "",
  resultId: "",
  userId: "",
});

// 4. 权限判断
const isMy = computed(() => {
  return !!loginUserId && loginUserId === data.value.userId;
});

// 5. 加载数据（核心修复：先渲染兜底值，再加载数据）
const loadData = async () => {
  if (!props.id) {
    message.warning("ID不能为空");
    return;
  }

  try {
    const res = await getUserAnswerVoByIdUsingGet({ id: props.id });

    if (res.data.code === 0 && res.data.data) {
      data.value = res.data.data;
      console.log("结果数据加载成功：", data.value);
    } else {
      const msg = res.data.message || "获取数据失败";
      message.error(msg);
    }
  } catch (error) {
    console.error("接口调用异常：", error);
    message.error("网络异常，无法获取结果");
  }
};

// 6. 监听 props.id 变化
watch(
  () => props.id,
  () => loadData(),
  { immediate: true, flush: "post" }
);
</script>

<style scoped>
.answerResultPage {
  padding: 20px;
}
.answerResultPage :deep(.content-wrapper) > * {
  margin-bottom: 30px;
}
.content-wrapper {
  padding-right: 20px;
}
</style>
