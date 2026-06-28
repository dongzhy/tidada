package com.zhy.tidada.config;


import ai.z.openapi.ZhipuAiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {
    /**
     * api key，需要从开发平台获取
     */
    private String apiKey;

    @Bean
    public ZhipuAiClient getClient() {
        return ZhipuAiClient.builder().ofZHIPU()
                .apiKey(apiKey)
                .build();
    }
}
