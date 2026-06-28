<template>
  <div id="addScoringResultPage">
    <h2 style="margin-bottom: 32px">创建评分结果</h2>
    <a-form
      :model="form"
      :style="{ width: '480px' }"
      label-align="left"
      auto-label-width
      @submit="handleSubmit"
    >
      <a-form-item field="appName" label="应用id" disabled>
        {{ appId }}
      </a-form-item>
      <a-form-item field="resultName" label="结果名称">
        <a-input v-model="form.resultName" placeholder="请输入结果名称" />
      </a-form-item>
      <a-form-item field="resultDesc" label="结果描述">
        <a-input v-model="form.resultDesc" placeholder="请输入结果描述" />
      </a-form-item>
      <a-form-item field="resultPicture" label="结果图标">
        <a-input
          v-model="form.resultPicture"
          placeholder="请输入结果图标地址"
        />
      </a-form-item>
      <a-form-item field="resultProp" label="结果集">
        <a-input-tag
          v-model="form.resultProp"
          :style="{ width: '320px' }"
          placeholder="输入结果集"
          allow-clear
        />
      </a-form-item>
      <a-form-item field="resultScoreRange" label="结果得分范围">
        <a-input-number
          v-model="form.resultScoreRange"
          placeholder="请输入结果得分范围"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          提交
        </a-button>
        <!-- 新增：重置表单按钮 -->
        <a-button @click="resetForm" style="margin-left: 10px">重置</a-button>
      </a-form-item>
    </a-form>
  </div>
  <h2>评分管理</h2>

  <ScoringResultTable :appId="appId" :doUpdate="doUpdate" ref="tableRef" />
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import {
  addScoringResultUsingPost,
  updateScoringResultUsingPost,
} from "@/api/scoringResultController";
import { defineProps, withDefaults } from "vue"; // 修正导入路径
import ScoringResultTable from "@/components/ScoringResultTable.vue";

const tableRef = ref();

// 定义Props
interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});

// 初始化表单默认值（抽离成常量，方便重置）
const initForm = {
  resultDesc: "",
  resultName: "",
  resultPicture: "",
  resultProp: [], // 注意：a-input-tag 绑定的是数组类型
  resultScoreRange: undefined,
} as API.ScoringResultAddRequest;

// 表单数据
const form = reactive({ ...initForm });

// 用于判断是否更新
let updateId = ref<number | undefined>(undefined);

/**
 * 修改 - 回显数据到表单
 * @param scoringResult
 */
const doUpdate = (scoringResult: API.ScoringResultVO) => {
  updateId.value = scoringResult.id;
  // 深拷贝避免原数据关联
  form.resultDesc = scoringResult.resultDesc || "";
  form.resultName = scoringResult.resultName || "";
  form.resultPicture = scoringResult.resultPicture || "";
  form.resultProp = scoringResult.resultProp
    ? [...scoringResult.resultProp]
    : [];
  form.resultScoreRange = scoringResult.resultScoreRange;
};

/**
 * 重置表单为初始状态
 */
const resetForm = () => {
  // 遍历重置每个字段
  Object.keys(initForm).forEach((key) => {
    (form as any)[key] = Array.isArray((initForm as any)[key])
      ? []
      : (initForm as any)[key];
  });
  updateId.value = undefined; // 清空编辑ID
};

/**
 * 提交
 */
const handleSubmit = async () => {
  if (!props.appId) {
    message.warning("应用ID不能为空");
    return;
  }
  try {
    let res;
    // 区分创建和修改
    if (updateId.value) {
      res = await updateScoringResultUsingPost({
        id: updateId.value,
        ...form,
      });
    } else {
      res = await addScoringResultUsingPost({
        appId: props.appId as any,
        ...form,
      });
    }

    if (res.data.code === 0) {
      message.success(updateId.value ? "修改成功" : "创建成功");

      // 核心1：刷新表格数据
      if (tableRef.value) {
        tableRef.value.loadData();
      }

      // 核心2：重置表单数据
      resetForm();
    } else {
      message.error(`操作失败：${res.data.message || "未知错误"}`);
    }
  } catch (err) {
    console.error("提交异常：", err);
    message.error("提交请求异常，请检查网络或接口");
  }
};
</script>
