package com.touhouqing.springaidemo.controller;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
@RefreshScope
public class ChatRedisMemoryController {
    private final ChatClient redisChatClient;

    @Autowired
    @Qualifier("redisChatMemory")
    private MessageWindowChatMemory messageWindowChatMemory;

    @Value(value = "${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Value(value = "${spring.ai.dashscope.base-url}")
    private String baseUrl;

    @Value(value = "${spring.ai.dashscope.chat.options.model}")
    private String model;

    @Value(value = "${spring.ai.dashscope.chat.options.max-tokens}")
    private Integer maxToken;

    @Value(value = "${system.prompt}")
    public String prompt;

    @GetMapping(path = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String message,String id) {
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apiKey).baseUrl(baseUrl).build();
        DashScopeChatOptions dashScopeChatOptions = DashScopeChatOptions.builder().withModel(model).withMaxToken(maxToken).build();
        DashScopeChatModel chatModel = DashScopeChatModel.builder().dashScopeApi(dashScopeApi).defaultOptions(dashScopeChatOptions).build();
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        return chatClient.prompt()
                .system(prompt)
                .user(message)
                .advisors(a -> a.param(CONVERSATION_ID, id))
                .stream()
                .content();
    }

    @GetMapping("/messages")
    public List<Message> messages(String id) {
        return messageWindowChatMemory.get(id);
    }
}
