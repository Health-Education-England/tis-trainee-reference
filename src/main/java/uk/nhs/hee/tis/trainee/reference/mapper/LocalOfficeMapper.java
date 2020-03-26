package uk.nhs.hee.tis.trainee.reference.mapper;

import org.mapstruct.Mapper;
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeDto;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocalOfficeMapper {

  LocalOfficeDto toDto(LocalOffice localOffice);

  List<LocalOfficeDto> toDtos(List<LocalOffice> localOffices);

  LocalOffice toEntity(LocalOfficeDto localOfficeDto);

  List<LocalOffice> toEntities(List<LocalOfficeDto> localOfficeDtos);
}