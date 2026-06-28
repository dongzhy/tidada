import "taro-ui/dist/style/components/button.scss"; // 按需引入
import "./index.scss";
// eslint-disable-next-line import/first
import { View } from "@tarojs/components";
// eslint-disable-next-line import/first
import { AtButton } from "taro-ui";
import GlobalFooter from "../../components/GlobalFooter";
// eslint-disable-next-line import/first
import Taro from "@tarojs/taro";

/**
 * 主页
 */
export default () => {
  return (
    <view className="indexPage">
      <View className="at-article__h1 title">MBTI 性格测试</View>
      <View className="at-article__h2 subTitle">
        只需2分钟，就能非常准确地描述出你的性格特点
      </View>
      <AtButton
        type="primary"
        circle
        className="enterBtn"
        onClick={() => {
          Taro.navigateTo({
            url: "/pages/doQuestion/index",
          });
        }}
      >
        开始测试
      </AtButton>
      <GlobalFooter />
    </view>
  );
};
