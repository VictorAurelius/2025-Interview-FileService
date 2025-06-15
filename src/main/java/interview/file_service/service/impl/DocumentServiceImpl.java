package interview.file_service.service.impl;

import interview.file_service.dto.DocumentDTO;
import interview.file_service.dto.DownloadResponse;
import interview.file_service.entity.Document;
import interview.file_service.exception.DocumentNotFoundException;
import interview.file_service.exception.FileStorageException;
import interview.file_service.mapper.DocumentMapper;
import interview.file_service.repository.DocumentRepository;
import interview.file_service.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path getFileStorageLocation() {
        Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored",
                    ex);
        }
        return fileStorageLocation;
    }

    @Override
    public DocumentDTO uploadDocument(MultipartFile file) {
        // Validate file
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        if (fileName.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + fileName);
        }

        String fileType = file.getContentType();
        if (fileType == null || (!fileType.equals("application/pdf")
                && !fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new FileStorageException("Invalid file type. Only PDF and DOCX files are allowed");
        }

        try {
            // Save file to storage
            Path targetLocation = getFileStorageLocation().resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save document info to database
            Document document = new Document();
            document.setFileName(fileName);
            document.setFileType(fileType);
            document.setUploadDate(LocalDateTime.now());
            document.setFilePath(targetLocation.toString());

            Document savedDocument = documentRepository.save(document);
            return DocumentMapper.INSTANCE.toDto(savedDocument);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName, ex);
        }
    }

    @Override
    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(DocumentMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentDTO getDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));
        return DocumentMapper.INSTANCE.toDto(document);
    }

    @Override
    public DownloadResponse downloadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));

        try {
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return new DownloadResponse(
                        resource,
                        document.getFileType(),
                        document.getFileName());
            } else {
                throw new DocumentNotFoundException("File not found: " + document.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new DocumentNotFoundException("File not found: " + document.getFileName(), ex);
        }
    }
}