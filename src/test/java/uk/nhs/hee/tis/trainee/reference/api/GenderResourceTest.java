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
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.nhs.hee.tis.trainee.reference.dto.GenderDto;
import uk.nhs.hee.tis.trainee.reference.mapper.GenderMapperImpl;
import uk.nhs.hee.tis.trainee.reference.model.Gender;
import uk.nhs.hee.tis.trainee.reference.service.GenderService;

class GenderResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Male";
  private static final String DEFAULT_LABEL_2 = "Female";

  private GenderResource controller;
  private GenderService service;

  @BeforeEach
  void setup() {
    service = mock(GenderService.class);
    controller = new GenderResource(service, new GenderMapperImpl());
  }

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    Gender entity1 = new Gender();
    entity1.setId(DEFAULT_ID_1);
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLabel(DEFAULT_LABEL_1);

    Gender entity2 = new Gender();
    entity2.setId(DEFAULT_ID_2);
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void shouldGetAllGenders() {
    Gender entity1 = new Gender();
    entity1.setId(DEFAULT_ID_1);
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLabel(DEFAULT_LABEL_1);

    Gender entity2 = new Gender();
    entity2.setId(DEFAULT_ID_2);
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLabel(DEFAULT_LABEL_2);

    when(service.get()).thenReturn(List.of(entity1, entity2));

    List<GenderDto> dtos = controller.getGenders();

    assertThat("Unexpected response count.", dtos, hasSize(2));

    GenderDto dto1 = dtos.get(0);
    assertThat("Unexpected ID.", dto1.getId(), CoreMatchers.is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", dto1.getTisId(), CoreMatchers.is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", dto1.getLabel(), CoreMatchers.is(DEFAULT_LABEL_1));

    GenderDto dto2 = dtos.get(1);
    assertThat("Unexpected ID.", dto2.getId(), CoreMatchers.is(DEFAULT_ID_2));
    assertThat("Unexpected TIS ID.", dto2.getTisId(), CoreMatchers.is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", dto2.getLabel(), CoreMatchers.is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldCreateGender() {
    GenderDto dto = new GenderDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);

    when(service.create(any())).thenAnswer(inv -> {
      Gender entity = inv.getArgument(0);
      assertThat("Unexpected ID.", entity.getId(), nullValue());
      entity.setId(DEFAULT_ID_1);
      return entity;
    });

    ResponseEntity<GenderDto> response = controller.createGender(dto);

    assertThat("Unexpected status code.", response.getStatusCode(), CoreMatchers.is(CREATED));
    assertThat("Unexpected location.", response.getHeaders().getLocation(),
        CoreMatchers.is(URI.create("/api/gender")));

    GenderDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected ID.", body.getId(), CoreMatchers.is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", body.getTisId(), CoreMatchers.is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", body.getLabel(), CoreMatchers.is(DEFAULT_LABEL_1));
  }

  @Test
  void shouldUpdateGender() {
    GenderDto dto = new GenderDto();
    dto.setId(DEFAULT_ID_1);
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);

    when(service.update(any())).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<GenderDto> response = controller.updateGender(dto);

    assertThat("Unexpected status code.", response.getStatusCode(), CoreMatchers.is(OK));

    GenderDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected ID.", body.getId(), CoreMatchers.is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", body.getTisId(), CoreMatchers.is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", body.getLabel(), CoreMatchers.is(DEFAULT_LABEL_1));
  }

  @Test
  void shouldDeleteGender() {
    ResponseEntity<Void> response = controller.deleteGender(DEFAULT_TIS_ID_1);

    assertThat("Unexpected status code.", response.getStatusCode(), CoreMatchers.is(NO_CONTENT));
    assertThat("Unexpected response body presence.", response.hasBody(), CoreMatchers.is(false));

    verify(service).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
