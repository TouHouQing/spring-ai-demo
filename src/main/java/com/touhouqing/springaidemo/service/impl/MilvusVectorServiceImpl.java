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

    @Override
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

    private void saveFileVector(FileVector fileVector, List<Document> documents) {
        documents.forEach(document -> {
            document.getMetadata().put("VectorId", fileVector.getVectorId());
            document.getMetadata().put("OriginalFileName", fileVector.getOriginalFileName());
            document.getMetadata().put("CurrentFileName", fileVector.getCurrentFileName());
            document.getMetadata().put("FileSize", fileVector.getFileSize());
            document.getMetadata().put("FileType", fileVector.getFileType());
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
        fileVector.setVectorId(UUID.randomUUID().toString());
        fileVector.setCreateTime(LocalDateTime.now());
        fileVectorMapper.insert(fileVector);
        return fileVector;
    }

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
