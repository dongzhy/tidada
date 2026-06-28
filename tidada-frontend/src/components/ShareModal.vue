<template>
  <a-modal v-model:visible="visible" :footer="false" @cancel="closeModal">
    <template #title>
      {{ title }}
    </template>
    <h4 style="margin-top: 0">复制分享链接</h4>
    <a-typography-paragraph copyable>{{ link }}</a-typography-paragraph>
    <h4>手机扫码查看</h4>
    <img :src="codeImg" v-if="codeImg" />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import QRCode from "qrcode"; // 已安装 @types/qrcode 的情况下不需要 @ts-ignore
import { Message } from "@arco-design/web-vue"; // Arco Design 推荐引入方式

// defineProps / defineExpose 是编译器宏，不需要导入
const props = defineProps({
  link: {
    type: String,
    required: false,
    default: "https://laoyujianli.com/share/yupi",
  },
  title: {
    type: String,
    required: false,
    default: "分享",
  },
});

const codeImg = ref<string>("");
const visible = ref<boolean>(false);

const openModal = () => {
  visible.value = true;
};

// 暴露给父组件
defineExpose({
  openModal,
});

const closeModal = () => {
  visible.value = false;
};

const generateQRCode = (link: string) => {
  QRCode.toDataURL(link)
    .then((url: string) => {
      codeImg.value = url;
    })
    .catch((err: Error) => {
      console.error("二维码生成失败：", err);
      Message.error(`生成二维码失败：${err.message}`);
    });
};

generateQRCode(props.link);

watch(
  () => props.link,
  (newLink) => {
    generateQRCode(newLink);
  }
);
</script>

<style scoped></style>
