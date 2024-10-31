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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.nhs.hee.tis.trainee.reference.dto.DeclarationTypeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.DeclarationTypeMapperImpl;
import uk.nhs.hee.tis.trainee.reference.model.DeclarationType;
import uk.nhs.hee.tis.trainee.reference.service.DeclarationTypeService;

class DeclarationTypeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_LABEL_1 = "Significant event";
  private static final String DEFAULT_LABEL_2 = "Other investigation";

  private DeclarationTypeResource controller;
  private DeclarationTypeService service;

  @BeforeEach
  public void setup() {
    service = mock(DeclarationTypeService.class);
    controller = new DeclarationTypeResource(service, new DeclarationTypeMapperImpl());
  }

  @Test
  void shouldGetAllDeclarationType() {
    DeclarationType entity1 = new DeclarationType();
    entity1.setId(DEFAULT_ID_1);
    entity1.setLabel(DEFAULT_LABEL_1);

    DeclarationType entity2 = new DeclarationType();
    entity2.setId(DEFAULT_ID_2);
    entity2.setLabel(DEFAULT_LABEL_2);

    when(service.getDeclarationType()).thenReturn(List.of(entity1, entity2));

    List<DeclarationTypeDto> dtos = controller.getDeclarationTypes();

    assertThat("Unexpected response count.", dtos, hasSize(2));

    DeclarationTypeDto dto1 = dtos.get(0);
    assertThat("Unexpected ID.", dto1.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected label.", dto1.getLabel(), is(DEFAULT_LABEL_1));

    DeclarationTypeDto dto2 = dtos.get(1);
    assertThat("Unexpected ID.", dto2.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected label.", dto2.getLabel(), is(DEFAULT_LABEL_2));
  }
}
