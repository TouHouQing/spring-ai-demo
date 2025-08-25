package com.touhouqing.springaidemo.service.impl;

import com.touhouqing.springaidemo.config.NacosConfiguration;
import com.touhouqing.springaidemo.service.NacosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NacosServiceImpl implements NacosService {

    private final NacosConfiguration nacosConfiguration;

    private final String dataId = nacosConfiguration.getDataId();

    private final String group = nacosConfiguration.getGroup();

    private final String baseUrl = nacosConfiguration.getBaseUrl();

    @Override
    public void postPrompt(String prompt) throws IOException, InterruptedException {
        // 1. 编码参数，防止特殊字符导致的URL错误
        String encodedDataId = URLEncoder.encode(dataId, StandardCharsets.UTF_8);
        String encodedGroup = URLEncoder.encode(group, StandardCharsets.UTF_8);
        String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8);

        // 2. 构建完整URL（与curl命令保持一致的路径）
        String url = baseUrl + "/v1/cs/configs";

        // 3. 构建请求体，包含所有参数（与curl的--data参数对应）
        String requestBody = String.format(
                "dataId=%s&group=%s&content=system.prompt=%s",
                encodedDataId,
                encodedGroup,
                encodedPrompt
        );

        // 4. 创建HTTP客户端和请求
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // 使用POST方法
                .build();

        // 5. 发送请求并获取响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 6. 验证响应结果（可选，根据需要添加）
        if (response.statusCode() != 200 || !"true".equals(response.body())) {
            throw new RuntimeException("修改Nacos配置失败: " + response.body() +
                    ", 状态码: " + response.statusCode());
        }
    }
}
