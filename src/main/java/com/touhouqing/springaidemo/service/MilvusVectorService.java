package com.touhouqing.springaidemo.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MilvusVectorService {
    /**
     * 上传文件
     * @param file
     */
    void add(MultipartFile file);

    /**
     * 批量删除文件
     * @param ids
     */
    void deleteBatch(List<Integer> ids);
}
