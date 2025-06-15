package interview.file_service.service;

import interview.file_service.dto.DocumentDTO;
import interview.file_service.dto.DownloadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentDTO uploadDocument(MultipartFile file);

    List<DocumentDTO> getAllDocuments();

    DocumentDTO getDocument(Long id);

    DownloadResponse downloadDocument(Long id);
}