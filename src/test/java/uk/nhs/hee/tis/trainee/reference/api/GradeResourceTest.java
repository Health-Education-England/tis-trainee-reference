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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static uk.nhs.hee.tis.trainee.reference.dto.Status.CURRENT;
import static uk.nhs.hee.tis.trainee.reference.dto.Status.INACTIVE;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.nhs.hee.tis.trainee.reference.dto.GradeDto;
import uk.nhs.hee.tis.trainee.reference.dto.validator.GradeValidator;
import uk.nhs.hee.tis.trainee.reference.mapper.GradeMapperImpl;
import uk.nhs.hee.tis.trainee.reference.model.Grade;
import uk.nhs.hee.tis.trainee.reference.service.GradeService;

class GradeResourceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Foundation Year 1";
  private static final String DEFAULT_LABEL_2 = "Core Training Year 2";

  private GradeResource controller;
  private GradeService service;
  private GradeValidator validator;

  @BeforeEach
  void setup() {
    service = mock(GradeService.class);
    validator = mock(GradeValidator.class);
    controller = new GradeResource(service, new GradeMapperImpl(), validator);
  }

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    GradeDto dto = new GradeDto();
    dto.setId(DEFAULT_ID_1);
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);

    Grade entity1 = new Grade();
    entity1.setId(DEFAULT_ID_1);
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLabel(DEFAULT_LABEL_1);

    Grade entity2 = new Grade();
    entity2.setId(DEFAULT_ID_2);
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void shouldGetAllGrades() {
    Grade entity1 = new Grade();
    entity1.setId(DEFAULT_ID_1);
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLabel(DEFAULT_LABEL_1);

    Grade entity2 = new Grade();
    entity2.setId(DEFAULT_ID_2);
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLabel(DEFAULT_LABEL_2);

    when(service.get()).thenReturn(List.of(entity1, entity2));

    ResponseEntity<List<GradeDto>> response = controller.getGrades();

    assertThat("Unexpected status code.", response.getStatusCode(), is(OK));

    List<GradeDto> dtos = response.getBody();
    assertThat("Unexpected response count.", dtos, hasSize(2));

    GradeDto dto1 = dtos.get(0);
    assertThat("Unexpected ID.", dto1.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", dto1.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", dto1.getLabel(), is(DEFAULT_LABEL_1));

    GradeDto dto2 = dtos.get(1);
    assertThat("Unexpected ID.", dto2.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS ID.", dto2.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", dto2.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldCreateGradeWhenCreateValid() {
    GradeDto dto = new GradeDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);
    dto.setPlacementGrade(true);
    dto.setTrainingGrade(false);
    dto.setStatus(CURRENT);

    when(validator.isValid(dto)).thenReturn(true);

    when(service.create(any())).thenAnswer(inv -> {
      Grade entity = inv.getArgument(0);
      assertThat("Unexpected ID.", entity.getId(), nullValue());
      entity.setId(DEFAULT_ID_1);
      return entity;
    });

    ResponseEntity<GradeDto> response = controller.createGrade(dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(CREATED));
    assertThat("Unexpected location.", response.getHeaders().getLocation(),
        is(URI.create("/api/grade")));

    GradeDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected ID.", body.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", body.getLabel(), is(DEFAULT_LABEL_1));
    assertThat("Unexpected placement grade flag.", body.isPlacementGrade(), is(false));
    assertThat("Unexpected training grade flag.", body.isTrainingGrade(), is(false));
    assertThat("Unexpected status.", body.getStatus(), nullValue());
  }

  @Test
  void shouldDeleteGradeWhenCreateInvalid() {
    GradeDto dto = new GradeDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);
    dto.setPlacementGrade(true);
    dto.setTrainingGrade(false);
    dto.setStatus(INACTIVE);

    when(validator.isValid(dto)).thenReturn(false);

    ResponseEntity<GradeDto> response = controller.createGrade(dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(UNPROCESSABLE_ENTITY));
    assertThat("Unexpected response body presence.", response.hasBody(), is(false));

    verify(service).deleteByTisId(DEFAULT_TIS_ID_1);
    verifyNoMoreInteractions(service);
  }

  @Test
  void shouldUpdateGradeWhenUpdateValid() {
    GradeDto dto = new GradeDto();
    dto.setId(DEFAULT_ID_1);
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);
    dto.setPlacementGrade(true);
    dto.setTrainingGrade(false);
    dto.setStatus(CURRENT);

    when(validator.isValid(dto)).thenReturn(true);

    when(service.update(any())).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<GradeDto> response = controller.updateGrade(dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(OK));

    GradeDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected ID.", body.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", body.getLabel(), is(DEFAULT_LABEL_1));
    assertThat("Unexpected placement grade flag.", body.isPlacementGrade(), is(false));
    assertThat("Unexpected training grade flag.", body.isTrainingGrade(), is(false));
    assertThat("Unexpected status.", body.getStatus(), nullValue());
  }

  @Test
  void shouldDeleteGradeWhenUpdateInvalid() {
    GradeDto dto = new GradeDto();
    dto.setId(DEFAULT_ID_1);
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLabel(DEFAULT_LABEL_1);
    dto.setPlacementGrade(true);
    dto.setTrainingGrade(false);
    dto.setStatus(INACTIVE);

    when(validator.isValid(dto)).thenReturn(false);

    ResponseEntity<GradeDto> response = controller.updateGrade(dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(UNPROCESSABLE_ENTITY));
    assertThat("Unexpected response body presence.", response.hasBody(), is(false));

    verify(service).deleteByTisId(DEFAULT_TIS_ID_1);
    verifyNoMoreInteractions(service);
  }

  @Test
  void shouldDeleteGrade() {
    ResponseEntity<Void> response = controller.deleteGrade(DEFAULT_TIS_ID_1);

    assertThat("Unexpected status code.", response.getStatusCode(), is(NO_CONTENT));
    assertThat("Unexpected response body presence.", response.hasBody(), is(false));

    verify(service).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
