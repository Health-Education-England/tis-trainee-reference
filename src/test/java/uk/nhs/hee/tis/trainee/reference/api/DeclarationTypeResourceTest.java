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
import uk.nhs.hee.tis.trainee.reference.dto.DeclarationTypeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.DeclarationTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.DeclarationType;
import uk.nhs.hee.tis.trainee.reference.service.DeclarationTypeService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DeclarationTypeResource.class)
public class DeclarationTypeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_LABEL_1 = "Significant event";
  private static final String DEFAULT_LABEL_2 = "Other investigation";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  private MockMvc mockMvc;

  @MockBean
  private DeclarationTypeService declarationTypeServiceMock;

  @MockBean
  private DeclarationTypeMapper declarationTypeMapperMock;

  private DeclarationType declarationType1;
  private DeclarationType declarationType2;
  private DeclarationTypeDto declarationTypeDto1;
  private DeclarationTypeDto declarationTypeDto2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  public void setup() {
    DeclarationTypeResource declarationTypeResource =
        new DeclarationTypeResource(declarationTypeServiceMock, declarationTypeMapperMock);
    mockMvc = MockMvcBuilders.standaloneSetup(declarationTypeResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    declarationType1 = new DeclarationType();
    declarationType1.setId(DEFAULT_ID_1);
    declarationType1.setLabel(DEFAULT_LABEL_1);

    declarationType2 = new DeclarationType();
    declarationType2.setId(DEFAULT_ID_2);
    declarationType2.setLabel(DEFAULT_LABEL_2);

    declarationTypeDto1 = new DeclarationTypeDto();
    declarationTypeDto1.setId(DEFAULT_ID_1);
    declarationTypeDto1.setLabel(DEFAULT_LABEL_1);

    declarationTypeDto2 = new DeclarationTypeDto();
    declarationTypeDto2.setId(DEFAULT_ID_2);
    declarationTypeDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void testGetAllDeclarationType() throws Exception {
    List<DeclarationType> declarationType = new ArrayList<>();
    declarationType.add(declarationType1);
    declarationType.add(declarationType2);
    List<DeclarationTypeDto> declarationTypeDtos = new ArrayList<>();
    declarationTypeDtos.add(declarationTypeDto1);
    declarationTypeDtos.add(declarationTypeDto2);
    when(declarationTypeServiceMock.getDeclarationType()).thenReturn(declarationType);
    when(declarationTypeMapperMock.toDtos(declarationType)).thenReturn(declarationTypeDtos);
    this.mockMvc.perform(get("/api/declaration-type")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(declarationTypeDtos.size())))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_1)))
        .andExpect(jsonPath("$.[*].id").value(hasItem(DEFAULT_ID_2)));
  }
}
