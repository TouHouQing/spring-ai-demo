package com.touhouqing.springaidemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RequiredArgsConstructor
@RestController
@RefreshScope
public class ChatInMemoryController {

    private final ChatClient chatClient;

    @Value(value = "${system.prompt}")
    public String prompt;

    @GetMapping(path = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String message, String id) {
        return chatClient.prompt()
                .system(prompt)
                .user(message)
                .advisors(a -> a.param(CONVERSATION_ID, id))
                .stream()
                .content();
    }
}
