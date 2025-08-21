package com.touhouqing.springaidemo.controller;

import com.touhouqing.springaidemo.common.Result;
import com.touhouqing.springaidemo.service.MilvusVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RequestMapping("/file")
@RestController
@RequiredArgsConstructor
public class MilvusController {

    private final MilvusVectorService milvusVectorService;

    /**
     * 上传文件
     * @param file
     */
    @PostMapping
    public Result upload(MultipartFile file) {
        milvusVectorService.add(file);
        return Result.success();
    }

    /**
     * 批量删除文件
     * @param ids
     */
    @DeleteMapping
    public Result delete(@RequestParam("ids") List<Integer> ids) {
        milvusVectorService.deleteBatch(ids);
        return Result.success();
    }

}
