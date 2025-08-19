package com.touhouqing.springaidemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class ChatRedisMemoryController {
    private final ChatClient redisChatClient;

    @Autowired
    @Qualifier("redisChatMemory")
    private MessageWindowChatMemory messageWindowChatMemory;

    @GetMapping(path = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String message,String id) {
        return redisChatClient.prompt()
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
