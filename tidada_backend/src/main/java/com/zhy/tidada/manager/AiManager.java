package com.zhy.tidada.manager;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import com.zhy.tidada.common.ErrorCode;
import com.zhy.tidada.config.AiConfig;
import com.zhy.tidada.exception.BusinessException;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class AiManager {
    @Resource
    private AiConfig aiConfig;

    private static final float STABLE_TEMPERATURE = 0.5f;
    private static final float UNSTABLE_TEMPERATURE = 0.99f;
    private static final String GLM_MODEL = "glm-5";

    // ==================== 同步请求 ====================
    public String doSyncUNStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }

    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, STABLE_TEMPERATURE);
    }

    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }

    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        if (systemMessage == null) systemMessage = "";
        if (userMessage == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        if (temperature == null) temperature = STABLE_TEMPERATURE;

        List<ChatMessage> chatMessageList = new ArrayList<>();
        chatMessageList.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage));
        chatMessageList.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));

        return doRequest(chatMessageList, stream, temperature);
    }

    public String doRequest(List<ChatMessage> chatMessages, Boolean stream, Float temperature) {
        if (chatMessages == null || chatMessages.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息列表不能为空");
        }
        if (stream == null) stream = Boolean.FALSE;
        if (temperature == null) temperature = STABLE_TEMPERATURE;

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(GLM_MODEL)
                .stream(stream)
                .temperature(temperature)
                .messages(chatMessages)
                .build();

        ChatCompletionResponse response;
        try {
            ZhipuAiClient client = aiConfig.getClient();
            if (client == null) throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 客户端初始化失败");
            response = client.chat().createChatCompletion(request);
        } catch (Exception e) {
            log.error("AI 同步请求失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 请求失败：" + e.getMessage());
        }

        if (response.isSuccess()) {
            try {
                if (response.getData() == null || response.getData().getChoices() == null || response.getData().getChoices().isEmpty()) {
                    return "";
                }
                ChatMessage replyMessage = response.getData().getChoices().get(0).getMessage();
                return replyMessage == null || replyMessage.getContent() == null ? "" : replyMessage.getContent().toString().trim();
            } catch (Exception e) {
                log.error("AI 同步响应解析失败", e);
                return "";
            }
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务返回错误：" + response.getMsg());
        }
    }

    // ==================== 流式请求（核心修复） ====================
    public Flowable<ModelData> doStreamRequest(String systemMessage, String userMessage, Float temperature) {
        if (systemMessage == null) systemMessage = "";
        if (userMessage == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户消息不能为空");


        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));
        return doStreamRequest(messages, temperature);
    }

    public Flowable<ModelData> doStreamRequest(List<ChatMessage> chatMessages, Float temperature) {
        if (chatMessages == null || chatMessages.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息列表不能为空");
        }
        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(GLM_MODEL)
                .stream(true)
                .temperature(temperature)
                .messages(chatMessages)
                .build();

        ChatCompletionResponse response;
        try {
            ZhipuAiClient client = aiConfig.getClient();
            if (client == null) throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 客户端初始化失败");
            response = client.chat().createChatCompletion(request);
        } catch (Exception e) {
            log.error("AI 流式请求失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 请求失败：" + e.getMessage());
        }

        if (response.isSuccess()) {
            Flowable<ModelData> flowable = response.getFlowable();
            if (flowable == null) {
                log.warn("流式响应为空，返回空数据流");
                return Flowable.empty();
            }
            // ✅ 核心修复：只过滤 null 的 ModelData，不再主动抛异常中断流
            return flowable.filter(Objects::nonNull);
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务返回错误：" + response.getMsg());
        }
    }

    // ==================== 安全订阅工具 ====================
    public Disposable safeStreamSubscribe(String systemMessage, String userMessage, Float temperature) {
        return doStreamRequest(systemMessage, userMessage, temperature)
                .subscribe(
                        data -> {
                            // ✅ 所有空值校验和异常处理都移到这里，由订阅端处理
                            if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                                Delta delta = data.getChoices().get(0).getDelta();
                                if (delta != null && delta.getContent() != null) {
                                    System.out.print(delta.getContent());
                                }
                            }
                        },
                        error -> {
                            log.error("流式订阅异常", error);
                            System.err.println("\n 流式错误: " + error.getMessage());
                        },
                        () -> {
                            log.info("流式订阅完成");
                            System.out.println("\n 流式完成");
                        }
                );
    }
}