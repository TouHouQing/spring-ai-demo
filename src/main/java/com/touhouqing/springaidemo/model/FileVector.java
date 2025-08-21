package com.touhouqing.springaidemo.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author TouHouQing
 * @since 2025-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file_vector")
public class FileVector implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原始文件名
     */
    @TableField("original_file_name")
    private String originalFileName;

    /**
     * 修改后的文件名
     */
    @TableField("current_file_name")
    private String currentFileName;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件大小Byte
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 向量存储id
     */
    @TableField("vector_id")
    private String vectorId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 删除时间
     */
    @TableField("delete_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;

    public FileVector(){
        this.createTime = LocalDateTime.now();
    }
}
