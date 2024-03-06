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
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeService;

@ContextConfiguration(classes = {LocalOfficeMapper.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(LocalOfficeResource.class)
class LocalOfficeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Health Education England East of England";
  private static final String DEFAULT_LABEL_2 =
      "Northern Ireland Medical and Dental Training Agency";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  @Autowired
  private ObjectMapper mapper;

  private MockMvc mockMvc;

  @MockBean
  private LocalOfficeService localOfficeServiceMock;

  private LocalOffice localOffice1;
  private LocalOffice localOffice2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  void setup() {
    LocalOfficeMapper mapper = Mappers.getMapper(LocalOfficeMapper.class);
    LocalOfficeResource localOfficeResource = new LocalOfficeResource(localOfficeServiceMock,
        mapper);
    mockMvc = MockMvcBuilders.standaloneSetup(localOfficeResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    localOffice1 = new LocalOffice();
    localOffice1.setId(DEFAULT_ID_1);
    localOffice1.setTisId(DEFAULT_TIS_ID_1);
    localOffice1.setLabel(DEFAULT_LABEL_1);

    localOffice2 = new LocalOffice();
    localOffice2.setId(DEFAULT_ID_2);
    localOffice2.setTisId(DEFAULT_TIS_ID_2);
    localOffice2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllLocalOffices() throws Exception {
    List<LocalOffice> localOffices = new ArrayList<>();
    localOffices.add(localOffice1);
    localOffices.add(localOffice2);
    when(localOfficeServiceMock.get()).thenReturn(localOffices);
    this.mockMvc.perform(get("/api/local-office")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(2)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }

  @Test
  void testCreateLocalOffice() throws Exception {
    when(localOfficeServiceMock.create(localOffice1)).thenReturn(localOffice1);

    mockMvc.perform(post("/api/local-office")
            .content(mapper.writeValueAsBytes(localOffice1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(is(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.tisId").value(is(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.label").value(is(DEFAULT_LABEL_1)));
  }

  @Test
  void testUpdateLocalOffice() throws Exception {
    when(localOfficeServiceMock.update(localOffice1)).thenReturn(localOffice1);

    mockMvc.perform(put("/api/local-office")
            .content(mapper.writeValueAsBytes(localOffice1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(is(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.tisId").value(is(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.label").value(is(DEFAULT_LABEL_1)));
  }

  @Test
  void testDeleteLocalOffice() throws Exception {
    mockMvc.perform(delete("/api/local-office/{tisId}", DEFAULT_TIS_ID_1)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());

    verify(localOfficeServiceMock).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
