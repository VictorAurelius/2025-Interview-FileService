package interview.file_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
public class DownloadResponse {
    private Resource resource;
    private String contentType;
    private String filename;
}