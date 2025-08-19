package com.touhouqing.springaidemo.config;

import com.alibaba.cloud.ai.autoconfigure.memory.MysqlChatMemoryProperties;
import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@RequiredArgsConstructor
public class MysqlChatMemoryConfiguration {

    private final MysqlChatMemoryProperties mysqlChatMemoryProperties;

    @Bean
    public MysqlChatMemoryRepository mysqlChatMemoryRepository(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(mysqlChatMemoryProperties.getJdbcUrl());
        dataSource.setUsername(mysqlChatMemoryProperties.getUsername());
        dataSource.setPassword(mysqlChatMemoryProperties.getPassword());
        dataSource.setDriverClassName(mysqlChatMemoryProperties.getDriverClassName());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return MysqlChatMemoryRepository.mysqlBuilder()
                .jdbcTemplate(jdbcTemplate)
                .build();
    }

}
