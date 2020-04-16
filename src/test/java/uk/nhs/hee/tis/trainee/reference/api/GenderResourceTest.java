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
import uk.nhs.hee.tis.trainee.reference.dto.GenderDto;
import uk.nhs.hee.tis.trainee.reference.mapper.GenderMapper;
import uk.nhs.hee.tis.trainee.reference.model.Gender;
import uk.nhs.hee.tis.trainee.reference.service.GenderService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GenderResource.class)
public class GenderResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Male";
  private static final String DEFAULT_LABEL_2 = "Female";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private GenderService genderServiceMock;

  @MockBean
  private GenderMapper genderMapperMock;

  private Gender gender1;
  private Gender gender2;
  private GenderDto genderDto1;
  private GenderDto genderDto2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  public void setup() {
    GenderResource genderResource = new GenderResource(genderServiceMock, genderMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(genderResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    gender1 = new Gender();
    gender1.setId(DEFAULT_ID_1);
    gender1.setGenderTisId(DEFAULT_TIS_ID_1);
    gender1.setLabel(DEFAULT_LABEL_1);

    gender2 = new Gender();
    gender2.setId(DEFAULT_ID_2);
    gender2.setGenderTisId(DEFAULT_TIS_ID_2);
    gender2.setLabel(DEFAULT_LABEL_2);

    genderDto1 = new GenderDto();
    genderDto1.setId(DEFAULT_ID_1);
    genderDto1.setGenderTisId(DEFAULT_TIS_ID_1);
    genderDto1.setLabel(DEFAULT_LABEL_1);

    genderDto2 = new GenderDto();
    genderDto2.setId(DEFAULT_ID_2);
    genderDto2.setGenderTisId(DEFAULT_TIS_ID_2);
    genderDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllGenders() throws Exception {
    List<Gender> genders = new ArrayList<>();
    genders.add(gender1);
    genders.add(gender2);
    List<GenderDto> genderDtos = new ArrayList<>();
    genderDtos.add(genderDto1);
    genderDtos.add(genderDto2);
    when(genderServiceMock.getGender()).thenReturn(genders);
    when(genderMapperMock.toDtos(genders)).thenReturn(genderDtos);
    this.mockMvc.perform(get("/api/gender")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(genderDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
