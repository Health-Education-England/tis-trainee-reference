/*
 * The MIT License (MIT)
 *
 * Copyright 2021 Crown Copyright (Health Education England)
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
import uk.nhs.hee.tis.trainee.reference.dto.DbcDto;
import uk.nhs.hee.tis.trainee.reference.mapper.DbcMapperImpl;
import uk.nhs.hee.tis.trainee.reference.model.Dbc;
import uk.nhs.hee.tis.trainee.reference.service.DbcService;

class DbcResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final boolean DEFAULT_INTERNAL_1 = true;
  private static final boolean DEFAULT_INTERNAL_2 = false;

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "East of England";
  private static final String DEFAULT_LABEL_2 =
      "Northern Ireland Medical and Dental Training Agency";

  private static final String DEFAULT_TYPE_1 = "DEFAULT_TYPE_1";
  private static final String DEFAULT_TYPE_2 = "DEFAULT_TYPE_2";

  private DbcResource controller;
  private DbcService service;

  @BeforeEach
  void setup() {
    service = mock(DbcService.class);
    controller = new DbcResource(service, new DbcMapperImpl());
  }

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    Dbc entity1 = new Dbc();
    entity1.setId(DEFAULT_ID_1);
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLabel(DEFAULT_LABEL_1);
    entity1.setType(DEFAULT_TYPE_1);
    entity1.setInternal(DEFAULT_INTERNAL_1);

    Dbc entity2 = new Dbc();
    entity2.setId(DEFAULT_ID_2);
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLabel(DEFAULT_LABEL_2);
    entity2.setType(DEFAULT_TYPE_2);
    entity2.setInternal(DEFAULT_INTERNAL_2);
  }

  @Test
  void shouldGetAllDbcs() {
    Dbc entity1 = new Dbc();
    entity1.setId(DEFAULT_ID_1);
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLabel(DEFAULT_LABEL_1);
    entity1.setType(DEFAULT_TYPE_1);
    entity1.setInternal(DEFAULT_INTERNAL_1);

    Dbc entity2 = new Dbc();
    entity2.setId(DEFAULT_ID_2);
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLabel(DEFAULT_LABEL_2);
    entity2.setType(DEFAULT_TYPE_2);
    entity2.setInternal(DEFAULT_INTERNAL_2);

    when(service.get()).thenReturn(List.of(entity1, entity2));

    List<DbcDto> dtos = controller.getDbcs();

    assertThat("Unexpected response count.", dtos, hasSize(2));

    DbcDto dto1 = dtos.get(0);
    assertThat("Unexpected ID.", dto1.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", dto1.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", dto1.getLabel(), is(DEFAULT_LABEL_1));
    assertThat("Unexpected type.", dto1.getType(), is(DEFAULT_TYPE_1));
    assertThat("Unexpected internal flag.", dto1.isInternal(), is(DEFAULT_INTERNAL_1));

    DbcDto dto2 = dtos.get(1);
    assertThat("Unexpected ID.", dto2.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS ID.", dto2.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", dto2.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected type.", dto2.getType(), is(DEFAULT_TYPE_2));
    assertThat("Unexpected internal flag.", dto2.isInternal(), is(DEFAULT_INTERNAL_2));
  }

  @Test
  void shouldCreateDbc() {
    DbcDto dto = new DbcDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);
    dto.setType(DEFAULT_TYPE_1);
    dto.setInternal(DEFAULT_INTERNAL_1);

    when(service.create(any())).thenAnswer(inv -> {
      Dbc entity = inv.getArgument(0);
      assertThat("Unexpected ID.", entity.getId(), nullValue());
      entity.setId(DEFAULT_ID_1);
      return entity;
    });

    ResponseEntity<DbcDto> response = controller.createDbc(dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(CREATED));
    assertThat("Unexpected location.", response.getHeaders().getLocation(),
        is(URI.create("/api/dbc")));

    DbcDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected ID.", body.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", body.getLabel(), is(DEFAULT_LABEL_1));
    assertThat("Unexpected type.", body.getType(), is(DEFAULT_TYPE_1));
    assertThat("Unexpected internal flag.", body.isInternal(), is(DEFAULT_INTERNAL_1));
  }

  @Test
  void shouldUpdateDbc() {
    DbcDto dto = new DbcDto();
    dto.setId(DEFAULT_ID_1);
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);
    dto.setType(DEFAULT_TYPE_1);
    dto.setInternal(DEFAULT_INTERNAL_1);

    when(service.update(any())).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<DbcDto> response = controller.updateDbc(dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(OK));
    assertThat("Unexpected response body presence.", response.hasBody(), is(true));

    DbcDto body = response.getBody();

    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected ID.", body.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", body.getLabel(), is(DEFAULT_LABEL_1));
    assertThat("Unexpected type.", body.getType(), is(DEFAULT_TYPE_1));
    assertThat("Unexpected internal flag.", body.isInternal(), is(DEFAULT_INTERNAL_1));
  }

  @Test
  void shouldDeleteDbc() {
    ResponseEntity<Void> response = controller.deleteDbc(DEFAULT_TIS_ID_1);

    assertThat("Unexpected status code.", response.getStatusCode(), is(NO_CONTENT));
    assertThat("Unexpected response body presence.", response.hasBody(), is(false));

    verify(service).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
