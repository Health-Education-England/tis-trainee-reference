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
import uk.nhs.hee.tis.trainee.reference.dto.CollegeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.CollegeMapper;
import uk.nhs.hee.tis.trainee.reference.model.College;
import uk.nhs.hee.tis.trainee.reference.service.CollegeService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CollegeResource.class)
public class CollegeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Faculty Of Dental Surgery";
  private static final String DEFAULT_LABEL_2 = "Faculty of Intensive Care Medicine";


  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private CollegeService collegeServiceMock;

  @MockBean
  private CollegeMapper collageMapperMock;

  private College college1, college2;
  private CollegeDto collegeDto1, collegeDto2;

  @BeforeEach
  public void setup() {
    CollegeResource collegeResource = new CollegeResource(collegeServiceMock, collageMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(collegeResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  @BeforeEach
  public void initData() {
    college1 = new College();
    college1.setId(DEFAULT_ID_1);
    college1.setCollegeTisId(DEFAULT_TIS_ID_1);
    college1.setLabel(DEFAULT_LABEL_1);

    college2 = new College();
    college2.setId(DEFAULT_ID_2);
    college2.setCollegeTisId(DEFAULT_TIS_ID_2);
    college2.setLabel(DEFAULT_LABEL_2);

    collegeDto1 = new CollegeDto();
    collegeDto1.setId(DEFAULT_ID_1);
    collegeDto1.setCollegeTisId(DEFAULT_TIS_ID_1);
    collegeDto1.setLabel(DEFAULT_LABEL_1);

    collegeDto2 = new CollegeDto();
    collegeDto2.setId(DEFAULT_ID_2);
    collegeDto2.setCollegeTisId(DEFAULT_TIS_ID_2);
    collegeDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllColleges() throws Exception {
    List<College> colleges = new ArrayList<>();
    colleges.add(college1);
    colleges.add(college2);
    List<CollegeDto> collegeDtos = new ArrayList<>();
    collegeDtos.add(collegeDto1);
    collegeDtos.add(collegeDto2);
    when(collegeServiceMock.getCollege()).thenReturn(colleges);
    when(collageMapperMock.toDtos(colleges)).thenReturn(collegeDtos);
    this.mockMvc.perform(get("/api/college")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(collegeDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
