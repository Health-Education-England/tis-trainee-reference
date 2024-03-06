/*
 * The MIT License (MIT)
 *
 * Copyright 2024 Crown Copyright (Health Education England)
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
import java.util.UUID;
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
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeContactTypeService;

@ContextConfiguration(classes = {LocalOfficeContactTypeMapper.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(LocalOfficeContactTypeResource.class)
class LocalOfficeContactTypeResourceTest {

  private static final String DEFAULT_TIS_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_TIS_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_LABEL_1 = "Deferral";
  private static final String DEFAULT_LABEL_2 = "Onboarding Support";

  private static final String DEFAULT_CODE_1 = "DEFERRAL";
  private static final String DEFAULT_CODE_2 = "ONBOARDING_SUPPORT";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  @Autowired
  private ObjectMapper mapper;

  private MockMvc mockMvc;

  @MockBean
  private LocalOfficeContactTypeService localOfficeContactTypeServiceMock;

  private LocalOfficeContactType contactType1;
  private LocalOfficeContactType contactType2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  void setup() {
    LocalOfficeContactTypeMapper mapper = Mappers.getMapper(LocalOfficeContactTypeMapper.class);
    LocalOfficeContactTypeResource localOfficeResource
        = new LocalOfficeContactTypeResource(localOfficeContactTypeServiceMock, mapper);
    mockMvc = MockMvcBuilders.standaloneSetup(localOfficeResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    contactType1 = new LocalOfficeContactType();
    contactType1.setTisId(DEFAULT_TIS_ID_1);
    contactType1.setLabel(DEFAULT_LABEL_1);
    contactType1.setCode(DEFAULT_CODE_1);

    contactType2 = new LocalOfficeContactType();
    contactType2.setTisId(DEFAULT_TIS_ID_2);
    contactType2.setLabel(DEFAULT_LABEL_2);
    contactType2.setCode(DEFAULT_CODE_2);
  }

  @Test
  void testGetAllLocalOfficeContactTypes() throws Exception {
    List<LocalOfficeContactType> localOffices = new ArrayList<>();
    localOffices.add(contactType1);
    localOffices.add(contactType2);
    when(localOfficeContactTypeServiceMock.get()).thenReturn(localOffices);
    this.mockMvc.perform(get("/api/local-office-contact-type")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(2)))
        .andExpect(jsonPath("$.[*].tisId").value(hasItem(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.[*].tisId").value(hasItem(DEFAULT_TIS_ID_2)));
  }

  @Test
  void testCreateLocalOfficeContactType() throws Exception {
    when(localOfficeContactTypeServiceMock.create(contactType1)).thenReturn(contactType1);

    mockMvc.perform(post("/api/local-office-contact-type")
            .content(mapper.writeValueAsBytes(contactType1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.tisId").value(is(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.label").value(is(DEFAULT_LABEL_1)))
        .andExpect(jsonPath("$.code").value(is(DEFAULT_CODE_1)));
  }

  @Test
  void testUpdateLocalOfficeContactType() throws Exception {
    when(localOfficeContactTypeServiceMock.update(contactType1)).thenReturn(contactType1);

    mockMvc.perform(put("/api/local-office-contact-type")
            .content(mapper.writeValueAsBytes(contactType1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.tisId").value(is(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.label").value(is(DEFAULT_LABEL_1)))
        .andExpect(jsonPath("$.code").value(is(DEFAULT_CODE_1)));
  }

  @Test
  void testDeleteLocalOfficeContactType() throws Exception {
    mockMvc.perform(delete("/api/local-office-contact-type/{tisId}", DEFAULT_TIS_ID_1)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());

    verify(localOfficeContactTypeServiceMock).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
