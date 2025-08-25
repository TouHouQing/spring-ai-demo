package com.touhouqing.springaidemo.controller;

import com.touhouqing.springaidemo.common.Result;
import com.touhouqing.springaidemo.service.NacosService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 *
 * @author TouHouQing
 * @since 2025-08-20
 */
@RestController
@RequestMapping("/nacos")
@RequiredArgsConstructor
public class NacosController {

    private final NacosService nacosService;

    //修改nacos的prompt配置
    @PostMapping("/prompt")
    public Result prompt(String prompt) throws IOException, InterruptedException {
        nacosService.postPrompt(prompt);
        //发送请求
        return Result.success();
    }

    //获取nacos的prompt配置
    @GetMapping("/prompt")
    public Result<String> getPrompt() throws IOException, InterruptedException {
        return Result.success(nacosService.getPrompt());
    }

}
