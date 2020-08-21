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
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.nhs.hee.tis.trainee.reference.mapper.GradeMapper;
import uk.nhs.hee.tis.trainee.reference.model.Grade;
import uk.nhs.hee.tis.trainee.reference.service.GradeService;

@ContextConfiguration(classes = {GradeMapper.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(GradeResource.class)
public class GradeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Foundation Year 1";
  private static final String DEFAULT_LABEL_2 = "Core Training Year 2";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  @Autowired
  private ObjectMapper mapper;

  private MockMvc mockMvc;

  @MockBean
  private GradeService gradeServiceMock;

  private Grade grade1;
  private Grade grade2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  public void setup() {
    GradeMapper mapper = Mappers.getMapper(GradeMapper.class);
    GradeResource gradeResource = new GradeResource(gradeServiceMock, mapper);
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
    grade1.setTisId(DEFAULT_TIS_ID_1);
    grade1.setLabel(DEFAULT_LABEL_1);

    grade2 = new Grade();
    grade2.setId(DEFAULT_ID_2);
    grade2.setTisId(DEFAULT_TIS_ID_2);
    grade2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllGrades() throws Exception {
    List<Grade> grades = new ArrayList<>();
    grades.add(grade1);
    grades.add(grade2);
    when(gradeServiceMock.getAllGrades()).thenReturn(grades);
    mockMvc.perform(get("/api/grade")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(2)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }

  @Test
  void testCreateGrade() throws Exception {
    when(gradeServiceMock.createGrade(grade1)).thenReturn(grade1);

    mockMvc.perform(post("/api/grade")
        .content(mapper.writeValueAsBytes(grade1))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(is(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.tisId").value(is(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.label").value(is(DEFAULT_LABEL_1)));
  }

  @Test
  void testUpdateGrade() throws Exception {
    when(gradeServiceMock.updateGrade(grade1)).thenReturn(grade1);

    mockMvc.perform(put("/api/grade")
        .content(mapper.writeValueAsBytes(grade1))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(is(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.tisId").value(is(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.label").value(is(DEFAULT_LABEL_1)));
  }

  @Test
  void testDeleteGrade() throws Exception {
    mockMvc.perform(delete("/api/grade/{tisId}", DEFAULT_TIS_ID_1)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());

    verify(gradeServiceMock).deleteGrade(DEFAULT_TIS_ID_1);
  }
}
