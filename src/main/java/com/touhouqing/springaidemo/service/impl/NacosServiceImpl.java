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
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class NacosServiceImpl implements NacosService {

    private final NacosConfiguration nacosConfiguration;

    /**
     * 向nacos配置中心修改prompt
     * @param prompt
     */
    @Override
    public void postPrompt(String prompt) throws IOException, InterruptedException {
        String dataId = nacosConfiguration.getPromptDataId();
        String group = nacosConfiguration.getGroup();
        String baseUrl = nacosConfiguration.getBaseUrl();

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

    /**
     * 获取nacos的prompt配置
     * @return
     */
    @Override
    public String getPrompt() throws IOException, InterruptedException {
        String dataId = nacosConfiguration.getPromptDataId();
        String group = nacosConfiguration.getGroup();
        String baseUrl = nacosConfiguration.getBaseUrl();
        // 1. 编码参数
        String encodedDataId = URLEncoder.encode(dataId, StandardCharsets.UTF_8);
        String encodedGroup = URLEncoder.encode(group, StandardCharsets.UTF_8);

        // 2. 构建完整URL，包含查询参数
        String url = String.format(
                "%s/v1/cs/configs?dataId=%s&group=%s",
                baseUrl,
                encodedDataId,
                encodedGroup
        );

        // 3. 创建HTTP客户端和请求（使用GET方法）
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json, text/plain, */*")
                .GET() // 获取配置使用GET方法
                .build();

        // 4. 发送请求并获取响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 5. 验证响应结果
        if (response.statusCode() != 200) {
            throw new RuntimeException("获取Nacos配置失败: " + response.body() +
                    ", 状态码: " + response.statusCode());
        }

        // 6. 返回配置内容（如果只需要system.prompt的值，可以在这里进行解析）
        return response.body();
    }

    /**
     * 向nacos配置中心修改yaml
     *
     * @param apiKey
     * @param dashscopeBaseUrl
     * @param chatModel
     * @param embeddingModel
     * @param maxToken
     */
    @Override
    public void postYaml(String apiKey, String dashscopeBaseUrl, String chatModel, String embeddingModel, Integer maxToken) {
        // 2. 获取Nacos基础配置
        String dataId = nacosConfiguration.getYamlDataId();
        String group = nacosConfiguration.getGroup();
        String baseUrl = nacosConfiguration.getBaseUrl();
        try {
            // 3. 编码URL参数（仅dataId和group需要编码，content内部无需提前编码）
            String encodedDataId = URLEncoder.encode(dataId, StandardCharsets.UTF_8);
            String encodedGroup = URLEncoder.encode(group, StandardCharsets.UTF_8);

            // 4. 构建标准YAML格式的配置内容（注意缩进和格式正确性）
            // 这里不编码content内容，避免YAML格式被破坏，HttpClient会自动处理
            StringBuilder yamlContent = new StringBuilder();
            yamlContent.append("DASHSCOPE_API_KEY: ").append(apiKey).append("\n")
                    .append("DASHSCOPE_BASE_URL: ").append(dashscopeBaseUrl).append("\n")
                    .append("CHAT_MODEL: ").append(chatModel).append("\n")
                    .append("EMBEDDING_MODEL: ").append(embeddingModel).append("\n")
                    .append("MAX_TOKEN: ").append(maxToken);

            // 5. 构建请求体（包含所有参数）
            String requestBody = String.format(
                    "dataId=%s&group=%s&content=%s",
                    encodedDataId,
                    encodedGroup,
                    URLEncoder.encode(yamlContent.toString(), StandardCharsets.UTF_8)  // 对整个YAML内容编码
            );

            // 6. 构建完整URL
            String url = baseUrl + "/v1/cs/configs";

            // 7. 创建HTTP客户端（配置超时，避免无限等待）
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            // 8. 构建POST请求（指定正确的Content-Type）
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 9. 发送请求并处理响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 10. 验证响应结果
            if (response.statusCode() != 200 || !"true".equals(response.body().trim())) {
                throw new RuntimeException(String.format(
                        "Nacos配置更新失败，状态码: %d，响应: %s",
                        response.statusCode(),
                        response.body()
                ));
            }

        } catch (IllegalArgumentException e) {
            // 处理参数编码异常
            throw new RuntimeException("参数编码失败: " + e.getMessage(), e);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getYaml() throws IOException, InterruptedException {
        String dataId = nacosConfiguration.getYamlDataId();
        String group = nacosConfiguration.getGroup();
        String baseUrl = nacosConfiguration.getBaseUrl();
        String encodedDataId = URLEncoder.encode(dataId, StandardCharsets.UTF_8);
        String encodedGroup = URLEncoder.encode(group, StandardCharsets.UTF_8);
        String url = String.format(
                "%s/v1/cs/configs?dataId=%s&group=%s",
                baseUrl,
                encodedDataId,
                encodedGroup
        );
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json, text/plain, */*")
                .GET() // 获取配置使用GET方法
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("获取Nacos配置失败: " + response.body() +
                    ", 状态码: " + response.statusCode());
        }
        return response.body();
    }
}
