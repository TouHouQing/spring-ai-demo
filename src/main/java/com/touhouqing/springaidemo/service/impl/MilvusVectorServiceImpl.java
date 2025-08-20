package com.touhouqing.springaidemo.service.impl;

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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MilvusVectorServiceImpl implements MilvusVectorService {

    private final VectorStore vectorStore;


    @Override
    public void add(MultipartFile file) {
        //如果文件类型是pdf
        if(Objects.requireNonNull(file.getOriginalFilename()).endsWith(".pdf")){
            pdfToVectorStore(file.getResource());
        }
        else{
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(file.getResource());
            List<Document> documents = tikaDocumentReader.read();
            // 为每个Document设置file_name和Uid元数据
            documents.forEach(document -> {
                document.getMetadata().put("file_name", file.getOriginalFilename());
                document.getMetadata().put("id", UUID.randomUUID().toString());
            });
            vectorStore.add(documents);
        }
    }

    private void pdfToVectorStore(Resource resource) {
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
        String fileName = resource.getFilename();
        documents.forEach(document -> {
            document.getMetadata().put("file_name", fileName);
        });
        // 4.写入向量库
        vectorStore.add(documents);
    }
}
