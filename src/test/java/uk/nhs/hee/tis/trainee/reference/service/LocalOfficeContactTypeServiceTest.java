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

package uk.nhs.hee.tis.trainee.reference.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactTypeRepository;

@ExtendWith(MockitoExtension.class)
class LocalOfficeContactTypeServiceTest {

  private static final String DEFAULT_TIS_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_TIS_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_LABEL_1 = "Deferral";
  private static final String DEFAULT_LABEL_2 = "Onboarding Support";

  private static final String DEFAULT_CODE_1 = "DEFERRAL";
  private static final String DEFAULT_CODE_2 = "ONBOARDING_SUPPORT";


  private LocalOfficeContactTypeService service;

  @Mock
  private LocalOfficeContactTypeRepository repository;

  @Mock
  private LocalOfficeContactService contactService;

  private LocalOfficeContactType contactType1;
  private LocalOfficeContactType contactType2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new LocalOfficeContactTypeService(repository,
        Mappers.getMapper(LocalOfficeContactTypeMapper.class), contactService);

    contactType1 = new LocalOfficeContactType();
    contactType1.setTisId(DEFAULT_TIS_ID_1);
    contactType1.setLabel(DEFAULT_LABEL_1);
    contactType1.setCode(DEFAULT_CODE_1);

    contactType2 = new LocalOfficeContactType();
    contactType2.setTisId(DEFAULT_TIS_ID_2);
    contactType2.setLabel(DEFAULT_LABEL_2);
    contactType2.setCode(DEFAULT_CODE_2);
  }

  @Test
  void getAllLocalOfficeContactTypeShouldReturnAllLocalOfficeContactTypes() {
    List<LocalOfficeContactType> contactTypes = new ArrayList<>();
    contactTypes.add(contactType1);
    contactTypes.add(contactType2);
    when(repository.findAll(Sort.by("label"))).thenReturn(contactTypes);
    List<LocalOfficeContactType> allContactTypes = service.get();
    assertThat("Unexpected size of returned LocalOfficeContactTypes list",
        allContactTypes.size(), equalTo(contactTypes.size()));
    assertThat("The contact types list doesn't contain an expected contact type",
        allContactTypes, hasItem(contactType1));
  }

  @Test
  void createLocalOfficeContactTypesShouldCreateLocalOfficeContactTypeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(LocalOfficeContactType.class))).thenAnswer(returnsFirstArg());

    LocalOfficeContactType contactType = service.create(contactType2);

    assertThat("Unexpected TIS id.", contactType.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", contactType.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected code.", contactType.getCode(), is(DEFAULT_CODE_2));
  }

  @Test
  void createLocalOfficeContactTypeShouldUpdateLocalOfficeContactTypeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(contactType1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    LocalOfficeContactType contactType = service.create(contactType2);

    assertThat("Unexpected TIS id.", contactType.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", contactType.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected code.", contactType.getCode(), is(DEFAULT_CODE_2));
  }

  @Test
  void updateLocalOfficeContactTypeShouldCreateLocalOfficeContactTypeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(LocalOfficeContactType.class))).thenAnswer(returnsFirstArg());

    LocalOfficeContactType contactType = service.update(contactType2);

    assertThat("Unexpected TIS id.", contactType.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", contactType.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected code.", contactType.getCode(), is(DEFAULT_CODE_2));
  }

  @Test
  void updateLocalOfficeContactTypeShouldUpdateLocalOfficeContactTypeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(contactType1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    LocalOfficeContactType contactType = service.update(contactType2);

    assertThat("Unexpected TIS id.", contactType.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", contactType.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected code.", contactType.getCode(), is(DEFAULT_CODE_2));
  }

  @Test
  void shouldDeleteLocalOfficeContactTypeByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
