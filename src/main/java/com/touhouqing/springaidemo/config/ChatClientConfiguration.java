package com.touhouqing.springaidemo.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class ChatClientConfiguration {

    private final VectorStore vectorStore;

    @Bean
    public ChatClient chatClient(DashScopeChatModel dashScopeChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你只需要记住用户的名字，每次只需要回答用户的名字，并在结尾加上内存，如果你不知道用户名字是什么就说不知道")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @Bean
    public ChatClient mysqlChatClient(DashScopeChatModel dashScopeChatModel, ChatMemory mysqlChatMemory) {
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你只需要记住用户的名字，每次只需要回答用户的名字，并在结尾加上mysql，如果你不知道用户名字是什么就说不知道")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(mysqlChatMemory).build()
                )
                .build();
    }

    @Bean
    public ChatClient redisChatClient(DashScopeChatModel dashScopeChatModel, ChatMemory redisChatMemory) {
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你只需要记住用户的名字，每次只需要回答用户的名字，并在结尾加上redis，如果你不知道用户名字是什么就说不知道")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(redisChatMemory).build()
                )
                .build();
    }

    @Bean
    public ChatClient milvusChatClient(DashScopeChatModel dashScopeChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(dashScopeChatModel)
                .defaultSystem("你是一个友好且知识渊博的AI助手。基于提供的上下文信息来回答问题，如果上下文中没有相关信息，请明确告知用户。")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(10)
                                        .similarityThreshold(0.2)
                                        .build())
                                .build()
                )
                .build();
    }

}
