package com.touhouqing.springaidemo.service;

import org.springframework.web.multipart.MultipartFile;

public interface MilvusVectorService {
    /**
     * 上传文件
     * @param file
     */
    void add(MultipartFile file);
}
