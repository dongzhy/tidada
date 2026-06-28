import { PropsWithChildren } from 'react';
import 'taro-ui/dist/style/index.scss'; // 引入组件样式
import './app.scss';
// eslint-disable-next-line import/first,import/no-duplicates
import Taro from "@tarojs/taro";
// eslint-disable-next-line import/first,import/no-duplicates
import { useLaunch } from '@tarojs/taro';

/**
 * 应用入口组件
 */
function App({ children }: PropsWithChildren) {
  // 正确使用 useLaunch 生命周期 Hook
  useLaunch(async () => {
    try {
      // 调用微信登录接口
      const res: Taro.login.SuccessCallbackResult = await Taro.login();
      console.log('登录返回结果：', res);

      // 登录失败处理（code 为空）
      if (!res.code) {
        Taro.showToast({
          title: '登录失败',
          icon: 'error',
          duration: 2000
        });
        return;
      }

      // todo 拿到 res.code 后调用后端登录接口
      // 示例：
      // const loginRes = await Taro.request({
      //   url: '你的后端登录接口',
      //   method: 'POST',
      //   data: { code: res.code }
      // });
    } catch (error) {
      // 捕获登录接口异常
      console.error('登录接口调用失败：', error);
      Taro.showToast({
        title: '网络异常',
        icon: 'error',
        duration: 2000
      });
    }
  });

  // 必须返回 children，否则页面内容无法渲染
  return children;
}

export default App;
