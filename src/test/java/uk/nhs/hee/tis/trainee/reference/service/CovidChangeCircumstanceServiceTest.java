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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.model.CovidChangeCircumstance;
import uk.nhs.hee.tis.trainee.reference.repository.CovidChangeCircumstanceRepository;

@ExtendWith(MockitoExtension.class)
class CovidChangeCircumstanceServiceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_LABEL_1 = "DEFAULT_LABEL_1";
  private static final String DEFAULT_LABEL_2 = "DEFAULT_LABEL_2";

  @InjectMocks
  private CovidChangeCircumstanceService covidChangeCircServiceImpl;

  @Mock
  private CovidChangeCircumstanceRepository covidChangeCircRepositoryMock;

  private CovidChangeCircumstance covidChangeCirc1;
  private CovidChangeCircumstance covidChangeCirc2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    covidChangeCirc1 = new CovidChangeCircumstance();
    covidChangeCirc1.setId(DEFAULT_ID_1);
    covidChangeCirc1.setLabel(DEFAULT_LABEL_1);

    covidChangeCirc2 = new CovidChangeCircumstance();
    covidChangeCirc2.setId(DEFAULT_ID_2);
    covidChangeCirc2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllCovidChangeCircShouldReturnAll() {
    List<CovidChangeCircumstance> covidChangeCircs = new ArrayList<>();
    covidChangeCircs.add(covidChangeCirc1);
    covidChangeCircs.add(covidChangeCirc2);
    when(covidChangeCircRepositoryMock.findAll(Sort.by("label"))).thenReturn(covidChangeCircs);

    List<CovidChangeCircumstance> allCovidChangeCircs = covidChangeCircServiceImpl
        .getCovidChangeCircumstances();

    assertThat("Unexpected size of returned CovidChangeCircumstance list",
        allCovidChangeCircs.size(), equalTo(covidChangeCircs.size()));
    assertThat("The returned CovidChangeCircumstance list doesn't contain the expected "
        + "covid change item", allCovidChangeCircs, hasItem(covidChangeCirc1));
  }
}
