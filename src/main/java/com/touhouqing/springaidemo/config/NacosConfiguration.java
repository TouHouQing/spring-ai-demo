package com.touhouqing.springaidemo.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class NacosConfiguration {

    private String ip = "127.0.0.1:8848";

    private String dataId = "nacos-config-example.properties";

    private String group = "DEFAULT_GROUP";

    private String baseUrl = "http://" + ip + "/nacos";

}
