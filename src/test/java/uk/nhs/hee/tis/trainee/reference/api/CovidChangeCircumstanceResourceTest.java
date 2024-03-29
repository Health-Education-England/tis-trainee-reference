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

package uk.nhs.hee.tis.trainee.reference.api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.nhs.hee.tis.trainee.reference.dto.CovidChangeCircumstanceDto;
import uk.nhs.hee.tis.trainee.reference.mapper.CovidChangeCircumstanceMapper;
import uk.nhs.hee.tis.trainee.reference.model.CovidChangeCircumstance;
import uk.nhs.hee.tis.trainee.reference.service.CovidChangeCircumstanceService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CovidChangeCircumstanceResource.class)
class CovidChangeCircumstanceResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_LABEL_1 = "DEFAULT_LABEL_1";
  private static final String DEFAULT_LABEL_2 = "DEFAULT_LABEL_2";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private CovidChangeCircumstanceService covidChangeCircServiceMock;

  @MockBean
  private CovidChangeCircumstanceMapper covidChangeCircMapperMock;

  private CovidChangeCircumstance covidChangeCirc1;
  private CovidChangeCircumstance covidChangeCirc2;
  private CovidChangeCircumstanceDto covidChangeCircDto1;
  private CovidChangeCircumstanceDto covidChangeCircDto2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  public void setup() {
    CovidChangeCircumstanceResource collegeChangeCircResource = new CovidChangeCircumstanceResource(
        covidChangeCircServiceMock, covidChangeCircMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(collegeChangeCircResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    covidChangeCirc1 = new CovidChangeCircumstance();
    covidChangeCirc1.setId(DEFAULT_ID_1);
    covidChangeCirc1.setLabel(DEFAULT_LABEL_1);

    covidChangeCirc2 = new CovidChangeCircumstance();
    covidChangeCirc2.setId(DEFAULT_ID_2);
    covidChangeCirc2.setLabel(DEFAULT_LABEL_2);

    covidChangeCircDto1 = new CovidChangeCircumstanceDto();
    covidChangeCircDto1.setId(DEFAULT_ID_1);
    covidChangeCircDto1.setLabel(DEFAULT_LABEL_1);

    covidChangeCircDto2 = new CovidChangeCircumstanceDto();
    covidChangeCircDto2.setId(DEFAULT_ID_2);
    covidChangeCircDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllCovidChangeCircs() throws Exception {
    List<CovidChangeCircumstance> covidChangeCircs = new ArrayList<>();
    covidChangeCircs.add(covidChangeCirc1);
    covidChangeCircs.add(covidChangeCirc2);
    List<CovidChangeCircumstanceDto> covidChangeCircsDtos = new ArrayList<>();
    covidChangeCircsDtos.add(covidChangeCircDto1);
    covidChangeCircsDtos.add(covidChangeCircDto2);
    when(covidChangeCircServiceMock.getCovidChangeCircumstances()).thenReturn(covidChangeCircs);
    when(covidChangeCircMapperMock.toDtos(covidChangeCircs)).thenReturn(covidChangeCircsDtos);
    this.mockMvc.perform(get("/api/covid-change-circs")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(covidChangeCircsDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
