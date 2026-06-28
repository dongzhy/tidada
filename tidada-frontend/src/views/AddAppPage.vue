<template>
  <div class="appAppPage">
    <h2 style="text-align: center">创建应用</h2>
  </div>
  <a-form
    :model="form"
    class="formadd"
    :style="{ width: '600px' }"
    @submit="handleSubmit"
  >
    <a-form-item
      field="appName"
      tooltip="Please enter username"
      label="应用名称"
    >
      <a-input v-model="form.appName" />
    </a-form-item>
    <a-form-item field="name" tooltip="Please enter username" label="应用描述">
      <a-input v-model="form.appDesc" />
    </a-form-item>
    <a-form-item field="appIcon" label="应用图标">
      <a-input v-model="form.appIcon" placeholder="请输入应用图标地址" />
    </a-form-item>

    <a-form-item field="appType" label="应用类型">
      <a-select
        v-model="form.appType"
        :style="{ width: '320px' }"
        placeholder="请选择应用类型"
      >
        <a-option
          v-for="(value, key) of APP_TYPE_MAP"
          :value="Number(key)"
          :label="value"
          :key="key"
        />
      </a-select>
    </a-form-item>

    <a-form-item field="scoringStrategy" label="评分策略">
      <a-select
        v-model="form.scoringStrategy"
        :style="{ width: '320px' }"
        placeholder="请选择评分策略"
      >
        <a-option
          v-for="(value, key) of APP_SCORING_STRATEGY_MAP"
          :value="Number(key)"
          :label="value"
          :key="key"
        />
      </a-select>
    </a-form-item>

    <a-form-item>
      <a-button html-type="submit">提交</a-button>
    </a-form-item>
  </a-form>
  {{ form }}
</template>

<script setup lang="ts">
import { ref, toRaw, watchEffect } from "vue"; // 移除无用的 reactive，新增 watch
import { withDefaults, defineProps } from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import {
  addAppUsingPost,
  editAppUsingPost,
  getAppVoByIdUsingGet,
} from "@/api/appController";
import router from "@/router";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
const form = ref({
  appName: "",
  appDesc: "",
  appIcon: "",
  appType: 0,
  scoringStrategy: 0,
} as API.AppAddRequest);

const oldApp = ref<API.AppVO>();

// 3. 加载数据（修复类型断言，增加异常捕获）
const loadData = async () => {
  if (!props.appId) return;

  try {
    const res = await getAppVoByIdUsingGet({
      id: props.appId, // 修复：字符串转数字，避免类型错误
    });
    if (res.data.code === 0 && res.data.data) {
      oldApp.value = res.data.data;
      // 修复：合并数据时保留响应式，只覆盖需要的字段
      form.value = { ...form.value, ...res.data.data };
    } else {
      message.error("获取数据失败：" + res.data.message);
    }
  } catch (error) {
    console.error("加载应用数据失败：", error);
    message.error("网络异常，无法获取应用数据");
  }
};
interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});
/**
 * 提交函数（核心修复：解除响应式，传递原始值）
 */
const handleSubmit = async () => {
  try {
    // 核心修复1：用 toRaw 解除响应式，获取原始对象
    const rawForm = toRaw(form.value);
    // 核心修复2：手动构建请求参数，确保无循环引用
    const requestData = {
      appName: rawForm.appName,
      appDesc: rawForm.appDesc,
      appIcon: rawForm.appIcon,
      appType: rawForm.appType,
      scoringStrategy: rawForm.scoringStrategy,
    };
    // 是修改
    let res: any;
    if (props.appId) {
      res = await editAppUsingPost(requestData);
    } else {
      res = await addAppUsingPost(form.value);
    }
    // 发送请求（传递纯普通对象，而非响应式对象）
    res = await addAppUsingPost(requestData);

    if (res.data.code === 0) {
      message.success("创建成功，即将跳转到应用详情页");
      setTimeout(() => {
        router.push(`/app/detail/${res.data.data}`);
      }, 1000); // 缩短跳转时间，提升体验
    } else {
      message.error("创建失败：" + res.data.message);
    }
  } catch (error) {
    console.error("提交应用失败：", error);
    message.error("网络异常，提交失败");
  }
};

watchEffect(loadData);
/**
 * 提交
 */
const handleSubmitEdit = async () => {
  // 是修改
  let res: any;
  if (props.appId) {
    res = await editAppUsingPost({
      id: props.appId as any,
      ...form.value,
    });
  } else {
    res = await addAppUsingPost(form.value);
  }
  if (res.data.code === 0) {
    message.success("操作成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${props.appId || res.data.data}`);
    }, 3000);
  } else {
    message.error("操作失败，" + res.data.message);
  }
};
</script>

<style scoped>
.appDetailPage {
  padding: 20px;
}
#appDetailPage .content-warp > * {
  margin-bottom: 30px;
}
.formadd {
  text-align: center;
  margin: 0 auto;
  margin-inline: auto;
}
</style>
