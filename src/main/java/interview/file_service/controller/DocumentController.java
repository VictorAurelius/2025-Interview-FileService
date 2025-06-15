package interview.file_service.controller;

import interview.file_service.dto.ApiResponse;
import interview.file_service.dto.DocumentDTO;
import interview.file_service.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Validated
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<DocumentDTO>> uploadDocument(
            @RequestParam("file") MultipartFile file) {
        DocumentDTO document = documentService.uploadDocument(file);
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", document));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentDTO>>> getAllDocuments() {
        List<DocumentDTO> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentDTO>> getDocument(@PathVariable Long id) {
        DocumentDTO document = documentService.getDocument(id);
        return ResponseEntity.ok(ApiResponse.success("Document retrieved successfully", document));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        var downloadResponse = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadResponse.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadResponse.getFilename() + "\"")
                .body(downloadResponse.getResource());
    }
}