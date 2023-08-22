/*
 * The MIT License (MIT)
 *
 * Copyright 2023 Crown Copyright (Health Education England)
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.mapper.ProgrammeMembershipTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.ProgrammeMembershipType;
import uk.nhs.hee.tis.trainee.reference.repository.ProgrammeMembershipTypeRepository;

@ExtendWith(MockitoExtension.class)
class ProgrammeMembershipTypeServiceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Substantive";
  private static final String DEFAULT_LABEL_2 = "Military";

  private ProgrammeMembershipTypeService service;

  @Mock
  private ProgrammeMembershipTypeRepository repository;

  private ProgrammeMembershipType programmeMembershipType1;
  private ProgrammeMembershipType programmeMembershipType2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new ProgrammeMembershipTypeService(repository, Mappers.getMapper(
        ProgrammeMembershipTypeMapper.class));

    programmeMembershipType1 = new ProgrammeMembershipType();
    programmeMembershipType1.setId(DEFAULT_ID_1);
    programmeMembershipType1.setTisId(DEFAULT_TIS_ID_1);
    programmeMembershipType1.setLabel(DEFAULT_LABEL_1);

    programmeMembershipType2 = new ProgrammeMembershipType();
    programmeMembershipType2.setId(DEFAULT_ID_2);
    programmeMembershipType2.setTisId(DEFAULT_TIS_ID_2);
    programmeMembershipType2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllProgrammeMembershipTypeShouldReturnAllProgrammeMembershipTypes() {
    List<ProgrammeMembershipType> programmeMembershipTypes = new ArrayList<>();
    programmeMembershipTypes.add(programmeMembershipType1);
    programmeMembershipTypes.add(programmeMembershipType2);
    when(repository.findAll(Sort.by("label"))).thenReturn(programmeMembershipTypes);
    List<ProgrammeMembershipType> allProgrammeMembershipTypes = service.get();
    MatcherAssert
        .assertThat("Unexpected size of returned ProgrammeMembershipType list",
            allProgrammeMembershipTypes.size(),
            CoreMatchers.equalTo(programmeMembershipTypes.size()));
    MatcherAssert
        .assertThat("The returned ProgrammeMembershipType list doesn't contain the expected "
                + "ProgrammeMembershipType",
            allProgrammeMembershipTypes, CoreMatchers.hasItem(programmeMembershipType1));
  }

  @Test
  void createProgrammeMembershipTypeShouldCreateProgrammeMembershipTypeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(ProgrammeMembershipType.class))).thenAnswer(returnsFirstArg());

    ProgrammeMembershipType programmeMembershipType = service.create(programmeMembershipType2);

    assertThat("Unexpected id.", programmeMembershipType.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", programmeMembershipType.getTisId(),
        is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", programmeMembershipType.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void createProgrammeMembershipTypeShouldUpdateProgrammeMembershipTypeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(programmeMembershipType1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    ProgrammeMembershipType programmeMembershipType = service.create(programmeMembershipType2);

    assertThat("Unexpected id.", programmeMembershipType.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", programmeMembershipType.getTisId(),
        is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", programmeMembershipType.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateProgrammeMembershipTypeShouldCreateProgrammeMembershipTypeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(ProgrammeMembershipType.class))).thenAnswer(returnsFirstArg());

    ProgrammeMembershipType programmeMembershipType = service.update(programmeMembershipType2);

    assertThat("Unexpected id.", programmeMembershipType.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", programmeMembershipType.getTisId(),
        is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", programmeMembershipType.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateProgrammeMembershipTypeShouldUpdateProgrammeMembershipTypeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(programmeMembershipType1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    ProgrammeMembershipType programmeMembershipType = service.update(programmeMembershipType2);

    assertThat("Unexpected id.", programmeMembershipType.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", programmeMembershipType.getTisId(),
        is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", programmeMembershipType.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldDeleteProgrammeMembershipTypeByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
