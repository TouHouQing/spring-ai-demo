package com.touhouqing.springaidemo.controller;

import com.touhouqing.springaidemo.service.MilvusVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/milvus")
@RestController
@RequiredArgsConstructor
public class ChatMilvusController {

    private final MilvusVectorService milvusVectorService;

    @PutMapping("/upload")
    public void upload(MultipartFile file) {
        milvusVectorService.add(file);
    }

}
