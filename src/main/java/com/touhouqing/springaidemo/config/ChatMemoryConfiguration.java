package com.touhouqing.springaidemo.config;

import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class ChatMemoryConfiguration {

    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(5)
                .build();
    }

    @Bean
    public ChatMemory mysqlChatMemory(MysqlChatMemoryRepository mysqlChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(mysqlChatMemoryRepository)
                .maxMessages(5)
                .build();
    }

    @Bean
    public ChatMemory redisChatMemory(RedissonRedisChatMemoryRepository redissonRedisChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(redissonRedisChatMemoryRepository)
                .maxMessages(5)
                .build();
    }
}
