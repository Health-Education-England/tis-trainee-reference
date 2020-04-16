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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.trainee.reference.dto.GradeDto;
import uk.nhs.hee.tis.trainee.reference.model.Grade;
import uk.nhs.hee.tis.trainee.reference.repository.GradeRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.GradeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GradeServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_ABBREVIATION_1 = "F1";
  private static final String DEFAULT_ABBREVIATION_2 = "CT2";

  private static final String DEFAULT_LABEL_1 = "Foundation Year 1";
  private static final String DEFAULT_LABEL_2 = "Core Training Year 2";

  @InjectMocks
  private GradeServiceImpl gradeServiceImpl;

  @Mock
  private GradeRepository gradeRepositoryMock;

  private Grade grade1;
  private Grade grade2;
  private GradeDto gradeDto1;
  private GradeDto gradeDto2;

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    grade1 = new Grade();
    grade1.setId(DEFAULT_ID_1);
    grade1.setGradeTisId(DEFAULT_TIS_ID_1);
    grade1.setAbbreviation(DEFAULT_ABBREVIATION_1);
    grade1.setLabel(DEFAULT_LABEL_1);

    grade2 = new Grade();
    grade2.setId(DEFAULT_ID_2);
    grade2.setGradeTisId(DEFAULT_TIS_ID_2);
    grade2.setAbbreviation(DEFAULT_ABBREVIATION_2);
    grade2.setLabel(DEFAULT_LABEL_2);

    gradeDto1 = new GradeDto();
    gradeDto1.setId(DEFAULT_ID_1);
    gradeDto1.setGradeTisId(DEFAULT_TIS_ID_1);
    gradeDto1.setAbbreviation(DEFAULT_ABBREVIATION_1);
    gradeDto1.setLabel(DEFAULT_LABEL_1);

    gradeDto2 = new GradeDto();
    gradeDto2.setId(DEFAULT_ID_2);
    gradeDto2.setGradeTisId(DEFAULT_TIS_ID_2);
    gradeDto2.setAbbreviation(DEFAULT_ABBREVIATION_2);
    gradeDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  public void getAllGradesShouldReturnAllGrades() {
    List<Grade> grades = new ArrayList<>();
    grades.add(grade1);
    grades.add(grade2);
    when(gradeRepositoryMock.findAll()).thenReturn(grades);
    List<Grade> allGrades = gradeServiceImpl.getAllGrades();
    MatcherAssert.assertThat("The size of returned grade list do not match the expected value",
        allGrades.size(), CoreMatchers.equalTo(grades.size()));
    MatcherAssert.assertThat("The returned grade list doesn't not contain the expected grade",
        allGrades, CoreMatchers.hasItem(grade1));
  }
}
