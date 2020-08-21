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
import uk.nhs.hee.tis.trainee.reference.dto.QualificationDto;
import uk.nhs.hee.tis.trainee.reference.mapper.QualificationMapper;
import uk.nhs.hee.tis.trainee.reference.model.Qualification;
import uk.nhs.hee.tis.trainee.reference.service.QualificationService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = QualificationResource.class)
public class QualificationResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Academic Degree in Medicine";
  private static final String DEFAULT_LABEL_2 = "B Med and Surgery";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private QualificationService qualificationServiceMock;

  @MockBean
  private QualificationMapper qualificationMapperMock;

  private Qualification qualification1;
  private Qualification qualification2;
  private QualificationDto qualificationDto1;
  private QualificationDto qualificationDto2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  public void setup() {
    QualificationResource qualificationResource = new QualificationResource(
        qualificationServiceMock, qualificationMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(qualificationResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

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

    qualificationDto1 = new QualificationDto();
    qualificationDto1.setId(DEFAULT_ID_1);
    qualificationDto1.setTisId(DEFAULT_TIS_ID_1);
    qualificationDto1.setLabel(DEFAULT_LABEL_1);

    qualificationDto2 = new QualificationDto();
    qualificationDto2.setId(DEFAULT_ID_2);
    qualificationDto2.setTisId(DEFAULT_TIS_ID_2);
    qualificationDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllQualifications() throws Exception {
    List<Qualification> qualifications = new ArrayList<>();
    qualifications.add(qualification1);
    qualifications.add(qualification2);
    List<QualificationDto> qualificationDtos = new ArrayList<>();
    qualificationDtos.add(qualificationDto1);
    qualificationDtos.add(qualificationDto2);
    when(qualificationServiceMock.getQualification()).thenReturn(qualifications);
    when(qualificationMapperMock.toDtos(qualifications)).thenReturn(qualificationDtos);
    this.mockMvc.perform(get("/api/qualification")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(qualificationDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
