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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.mapper.CurriculumMapper;
import uk.nhs.hee.tis.trainee.reference.model.Curriculum;
import uk.nhs.hee.tis.trainee.reference.repository.CurriculumRepository;

@ExtendWith(MockitoExtension.class)
class CurriculumServiceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "GP Returner";
  private static final String DEFAULT_LABEL_2 = "Paediatrics";

  private CurriculumService service;

  @Mock
  private CurriculumRepository repository;

  private Curriculum curriculum1;
  private Curriculum curriculum2;

  /**
   * Set up data.
   */
  @BeforeEach
  void setUp() {
    service = new CurriculumService(repository, Mappers.getMapper(CurriculumMapper.class));

    curriculum1 = new Curriculum();
    curriculum1.setId(DEFAULT_ID_1);
    curriculum1.setTisId(DEFAULT_TIS_ID_1);
    curriculum1.setLabel(DEFAULT_LABEL_1);

    curriculum2 = new Curriculum();
    curriculum2.setId(DEFAULT_ID_2);
    curriculum2.setTisId(DEFAULT_TIS_ID_2);
    curriculum2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllCurriculumsShouldReturnAllCurriculums() {
    List<Curriculum> curriculums = new ArrayList<>();
    curriculums.add(curriculum1);
    curriculums.add(curriculum2);
    when(repository.findAll(Sort.by("label"))).thenReturn(curriculums);
    List<Curriculum> allCurriculums = service.get();
    assertThat("The size of returned curriculum list do not match the expected value",
        allCurriculums.size(), CoreMatchers.equalTo(curriculums.size()));
    assertThat("The returned curriculum list doesn't not contain the expected curriculum",
        allCurriculums, CoreMatchers.hasItem(curriculum1));
  }

  @Test
  void createCurriculumShouldCreateCurriculumWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(Curriculum.class))).thenAnswer(returnsFirstArg());

    Curriculum curriculum = service.create(curriculum2);

    assertThat("Unexpected id.", curriculum.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", curriculum.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", curriculum.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void createCurriculumShouldUpdateCurriculumWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(curriculum1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    Curriculum curriculum = service.create(curriculum2);

    assertThat("Unexpected id.", curriculum.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", curriculum.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", curriculum.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateCurriculumShouldCreateCurriculumWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(Curriculum.class))).thenAnswer(returnsFirstArg());

    Curriculum curriculum = service.update(curriculum2);

    assertThat("Unexpected id.", curriculum.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", curriculum.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", curriculum.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateCurriculumShouldUpdateCurriculumWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(curriculum1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    Curriculum curriculum = service.update(curriculum2);

    assertThat("Unexpected id.", curriculum.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", curriculum.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", curriculum.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldDeleteCurriculumsByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
