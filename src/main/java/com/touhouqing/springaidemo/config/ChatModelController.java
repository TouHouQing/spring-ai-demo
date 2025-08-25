package com.touhouqing.springaidemo.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Data
@RefreshScope
public class ChatModelController {

    @Value(value = "${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Value(value = "${spring.ai.dashscope.base-url}")
    private String baseUrl;

    @Value(value = "${spring.ai.dashscope.chat.options.model}")
    private String model;

    @Value(value = "${spring.ai.dashscope.chat.options.max-tokens}")
    private Integer maxToken;

    @Bean
    public DashScopeApi dashScopeApi(){
        return DashScopeApi.builder().apiKey(apiKey).baseUrl(baseUrl).build();
    }

    @Bean
    public DashScopeChatOptions chatOptions(){
        return DashScopeChatOptions.builder().withModel(model).withMaxToken(maxToken).build();
    }

    @Bean
    public DashScopeChatModel chatModel(){
        return DashScopeChatModel.builder().dashScopeApi(dashScopeApi()).defaultOptions(chatOptions()).build();
    }

}
