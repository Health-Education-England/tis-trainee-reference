/*
 * The MIT License (MIT)
 *
 * Copyright 2024 Crown Copyright (Health Education England)
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
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeContactDetailsDto;
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeContactDto;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;

/**
 * Mapper for local office contacts.
 */
@Mapper(componentModel = "spring")
public interface LocalOfficeContactMapper {

  LocalOfficeContactDto toDto(LocalOfficeContact localOfficeContact);

  List<LocalOfficeContactDto> toDtos(List<LocalOfficeContact> localOfficeContacts);

  LocalOfficeContactDetailsDto toDetailsDto(LocalOfficeContact localOfficeContact);

  List<LocalOfficeContactDetailsDto> toDetailsDtos(List<LocalOfficeContact> localOfficeContacts);

  @Mapping(target = "localOfficeName", ignore = true)
  @Mapping(target = "contactTypeName", ignore = true)
  @Mapping(target = "label", ignore = true)
  LocalOfficeContact toEntity(LocalOfficeContactDto localOfficeContactDto);

  List<LocalOfficeContact> toEntities(List<LocalOfficeContactDto> localOfficeContactDtos);

  @Mapping(target = "tisId", ignore = true)
  LocalOfficeContact update(@MappingTarget LocalOfficeContact target, LocalOfficeContact source);
}
