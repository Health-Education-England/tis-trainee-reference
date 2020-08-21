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
import uk.nhs.hee.tis.trainee.reference.model.Qualification;
import uk.nhs.hee.tis.trainee.reference.repository.QualificationRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.QualificationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class QualificationServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Academic Degree in Medicine";
  private static final String DEFAULT_LABEL_2 = "B Med and Surgery";

  @InjectMocks
  private QualificationServiceImpl qualificationServiceImpl;

  @Mock
  private QualificationRepository qualificationRepositoryMock;

  private Qualification qualification1;
  private Qualification qualification2;

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    qualification1 = new Qualification();
    qualification1.setId(DEFAULT_ID_1);
    qualification1.setTisId(DEFAULT_TIS_ID_1);
    qualification1.setLabel(DEFAULT_LABEL_1);

    qualification2 = new Qualification();
    qualification2.setId(DEFAULT_ID_2);
    qualification2.setTisId(DEFAULT_TIS_ID_2);
    qualification2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  public void getAllQualificationsShouldReturnAllQualifications() {
    List<Qualification> qualifications = new ArrayList<>();
    qualifications.add(qualification1);
    qualifications.add(qualification2);
    when(qualificationRepositoryMock.findAll()).thenReturn(qualifications);
    List<Qualification> allQualifications = qualificationServiceImpl.getQualification();
    MatcherAssert
        .assertThat("The size of returned qualification list do not match the expected value",
            allQualifications.size(), CoreMatchers.equalTo(qualifications.size()));
    MatcherAssert.assertThat(
        "The returned qualification list doesn't not contain the expected qualification",
        allQualifications, CoreMatchers.hasItem(qualification1));
  }
}
