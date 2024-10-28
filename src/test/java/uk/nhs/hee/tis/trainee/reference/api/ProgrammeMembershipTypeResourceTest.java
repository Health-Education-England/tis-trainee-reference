/*
 * The MIT License (MIT)
 *
 * Copyright 2023 Crown Copyright (Health Education England)
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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.nhs.hee.tis.trainee.reference.dto.ProgrammeMembershipTypeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.ProgrammeMembershipTypeMapperImpl;
import uk.nhs.hee.tis.trainee.reference.model.ProgrammeMembershipType;
import uk.nhs.hee.tis.trainee.reference.service.ProgrammeMembershipTypeService;

class ProgrammeMembershipTypeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Substantive";
  private static final String DEFAULT_LABEL_2 = "Military";

  private ProgrammeMembershipTypeResource controller;
  private ProgrammeMembershipTypeService service;

  @BeforeEach
  void setup() {
    service = mock(ProgrammeMembershipTypeService.class);
    controller = new ProgrammeMembershipTypeResource(service,
        new ProgrammeMembershipTypeMapperImpl());
  }

  @Test
  void shouldGetAllProgrammeMembershipTypes() {
    ProgrammeMembershipType entity1 = new ProgrammeMembershipType();
    entity1.setId(DEFAULT_ID_1);
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLabel(DEFAULT_LABEL_1);

    ProgrammeMembershipType entity2 = new ProgrammeMembershipType();
    entity2.setId(DEFAULT_ID_2);
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLabel(DEFAULT_LABEL_2);

    when(service.get()).thenReturn(List.of(entity1, entity2));

    ResponseEntity<List<ProgrammeMembershipTypeDto>> response = controller.getProgrammeMembershipTypes();

    assertThat("Unexpected status code.", response.getStatusCode(), is(OK));

    List<ProgrammeMembershipTypeDto> dtos = response.getBody();
    assertThat("Unexpected response count.", dtos, hasSize(2));

    ProgrammeMembershipTypeDto dto1 = dtos.get(0);
    assertThat("Unexpected ID.", dto1.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", dto1.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", dto1.getLabel(), is(DEFAULT_LABEL_1));

    ProgrammeMembershipTypeDto dto2 = dtos.get(1);
    assertThat("Unexpected ID.", dto2.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS ID.", dto2.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", dto2.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldCreateProgrammeMembershipType() {
    ProgrammeMembershipTypeDto dto = new ProgrammeMembershipTypeDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);

    when(service.create(any())).thenAnswer(inv -> {
      ProgrammeMembershipType entity = inv.getArgument(0);
      assertThat("Unexpected ID.", entity.getId(), nullValue());
      entity.setId(DEFAULT_ID_1);
      return entity;
    });

    ResponseEntity<ProgrammeMembershipTypeDto> response = controller.createProgrammeMembershipType(
        dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(CREATED));
    assertThat("Unexpected location.", response.getHeaders().getLocation(),
        is(URI.create("/api/programme-membership-type")));

    ProgrammeMembershipTypeDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected ID.", body.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", body.getLabel(), is(DEFAULT_LABEL_1));
  }

  @Test
  void shouldUpdateProgrammeMembershipType() {
    ProgrammeMembershipTypeDto dto = new ProgrammeMembershipTypeDto();
    dto.setId(DEFAULT_ID_1);
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);

    when(service.update(any())).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<ProgrammeMembershipTypeDto> response = controller.updateProgrammeMembershipType(
        dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(OK));

    ProgrammeMembershipTypeDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected ID.", body.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", body.getLabel(), is(DEFAULT_LABEL_1));
  }

  @Test
  void shouldDeleteProgrammeMembershipType() {
    ResponseEntity<Void> response = controller.deleteProgrammeMembershipType(DEFAULT_TIS_ID_1);

    assertThat("Unexpected status code.", response.getStatusCode(), is(NO_CONTENT));
    assertThat("Unexpected response body presence.", response.hasBody(), is(false));

    verify(service).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
