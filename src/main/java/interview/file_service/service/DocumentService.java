package interview.file_service.service;

import interview.file_service.dto.DocumentDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentDTO uploadDocument(MultipartFile file);

    List<DocumentDTO> getAllDocuments();

    Resource downloadDocument(Long id);
}