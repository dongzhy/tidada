import Taro from "@tarojs/taro";
import { View } from "@tarojs/components";
import { AtButton } from "taro-ui";
import "taro-ui/dist/style/components/button.scss";
import GlobalFooter from "../../components/GlobalFooter";
import { getBestQuestionResult } from "../../../utils/bizUtils";
// 注意：确认 JSON 文件路径和格式是否正确
// eslint-disable-next-line import/no-duplicates
import questions from "../../pages/data/questions_result.json";
// eslint-disable-next-line import/no-duplicates
import questionResults from "../../pages/data/questions_result.json";
import "./index.scss";

export default () => {
  const answerList = Taro.getStorageSync("answerList");

  // 1. 提前校验答案
  if (!answerList || answerList.length < 1) {
    Taro.showToast({ title: "答案为空", icon: "error", duration: 3000 });
    return (
      <View className="resultPage">
        <GlobalFooter />
      </View>
    );
  }

  // 2. 校验 JSON 数据格式（调试用，上线可移除）
  console.log('题库数据：', questions);
  console.log('结果数据：', questionResults);
  if (!Array.isArray(questions)) {
    Taro.showToast({ title: "题库数据格式错误", icon: "error" });
    return (
      <View className="resultPage">
        <GlobalFooter />
      </View>
    );
  }

  // 3. 调用函数时增加异常捕获
  let result = null;
  try {
    result = getBestQuestionResult(answerList, questions, questionResults) || {};
  } catch (e) {
    console.error('计算结果失败：', e);
    Taro.showToast({ title: "结果计算失败", icon: "error" });
    result = { resultName: "暂无结果", resultDesc: "数据解析异常" };
  }

  return (
    <View className="resultPage">
      <View className="at-article__h1 title">{result.resultName || "暂无结果"}</View>
      <View className="at-article__h2 subTitle">{result.resultDesc || "暂无描述"}</View>
      <AtButton
        type="primary"
        circle
        className="enterBtn"
        onClick={() => Taro.reLaunch({ url: "/pages/index/index" })}
      >
        返回主页
      </AtButton>
      <GlobalFooter />
    </View>
  );
};
