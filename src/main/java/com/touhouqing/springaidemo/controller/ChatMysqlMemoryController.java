package com.touhouqing.springaidemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mysql")
public class ChatMysqlMemoryController {

    private final ChatClient mysqlChatClient;

    @GetMapping(path = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String message, String id) {
        return mysqlChatClient.prompt()
                .user(message)
                .advisors(a -> a.param(CONVERSATION_ID, id))
                .stream()
                .content();
    }
}
