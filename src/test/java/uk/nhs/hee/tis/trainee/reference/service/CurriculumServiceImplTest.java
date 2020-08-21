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
import uk.nhs.hee.tis.trainee.reference.model.Curriculum;
import uk.nhs.hee.tis.trainee.reference.repository.CurriculumRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.CurriculumServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CurriculumServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "GP Returner";
  private static final String DEFAULT_LABEL_2 = "Paediatrics";

  @InjectMocks
  private CurriculumServiceImpl curriculumServiceImpl;

  @Mock
  private CurriculumRepository curriculumRepositoryMock;

  private Curriculum curriculum1;
  private Curriculum curriculum2;

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
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
  public void getAllCurriculaShouldReturnAllCurricula() {
    List<Curriculum> curricula = new ArrayList<>();
    curricula.add(curriculum1);
    curricula.add(curriculum2);
    when(curriculumRepositoryMock.findAll()).thenReturn(curricula);
    List<Curriculum> allCurricula = curriculumServiceImpl.getCurricula();
    MatcherAssert.assertThat("The size of returned curriculum list do not match the expected value",
        allCurricula.size(), CoreMatchers.equalTo(curricula.size()));
    MatcherAssert
        .assertThat("The returned local office list doesn't not contain the expected local office",
            allCurricula, CoreMatchers.hasItem(curriculum1));
  }
}
