package uk.nhs.hee.tis.trainee.reference.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.nhs.hee.tis.trainee.reference.dto.QualificationDto;
import uk.nhs.hee.tis.trainee.reference.model.Qualification;

@Mapper(componentModel = "spring")
public interface QualificationMapper {

  QualificationDto toDto(Qualification qualification);

  List<QualificationDto> toDtos(List<Qualification> qualifications);

  Qualification toEntity(QualificationDto qualificationDto);

  List<Qualification> toEntities(List<QualificationDto> qualificationDtos);
}
