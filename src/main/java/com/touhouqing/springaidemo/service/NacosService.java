package com.touhouqing.springaidemo.service;

import java.io.IOException;
public interface NacosService {

    /**
     * 向nacos配置中心修改prompt
     * @param prompt
     */
    void postPrompt(String prompt) throws IOException, InterruptedException;

}
