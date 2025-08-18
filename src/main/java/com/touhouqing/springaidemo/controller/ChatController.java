package com.touhouqing.springaidemo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {


    private final ChatClient chatClient;


    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    @GetMapping("/chat")
    public String chat(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
