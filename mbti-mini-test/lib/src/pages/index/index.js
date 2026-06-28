"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const jsx_runtime_1 = require("react/jsx-runtime");
require("taro-ui/dist/style/components/button.scss"); // 按需引入
require("./index.scss");
// eslint-disable-next-line import/first
const components_1 = require("@tarojs/components");
const taro_ui_1 = require("taro-ui");
const GlobalFooter_1 = require("../../components/GlobalFooter");
/**
 * 主页
 */
exports.default = () => {
    return ((0, jsx_runtime_1.jsxs)("view", Object.assign({ className: "indexPage" }, { children: [(0, jsx_runtime_1.jsx)(components_1.View, Object.assign({ className: 'at-article__h1 title' }, { children: "MBTI \u6027\u683C\u6D4B\u8BD5" })), (0, jsx_runtime_1.jsx)(components_1.View, Object.assign({ className: 'at-article__h2 subTitle' }, { children: "\u53EA\u97002\u5206\u949F\uFF0C\u5C31\u80FD\u975E\u5E38\u51C6\u786E\u5730\u63CF\u8FF0\u51FA\u4F60\u7684\u6027\u683C\u7279\u70B9" })), (0, jsx_runtime_1.jsx)(taro_ui_1.AtButton, Object.assign({ type: "primary", circle: true, className: "enterBtn" }, { children: "\u5F00\u59CB\u6D4B\u8BD5" })), (0, jsx_runtime_1.jsx)(components_1.Image, { className: "headerbg", src: "https://cdn.taobao.com/taobao/images/logo.png" }), (0, jsx_runtime_1.jsx)(GlobalFooter_1.default, {})] })));
};
//# sourceMappingURL=index.js.map