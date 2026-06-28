<template>
  <div class="page-box">
    <h2>评分结果管理</h2>
    <a-table
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
    >
      <template #resultPicture="{ record }">
        <a-image
          v-if="record.resultPicture"
          width="64"
          :src="record.resultPicture"
        />
        <span v-else>无图</span>
      </template>
      <template #createTime="{ record }">
        {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
      <template #updateTime="{ record }">
        {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button
            @click="
              () => {
                console.log('点击修改按钮原生触发', record);
                handleEdit(record);
              }
            "
            >修改</a-button
          >
          <a-button status="danger" @click="doDelete(record)">删除</a-button>
        </a-space>
      </template>
    </a-table>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:visible="editModalVisible"
      title="编辑评分结果"
      width="600px"
      @ok="submitEditForm"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="结果名称" field="resultName">
          <a-input v-model="editForm.resultName" placeholder="请输入结果名称" />
        </a-form-item>
        <a-form-item label="结果描述" field="resultDesc">
          <a-textarea
            v-model="editForm.resultDesc"
            rows="3"
            placeholder="请输入描述"
          />
        </a-form-item>
        <a-form-item label="得分范围" field="resultScoreRange">
          <a-input-number
            v-model="editForm.resultScoreRange"
            placeholder="如80代表80分以上命中"
          />
        </a-form-item>
        <a-form-item label="结果图片地址" field="resultPicture">
          <a-input v-model="editForm.resultPicture" placeholder="图片链接" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, watchEffect } from "vue";
import {
  deleteScoringResultUsingPost,
  listScoringResultVoByPageUsingPost,
  getScoringResultVoByIdUsingGet,
  editScoringResultUsingPost,
} from "@/api/scoringResultController";

import message from "@arco-design/web-vue/es/message";
import dayjs from "dayjs";
import API from "@/api";

// 页面所属应用ID
const currentAppId = ref("");

// 列表查询参数
const initSearchParams: API.ScoringResultQueryRequest = {
  current: 1,
  pageSize: 10,
  sortField: "createTime",
  sortOrder: "descend",
};
const searchParams = ref<API.ScoringResultQueryRequest>({
  ...initSearchParams,
});
const dataList = ref<API.ScoringResultVO[]>([]);
const total = ref<number>(0);

// 编辑弹窗状态 + 表单
const editModalVisible = ref(false);
const editForm = ref<API.ScoringResultEditRequest>({
  id: undefined,
  resultName: "",
  resultDesc: "",
  resultPicture: "",
  resultProp: [],
  resultScoreRange: undefined,
});

/** 加载表格数据 */
const loadData = async () => {
  const params = {
    ...searchParams.value,
    appId: currentAppId.value ? Number(currentAppId.value) : undefined,
  };
  const res = await listScoringResultVoByPageUsingPost(params);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
    dataList.value = [];
    total.value = 0;
  }
};

/** 分页切换 */
const onPageChange = (page: number) => {
  searchParams.value.current = page;
};

/** 删除操作 */
const doDelete = async (record: API.ScoringResultVO) => {
  if (!record.id) return;
  const res = await deleteScoringResultUsingPost({ id: record.id });
  if (res.data.code === 0) {
    message.success("删除成功");
    loadData();
  } else {
    message.error("删除失败，" + res.data.message);
  }
};

/** 点击修改按钮，打开弹窗并回显数据 */
const handleEdit = async (record: API.ScoringResultVO) => {
  console.log("===== handleEdit 执行 =====", record);
  // 根据id查询完整详情
  const res = await getScoringResultVoByIdUsingGet({ id: record.id });
  if (res.data.code === 0) {
    const voData = res.data.data!;
    editForm.value = {
      id: voData.id,
      resultName: voData.resultName,
      resultDesc: voData.resultDesc,
      resultPicture: voData.resultPicture,
      resultProp: voData.resultProp ?? [],
      resultScoreRange: voData.resultScoreRange,
    };
    editModalVisible.value = true;
  } else {
    message.error("获取编辑详情失败：" + res.data.message);
  }
};

/** 弹窗确认提交编辑接口 */
const submitEditForm = async () => {
  const res = await editScoringResultUsingPost(editForm.value);
  if (res.data.code === 0) {
    message.success("修改成功！");
    editModalVisible.value = false;
    loadData(); // 修改后刷新列表
  } else {
    message.error("修改失败：" + res.data.message);
  }
};

// appId变化重置第一页刷新
watch(
  () => currentAppId.value,
  () => {
    searchParams.value.current = 1;
    loadData();
  },
  { immediate: true }
);

// 分页、排序变动刷新列表
watchEffect(() => {
  const { current, pageSize, sortField, sortOrder } = searchParams.value;
  void current;
  void pageSize;
  void sortField;
  void sortOrder;
  loadData();
});

// 表格列配置
const columns = [
  { title: "id", dataIndex: "id" },
  { title: "名称", dataIndex: "resultName" },
  { title: "描述", dataIndex: "resultDesc" },
  { title: "图片", dataIndex: "resultPicture", slotName: "resultPicture" },
  { title: "结果属性", dataIndex: "resultProp" },
  { title: "评分范围", dataIndex: "resultScoreRange" },
  { title: "应用 id", dataIndex: "appId" },
  { title: "用户 id", dataIndex: "userId" },
  { title: "创建时间", dataIndex: "createTime", slotName: "createTime" },
  { title: "更新时间", dataIndex: "updateTime", slotName: "updateTime" },
  { title: "操作", slotName: "optional" },
];
</script>
