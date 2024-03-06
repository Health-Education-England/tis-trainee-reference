/*
 * The MIT License (MIT)
 *
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.mapper.GradeMapper;
import uk.nhs.hee.tis.trainee.reference.model.Grade;
import uk.nhs.hee.tis.trainee.reference.repository.GradeRepository;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Foundation Year 1";
  private static final String DEFAULT_LABEL_2 = "Core Training Year 2";

  private GradeService service;

  @Mock
  private GradeRepository repository;

  private Grade grade1;
  private Grade grade2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new GradeService(repository, Mappers.getMapper(GradeMapper.class));

    grade1 = new Grade();
    grade1.setId(DEFAULT_ID_1);
    grade1.setTisId(DEFAULT_TIS_ID_1);
    grade1.setLabel(DEFAULT_LABEL_1);

    grade2 = new Grade();
    grade2.setId(DEFAULT_ID_2);
    grade2.setTisId(DEFAULT_TIS_ID_2);
    grade2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllGradesShouldReturnAllGrades() {
    List<Grade> grades = new ArrayList<>();
    grades.add(grade1);
    grades.add(grade2);
    when(repository.findAll(Sort.by("label"))).thenReturn(grades);
    List<Grade> allGrades = service.get();
    assertThat("Unexpected size of returned Grade list",
        allGrades.size(), equalTo(grades.size()));
    assertThat("The returned grade list doesn't contain the expected grade",
        allGrades, hasItem(grade1));
  }

  @Test
  void createGradeShouldCreateGradeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(Grade.class))).thenAnswer(returnsFirstArg());

    Grade grade = service.create(grade2);

    assertThat("Unexpected id.", grade.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", grade.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", grade.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void createGradeShouldUpdateGradeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(grade1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    Grade grade = service.create(grade2);

    assertThat("Unexpected id.", grade.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", grade.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", grade.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateGradeShouldCreateGradeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(Grade.class))).thenAnswer(returnsFirstArg());

    Grade grade = service.update(grade2);

    assertThat("Unexpected id.", grade.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", grade.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", grade.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateGradeShouldUpdateGradeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(grade1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    Grade grade = service.update(grade2);

    assertThat("Unexpected id.", grade.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", grade.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", grade.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldDeleteGradesByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
