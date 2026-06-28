<template>
  <div class="appDetailPage">
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col flex="auto" class="content-warpper">
          <!-- 空值兜底：避免数据未加载时显示 undefined -->
          <h2>{{ data.appName || "未命名应用" }}</h2>
          <p>{{ data.appDesc || "无应用描述" }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[data.appType] || "未设置" }}</p>
          <p>
            评分策略：{{
              APP_SCORING_STRATEGY_MAP[data.scoringStrategy] || "未设置"
            }}
          </p>
          <p>
            <a-space>
              作者：
              <div :style="{ display: 'flex', alignItems: 'center' }" />
              <a-avatar
                :size="24"
                :image-url="data.user?.userAvatar"
                :style="{ marginRight: '8px' }"
              />
              <a-typography-text>
                {{ data.user?.userName ?? "无名" }}
              </a-typography-text>
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
            <a-button type="primary" :href="`/answer/do/${props.id}`">
              开始答题
            </a-button>
            <a-button @click="doShare"> 分享应用 </a-button>

            <!-- 修复：跳转路径传递正确的 appId（props.id） -->
            <a-button v-if="isMy" :href="`/add/question/${props.id}`">
              设置题目
            </a-button>
            <a-button v-if="isMy" :href="`/add/scoring_result/${props.id}`">
              设置评分
            </a-button>
            <a-button v-if="isMy" :href="`/add/app/${props.id}`">
              修改应用
            </a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
          <!-- 图片空值兜底 -->
          <a-image
            style="width: 100%"
            :src="data.appIcon || '默认图标地址'"
            fallback="默认图标地址"
          ></a-image>
        </a-col>
      </a-row>
    </a-card>
    <ShareModal :link="shareLink" :title="应用分享" ref="shareModelRef" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted } from "vue";
import { defineProps, withDefaults } from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { userLoginUserStore } from "@/store/userStore";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import ShareModal from "@/components/ShareModal.vue";

// 1. Props 定义：明确 id 为必填字符串
interface Props {
  id: string;
}
const props = withDefaults(defineProps<Props>(), {
  id: "",
});

// 2. 状态管理：获取登录用户信息
const loginUserStore = userLoginUserStore();
const loginUserId = loginUserStore.loginUser?.id || "";

// 3. 响应式数据：初始化 AppVO 类型，避免空对象警告
const data = ref<API.AppVO>({
  appName: "",
  appDesc: "",
  appType: 0,
  scoringStrategy: 0,
  user: {},
  createTime: "",
  appIcon: "",
});

// 4. 计算属性：判断是否为当前用户创建的应用
const isMy = computed(() => {
  return loginUserId && loginUserId === data.value.userId;
});

/**
 * 5. 加载数据：修复传参 + 异常捕获 + 空值处理
 */
const loadData = async () => {
  // 校验 ID 有效性
  if (!props.id || props.id.trim() === "") {
    message.warning("应用ID不能为空");
    return;
  }

  try {
    // 传参：直接传递字符串 ID，避免类型转换丢失精度
    const res = await getAppVoByIdUsingGet({
      id: props.id,
    });

    if (res.data.code === 0 && res.data.data) {
      data.value = res.data.data;
    } else {
      const errorMsg = res.data.message || "获取应用详情失败";
      message.error(errorMsg);
      // 若数据不存在，可选择跳转
      if (errorMsg.includes("不存在")) {
        setTimeout(() => (window.location.href = "/app/list"), 1500);
      }
    }
  } catch (error) {
    console.error("加载应用详情失败：", error);
    message.error("网络异常，无法获取应用详情");
  }
};

/**
 * 6. 监听逻辑：只监听 props.id 变化，精准触发加载
 */
watch(
  () => props.id, // 监听应用ID变化
  () => {
    loadData();
  },
  {
    immediate: true, // 组件初始化时立即执行
    flush: "post", // 等待 DOM 更新后执行
  }
);

//分享弹窗引用
const shareModelRef = ref();
//分享链接
const shareLink = `${window.location.protocol}//${window.location.host}/app/detail/${props.id}`;
//分享
const doShare = (e: Event) => {
  if (shareModelRef.value) {
    shareModelRef.value.openModal();
  }
  //阻止冒泡，防止跳转到详情页
  e.stopPropagation();
};
</script>

<style scoped>
.appDetailPage {
  padding: 20px;
}
#appDetailPage .content-warp > * {
  margin-bottom: 30px;
}
.content-warpper {
  padding-right: 20px;
}
</style>
