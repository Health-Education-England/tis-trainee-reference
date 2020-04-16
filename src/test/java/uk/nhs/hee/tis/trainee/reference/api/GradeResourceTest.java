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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import uk.nhs.hee.tis.trainee.reference.dto.GradeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.GradeMapper;
import uk.nhs.hee.tis.trainee.reference.model.Grade;
import uk.nhs.hee.tis.trainee.reference.service.GradeService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GradeResource.class)
public class GradeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_ABBREVIATION_1 = "F1";
  private static final String DEFAULT_ABBREVIATION_2 = "CT2";

  private static final String DEFAULT_LABEL_1 = "Foundation Year 1";
  private static final String DEFAULT_LABEL_2 = "Core Training Year 2";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private GradeService gradeServiceMock;

  @MockBean
  private GradeMapper gradeMapperMock;

  private Grade grade1;
  private Grade grade2;
  private GradeDto gradeDto1;
  private GradeDto gradeDto2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  public void setup() {
    GradeResource gradeResource = new GradeResource(gradeServiceMock, gradeMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(gradeResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

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
  void testGetAllGrades() throws Exception {
    List<Grade> grades = new ArrayList<>();
    grades.add(grade1);
    grades.add(grade2);
    List<GradeDto> gradeDtos = new ArrayList<>();
    gradeDtos.add(gradeDto1);
    gradeDtos.add(gradeDto2);
    when(gradeServiceMock.getAllGrades()).thenReturn(grades);
    when(gradeMapperMock.toDtos(grades)).thenReturn(gradeDtos);
    this.mockMvc.perform(get("/api/grade")
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(gradeDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
