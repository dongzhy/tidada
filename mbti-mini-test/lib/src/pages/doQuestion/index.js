"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const jsx_runtime_1 = require("react/jsx-runtime");
require("taro-ui/dist/style/components/button.scss"); // 按需引入
require("./index.scss");
// eslint-disable-next-line import/first
const components_1 = require("@tarojs/components");
const questions_json_1 = require("../data/questions.json");
// eslint-disable-next-line import/first
const taro_ui_1 = require("taro-ui");
const react_1 = require("react");
/**
 * 主页
 */
exports.default = () => {
    const question = questions_json_1.default[0];
    const questionOptions = question.options.map((option) => {
        return {
            label: `${option.key}.${option.value}`,
            value: 'option.key'
        };
    });
    //当前题目序号，从1开始
    const [current, setCurrent] = (0, react_1.useState)(1);
    return ((0, jsx_runtime_1.jsxs)("view", Object.assign({ className: "doQuestionPage" }, { children: [(0, jsx_runtime_1.jsxs)(components_1.View, Object.assign({ className: "at-article__h1" }, { children: [current, "\u3001", question.title] })), (0, jsx_runtime_1.jsx)(taro_ui_1.AtRadio, { options: questionOptions, value: undefined, onClick: function (vaule, event) {
                    throw new Error("Function not implemented.");
                } }), (0, jsx_runtime_1.jsx)(taro_ui_1.AtButton, Object.assign({ type: "primary", circle: true, className: "enterBtn" }, { children: "\u4E0B\u4E00\u9898" })), (0, jsx_runtime_1.jsx)(taro_ui_1.AtButton, Object.assign({ type: "primary", circle: true, className: "enterBtn" }, { children: "\u67E5\u770B\u7ED3\u679C" })), (0, jsx_runtime_1.jsx)(taro_ui_1.AtButton, Object.assign({ type: "primary", circle: true, className: "enterBtn" }, { children: "\u4E0A\u4E00\u9898" }))] })));
};
//# sourceMappingURL=index.js.map