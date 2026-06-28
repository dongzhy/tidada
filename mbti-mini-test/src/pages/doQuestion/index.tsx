import "taro-ui/dist/style/components/button.scss"; // 按需引入
import "./index.scss";
// eslint-disable-next-line import/first
import { View } from "@tarojs/components";
import questions from "../data/questions.json";
// eslint-disable-next-line import/first
import { AtButton, AtRadio } from "taro-ui";
// eslint-disable-next-line import/first
import { useEffect, useState } from "react";
// eslint-disable-next-line import/first
import Taro from "@tarojs/taro";

/**
 * 主页
 */
export default () => {
  //当前题目序号，从1开始
  const [current, setCurrent] = useState<number>(1);
  //当前题目
  const [currentQuestion, setCurrentQuestion] = useState(questions[0]);
  const questionOptions: any = currentQuestion.options.map((option) => {
    return {
      label: `${option.key}.${option.value}`,
      value: option.key,
    };
  });
  //当前答案
  const [currentAnswer, setCurrentAnswer] = useState<string>();
  //回答列表
  const [answerList] = useState<string[]>([]);
  //序号变化时，要切换当前题目和当前回答
  useEffect(() => {
    setCurrentQuestion(questions[current - 1]);
    setCurrentAnswer(answerList[current - 1]);
  }, [current]);
  return (
    <view className="doQuestionPage">
      {JSON.stringify(answerList)}
      <View className="at-article__h2 title">
        {current}、{currentQuestion.title}
      </View>
      <view className="options-wrapper">
        <AtRadio
          options={questionOptions}
          value={currentAnswer}
          onClick={(value) => {
            setCurrentAnswer(value);
            answerList[current - 1] = value;
          }}
        />
      </view>


      {current < questions.length && (
        <AtButton
          type="primary"
          circle
          disabled={!currentAnswer}
          className="controlBtn"
          onClick={() => {
            setCurrent(current + 1);
          }}
        >
          下一题
        </AtButton>
      )}
      {current == questions.length && (
        <AtButton
          type="primary"
          circle
          className="controlBtn"
          disabled={!currentAnswer}
          onClick={() => {
            //传递答案
            Taro.setStorageSync('answerList', answerList);
            //todo 跳转结果页面
            Taro.navigateTo({
              url: '/pages/result/index',
            })
          }}
        >
          查看结果
        </AtButton>
      )}
      {current > 1 && (
        <AtButton
          circle
          className="controlBtn"
          onClick={() => {
            setCurrent(current - 1);
          }}
        >
          上一题
        </AtButton>
      )}
      </view>

  );
};
