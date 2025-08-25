package com.touhouqing.springaidemo.service;

import java.io.IOException;
public interface NacosService {

    /**
     * 向nacos配置中心修改prompt
     * @param prompt
     */
    void postPrompt(String prompt) throws IOException, InterruptedException;

    /**
     * 获取nacos的prompt配置
     * @return
     */
    String getPrompt() throws IOException, InterruptedException;

    /**
     * 向nacos配置中心修改yaml
     *
     * @param apiKey
     * @param dashscopeBaseUrl
     * @param chatModel
     * @param embeddingModel
     * @param maxToken
     */
    void postYaml(String apiKey, String dashscopeBaseUrl, String chatModel, String embeddingModel, Integer maxToken);

    /**
     * 获取nacos的yaml配置
     * @return
     */
    String getYaml() throws IOException, InterruptedException;
}
