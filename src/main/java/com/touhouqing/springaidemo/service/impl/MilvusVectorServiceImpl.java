package com.touhouqing.springaidemo.service.impl;

import com.touhouqing.springaidemo.mapper.FileVectorMapper;
import com.touhouqing.springaidemo.model.FileVector;
import com.touhouqing.springaidemo.service.MilvusVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MilvusVectorServiceImpl implements MilvusVectorService {

    private final VectorStore vectorStore;

    private final FileVectorMapper fileVectorMapper;

    /**
     * 上传文件
     * @param file
     */
    @Override
    @Transactional
    public void add(MultipartFile file) {
        //如果文件类型是pdf
        if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".pdf")) {
            pdfToVectorStore(file);
        } else {
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(file.getResource());
            List<Document> documents = tikaDocumentReader.read();
            FileVector fileVector = fileToFileVector(file);
            saveFileVector(fileVector, documents);
        }
    }

    /**
     * 批量删除文件
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Integer> ids) {
        fileVectorMapper.deleteByIds(ids);
        for (Integer id : ids) {
            String filterExpr = "id == " + id.toString();
            vectorStore.delete(filterExpr);
        }
    }

    /**
     * 保存文件向量
     * @param fileVector
     * @param documents
     */
    private void saveFileVector(FileVector fileVector, List<Document> documents) {
        documents.forEach(document -> {
            document.getMetadata().put("id", fileVector.getId());
            document.getMetadata().put("original_file_name", fileVector.getOriginalFileName());
            document.getMetadata().put("current_file_name", fileVector.getCurrentFileName());
            document.getMetadata().put("file_size", fileVector.getFileSize());
            document.getMetadata().put("file_type", fileVector.getFileType());
        });
        vectorStore.add(documents);
    }

    //文件处理
    private FileVector fileToFileVector(MultipartFile file) {
        FileVector fileVector = new FileVector();
        fileVector.setFileType(file.getContentType());
        fileVector.setFileSize(file.getSize());
        fileVector.setOriginalFileName(file.getOriginalFilename()+"."+file.getContentType());
        fileVector.setCurrentFileName(UUID.randomUUID()+file.getContentType());
        fileVector.setCreateTime(LocalDateTime.now());
        fileVectorMapper.insert(fileVector);
        return fileVector;
    }

    /**
     * pdf文件处理
     * @param file
     */
    private void pdfToVectorStore(MultipartFile file) {
        Resource resource = file.getResource();
        // 1.创建PDF的读取器
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                resource, // 文件源
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1) // 每1页PDF作为一个Document
                        .build()
        );
        // 2.读取PDF文档，拆分为Document
        List<Document> documents = reader.read();
        // 3.为每个Document设置file_name元数据
        FileVector fileVector = fileToFileVector(file);
        saveFileVector(fileVector, documents);
    }
}
