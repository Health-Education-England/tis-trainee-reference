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
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LocalOfficeResource.class)
public class LocalOfficeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Health Education England East of England";
  private static final String DEFAULT_LABEL_2 = "Northern Ireland Medical and Dental Training Agency";

  private static final String DEFAULT_ENTITY_ID_1 = "1";
  private static final String DEFAULT_ENTITY_ID_2 = "2";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private LocalOfficeService localOfficeServiceMock;

  @MockBean
  private LocalOfficeMapper localOfficeMapperMock;

  private LocalOffice localOffice1, localOffice2;
  private LocalOfficeDto localOfficeDto1, localOfficeDto2;

  @BeforeEach
  public void setup() {
    LocalOfficeResource localOfficeResource = new LocalOfficeResource(localOfficeServiceMock,
        localOfficeMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(localOfficeResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  @BeforeEach
  public void initData() {
    localOffice1 = new LocalOffice();
    localOffice1.setId(DEFAULT_ID_1);
    localOffice1.setLocalOfficeTisId(DEFAULT_TIS_ID_1);
    localOffice1.setLabel(DEFAULT_LABEL_1);
    localOffice1.setEntityId(DEFAULT_ENTITY_ID_1);

    localOffice2 = new LocalOffice();
    localOffice2.setId(DEFAULT_ID_2);
    localOffice2.setLocalOfficeTisId(DEFAULT_TIS_ID_2);
    localOffice2.setLabel(DEFAULT_LABEL_2);
    localOffice2.setEntityId(DEFAULT_ENTITY_ID_2);

    localOfficeDto1 = new LocalOfficeDto();
    localOfficeDto1.setId(DEFAULT_ID_1);
    localOfficeDto1.setLocalOfficeTisId(DEFAULT_TIS_ID_1);
    localOfficeDto1.setLabel(DEFAULT_LABEL_1);
    localOfficeDto1.setEntityId(DEFAULT_ENTITY_ID_1);

    localOfficeDto2 = new LocalOfficeDto();
    localOfficeDto2.setId(DEFAULT_ID_2);
    localOfficeDto2.setLocalOfficeTisId(DEFAULT_TIS_ID_2);
    localOfficeDto2.setLabel(DEFAULT_LABEL_2);
    localOfficeDto2.setEntityId(DEFAULT_ENTITY_ID_2);
  }

  @Test
  void testGetAllLocalOffices() throws Exception {
    List<LocalOffice> localOffices = new ArrayList<>();
    localOffices.add(localOffice1);
    localOffices.add(localOffice2);
    List<LocalOfficeDto> localOfficeDtos = new ArrayList<>();
    localOfficeDtos.add(localOfficeDto1);
    localOfficeDtos.add(localOfficeDto2);
    when(localOfficeServiceMock.getLocalOffice()).thenReturn(localOffices);
    when(localOfficeMapperMock.toDtos(localOffices)).thenReturn(localOfficeDtos);
    this.mockMvc.perform(get("/api/localoffice")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(localOfficeDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
