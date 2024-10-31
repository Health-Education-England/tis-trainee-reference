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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeContactTypeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactTypeMapperImpl;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeContactTypeService;

class LocalOfficeContactTypeResourceTest {

  private static final String DEFAULT_TIS_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_TIS_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_LABEL_1 = "Deferral";
  private static final String DEFAULT_LABEL_2 = "Onboarding Support";

  private static final String DEFAULT_CODE_1 = "DEFERRAL";
  private static final String DEFAULT_CODE_2 = "ONBOARDING_SUPPORT";

  private LocalOfficeContactTypeResource controller;
  private LocalOfficeContactTypeService service;

  @BeforeEach
  void setup() {
    service = mock(LocalOfficeContactTypeService.class);
    controller = new LocalOfficeContactTypeResource(service,
        new LocalOfficeContactTypeMapperImpl());
  }

  @Test
  void shouldGetAllLocalOfficeContactTypes() {
    LocalOfficeContactType entity1 = new LocalOfficeContactType();
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setCode(DEFAULT_CODE_1);
    entity1.setLabel(DEFAULT_LABEL_1);

    LocalOfficeContactType entity2 = new LocalOfficeContactType();
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setCode(DEFAULT_CODE_2);
    entity2.setLabel(DEFAULT_LABEL_2);

    when(service.get()).thenReturn(List.of(entity1, entity2));

    List<LocalOfficeContactTypeDto> dtos = controller.getLocalOfficeContactTypes();

    assertThat("Unexpected response count.", dtos, hasSize(2));

    LocalOfficeContactTypeDto dto1 = dtos.get(0);
    assertThat("Unexpected TIS ID.", dto1.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected code.", dto1.getCode(), is(DEFAULT_CODE_1));
    assertThat("Unexpected label.", dto1.getLabel(), is(DEFAULT_LABEL_1));

    LocalOfficeContactTypeDto dto2 = dtos.get(1);
    assertThat("Unexpected TIS ID.", dto2.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected code.", dto2.getCode(), is(DEFAULT_CODE_2));
    assertThat("Unexpected label.", dto2.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldCreateLocalOfficeContactType() {
    LocalOfficeContactTypeDto dto = new LocalOfficeContactTypeDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setCode(DEFAULT_CODE_1);
    dto.setLabel(DEFAULT_LABEL_1);

    when(service.create(any())).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<LocalOfficeContactTypeDto> response = controller.createLocalOfficeContactType(
        dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(CREATED));
    assertThat("Unexpected location.", response.getHeaders().getLocation(),
        is(URI.create("/api/local-office-contact-type")));

    LocalOfficeContactTypeDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected code.", body.getCode(), is(DEFAULT_CODE_1));
    assertThat("Unexpected label.", body.getLabel(), is(DEFAULT_LABEL_1));
  }

  @Test
  void shouldUpdateLocalOfficeContactType() {
    LocalOfficeContactTypeDto dto = new LocalOfficeContactTypeDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setCode(DEFAULT_CODE_1);
    dto.setLabel(DEFAULT_LABEL_1);

    when(service.update(any())).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<LocalOfficeContactTypeDto> response = controller.updateLocalOfficeContactType(
        dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(OK));

    LocalOfficeContactTypeDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected code.", body.getCode(), is(DEFAULT_CODE_1));
    assertThat("Unexpected label.", body.getLabel(), is(DEFAULT_LABEL_1));
  }

  @Test
  void shouldDeleteLocalOfficeContactType() {
    ResponseEntity<Void> response = controller.deleteLocalOfficeContactType(DEFAULT_TIS_ID_1);

    assertThat("Unexpected status code.", response.getStatusCode(), is(NO_CONTENT));
    assertThat("Unexpected response body presence.", response.hasBody(), is(false));

    verify(service).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
