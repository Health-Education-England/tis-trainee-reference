package uk.nhs.hee.tis.trainee.reference.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.nhs.hee.tis.trainee.reference.dto.GenderDto;
import uk.nhs.hee.tis.trainee.reference.model.Gender;

@Mapper(componentModel = "spring")
public interface GenderMapper {

  GenderDto toDto(Gender gender);

  List<GenderDto> toDtos(List<Gender> genders);

  Gender toEntity(GenderDto genderDto);

  List<Gender> toEntities(List<GenderDto> genderDtos);
}
