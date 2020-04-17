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
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.nhs.hee.tis.trainee.reference.dto.ImmigrationStatusDto;
import uk.nhs.hee.tis.trainee.reference.mapper.ImmigrationStatusMapper;
import uk.nhs.hee.tis.trainee.reference.model.ImmigrationStatus;
import uk.nhs.hee.tis.trainee.reference.service.ImmigrationStatusService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ImmigrationStatusResource.class)
public class ImmigrationStatusResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_LABEL_1 = "EEA Resident";
  private static final String DEFAULT_LABEL_2 = "Settled";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private ImmigrationStatusService immigrationStatusServiceMock;

  @MockBean
  private ImmigrationStatusMapper immigrationStatusMapperMock;

  private ImmigrationStatus immigrationStatus1;
  private ImmigrationStatus immigrationStatus2;
  private ImmigrationStatusDto immigrationStatusDto1;
  private ImmigrationStatusDto immigrationStatusDto2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  public void setup() {
    ImmigrationStatusResource immigrationStatusResource =
        new ImmigrationStatusResource(immigrationStatusServiceMock, immigrationStatusMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(immigrationStatusResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    immigrationStatus1 = new ImmigrationStatus();
    immigrationStatus1.setId(DEFAULT_ID_1);
    immigrationStatus1.setLabel(DEFAULT_LABEL_1);

    immigrationStatus2 = new ImmigrationStatus();
    immigrationStatus2.setId(DEFAULT_ID_2);
    immigrationStatus2.setLabel(DEFAULT_LABEL_2);

    immigrationStatusDto1 = new ImmigrationStatusDto();
    immigrationStatusDto1.setId(DEFAULT_ID_1);
    immigrationStatusDto1.setLabel(DEFAULT_LABEL_1);

    immigrationStatusDto2 = new ImmigrationStatusDto();
    immigrationStatusDto2.setId(DEFAULT_ID_2);
    immigrationStatusDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllImmigrationStatus() throws Exception {
    List<ImmigrationStatus> immigrationStatus = new ArrayList<>();
    immigrationStatus.add(immigrationStatus1);
    immigrationStatus.add(immigrationStatus2);
    List<ImmigrationStatusDto> immigrationStatusDtos = new ArrayList<>();
    immigrationStatusDtos.add(immigrationStatusDto1);
    immigrationStatusDtos.add(immigrationStatusDto2);
    when(immigrationStatusServiceMock.getImmigrationStatus()).thenReturn(immigrationStatus);
    when(immigrationStatusMapperMock.toDtos(immigrationStatus)).thenReturn(immigrationStatusDtos);
    this.mockMvc.perform(get("/api/immigration-status")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(immigrationStatusDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
