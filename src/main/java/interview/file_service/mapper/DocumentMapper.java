package interview.file_service.mapper;

import interview.file_service.dto.DocumentDTO;
import interview.file_service.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    @Mapping(target = "filePath", ignore = true)
    Document toEntity(DocumentDTO dto);

    DocumentDTO toDto(Document entity);
}