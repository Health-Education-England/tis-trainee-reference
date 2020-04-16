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
import uk.nhs.hee.tis.trainee.reference.dto.CurriculumDto;
import uk.nhs.hee.tis.trainee.reference.mapper.CurriculumMapper;
import uk.nhs.hee.tis.trainee.reference.model.Curriculum;
import uk.nhs.hee.tis.trainee.reference.service.CurriculumService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CurriculumResource.class)
public class CurriculumResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "GP Returner";
  private static final String DEFAULT_LABEL_2 = "Paediatrics";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private CurriculumService curriculumServiceMock;

  @MockBean
  private CurriculumMapper curriculumMapperMock;

  private Curriculum curriculum1, curriculum2;
  private CurriculumDto curriculumDto1, curriculumDto2;

  @BeforeEach
  public void setup() {
    CurriculumResource curriculumResource = new CurriculumResource(curriculumServiceMock,
        curriculumMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(curriculumResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  @BeforeEach
  public void initData() {
    curriculum1 = new Curriculum();
    curriculum1.setId(DEFAULT_ID_1);
    curriculum1.setCurriculumTisId(DEFAULT_TIS_ID_1);
    curriculum1.setLabel(DEFAULT_LABEL_1);

    curriculum2 = new Curriculum();
    curriculum2.setId(DEFAULT_ID_2);
    curriculum2.setCurriculumTisId(DEFAULT_TIS_ID_2);
    curriculum2.setLabel(DEFAULT_LABEL_2);

    curriculumDto1 = new CurriculumDto();
    curriculumDto1.setId(DEFAULT_ID_1);
    curriculumDto1.setCurriculumTisId(DEFAULT_TIS_ID_1);
    curriculumDto1.setLabel(DEFAULT_LABEL_1);

    curriculumDto2 = new CurriculumDto();
    curriculumDto2.setId(DEFAULT_ID_2);
    curriculumDto2.setCurriculumTisId(DEFAULT_TIS_ID_2);
    curriculumDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllCurricula() throws Exception {
    List<Curriculum> curricula = new ArrayList<>();
    curricula.add(curriculum1);
    curricula.add(curriculum2);
    List<CurriculumDto> curriculumDtos = new ArrayList<>();
    curriculumDtos.add(curriculumDto1);
    curriculumDtos.add(curriculumDto2);
    when(curriculumServiceMock.getCurricula()).thenReturn(curricula);
    when(curriculumMapperMock.toDtos(curricula)).thenReturn(curriculumDtos);
    this.mockMvc.perform(get("/api/curriculum")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(curriculumDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
