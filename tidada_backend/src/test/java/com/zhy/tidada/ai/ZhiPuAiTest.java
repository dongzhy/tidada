package com.zhy.tidada.ai;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import com.zhy.tidada.config.AiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest
public class ZhiPuAiTest {
    @Resource
    private AiConfig aiConfig;
    @Test
    public void test(){
        ZhipuAiClient client = aiConfig.getClient();

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model("glm-5")
                .messages(Arrays.asList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER.value())
                                .content("你好，请介绍一下自己")
                                .build()
                ))
                .build();

        // 发送请求
        ChatCompletionResponse response = client.chat().createChatCompletion(request);

        // 获取回复
        if (response.isSuccess()) {
            Object reply = response.getData().getChoices().get(0).getMessage();
            System.out.println("AI 回复: " + reply);
        } else {
            System.err.println("错误: " + response.getMsg());
        }
    }
}
