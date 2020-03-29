package uk.nhs.hee.tis.trainee.reference.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.nhs.hee.tis.trainee.reference.dto.CollegeDto;
import uk.nhs.hee.tis.trainee.reference.model.College;

@Mapper(componentModel = "spring")
public interface CollegeMapper {

  CollegeDto toDto(College college);

  List<CollegeDto> toDtos(List<College> colleges);

  College toEntity(CollegeDto collegeDto);

  List<College> toEntities(List<CollegeDto> collegeDtos);
}
