/*
 * The MIT License (MIT)
 * Copyright 2020 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.nhs.hee.tis.trainee.reference.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.nhs.hee.tis.trainee.reference.dto.CurriculumDto;
import uk.nhs.hee.tis.trainee.reference.model.Curriculum;

@Mapper(componentModel = "spring")
public interface CurriculumMapper {

  CurriculumDto toDto(Curriculum curriculum);

  List<CurriculumDto> toDtos(List<Curriculum> curricula);

  Curriculum toEntity(CurriculumDto curriculumDto);

  List<Curriculum> toEntities(List<CurriculumDto> curriculumDtos);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "tisId", ignore = true)
  @Mapping(target = "curriculumSubType")
  Curriculum update(@MappingTarget Curriculum target, Curriculum source);
}
