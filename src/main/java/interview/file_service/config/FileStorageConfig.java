package interview.file_service.config;

import interview.file_service.repository.DocumentRepository;
import interview.file_service.service.DocumentService;
import interview.file_service.service.impl.DocumentServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Bean
    public DocumentService documentService(DocumentRepository documentRepository) {
        return new DocumentServiceImpl(documentRepository, uploadDir);
    }
}