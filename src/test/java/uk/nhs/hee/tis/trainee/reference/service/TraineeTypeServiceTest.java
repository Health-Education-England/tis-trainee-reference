/*
 * The MIT License (MIT)
 *
 * Copyright 2026 Crown Copyright (Health Education England)
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

package uk.nhs.hee.tis.trainee.reference.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.mapper.TraineeTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.TraineeType;
import uk.nhs.hee.tis.trainee.reference.repository.TraineeTypeRepository;

@ExtendWith(MockitoExtension.class)
class TraineeTypeServiceTest {

  private static final String DEFAULT_TIS_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_TIS_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_LABEL_1 = "Specialty";
  private static final String DEFAULT_LABEL_2 = "Foundation";

  private static final String DEFAULT_CODE_1 = "SPECIALTY";
  private static final String DEFAULT_CODE_2 = "FOUNDATION";


  private TraineeTypeService service;

  @Mock
  private TraineeTypeRepository repository;

  @Mock
  private LocalOfficeContactService contactService;

  private TraineeType traineeType1;
  private TraineeType traineeType2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new TraineeTypeService(repository,
        Mappers.getMapper(TraineeTypeMapper.class), contactService, new ObjectMapper());

    traineeType1 = new TraineeType();
    traineeType1.setTisId(DEFAULT_TIS_ID_1);
    traineeType1.setLabel(DEFAULT_LABEL_1);
    traineeType1.setCode(DEFAULT_CODE_1);

    traineeType2 = new TraineeType();
    traineeType2.setTisId(DEFAULT_TIS_ID_2);
    traineeType2.setLabel(DEFAULT_LABEL_2);
    traineeType2.setCode(DEFAULT_CODE_2);
  }

  @Test
  void getAllTraineeTypeShouldReturnAllTraineeTypes() {
    List<TraineeType> traineeTypes = new ArrayList<>();
    traineeTypes.add(traineeType1);
    traineeTypes.add(traineeType2);
    when(repository.findAll(Sort.by("label"))).thenReturn(traineeTypes);
    List<TraineeType> allTraineeTypes = service.get();
    assertThat("Unexpected size of returned TraineeTypes list",
        allTraineeTypes.size(), equalTo(traineeTypes.size()));
    assertThat("The trainee types list doesn't contain an expected trainee type",
        allTraineeTypes, hasItem(traineeType1));
  }

  @Test
  void createTraineeTypesShouldCreateTraineeTypeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(TraineeType.class))).thenAnswer(returnsFirstArg());

    TraineeType traineeType = service.create(traineeType2);

    verify(contactService).updateAllForTraineeType(traineeType2);

    assertThat("Unexpected TIS id.", traineeType.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", traineeType.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected code.", traineeType.getCode(), is(DEFAULT_CODE_2));
  }

  @Test
  void createTraineeTypeShouldUpdateTraineeTypeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(traineeType1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    TraineeType traineeType = service.create(traineeType2);

    verify(contactService, atLeastOnce()).updateAllForTraineeType(traineeType1);

    assertThat("Unexpected TIS id.", traineeType.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", traineeType.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected code.", traineeType.getCode(), is(DEFAULT_CODE_2));
  }

  @Test
  void updateTraineeTypeShouldCreateTraineeTypeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(TraineeType.class))).thenAnswer(returnsFirstArg());

    TraineeType traineeType = service.update(traineeType2);

    verify(contactService, atLeastOnce()).updateAllForTraineeType(traineeType2);

    assertThat("Unexpected TIS id.", traineeType.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", traineeType.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected code.", traineeType.getCode(), is(DEFAULT_CODE_2));
  }

  @Test
  void updateTraineeTypeShouldUpdateTraineeTypeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(traineeType1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    TraineeType traineeType = service.update(traineeType2);

    verify(contactService).updateAllForTraineeType(traineeType1);

    assertThat("Unexpected TIS id.", traineeType.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", traineeType.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected code.", traineeType.getCode(), is(DEFAULT_CODE_2));
  }

  @Test
  void shouldDeleteTraineeTypeByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }

  @Test
  void shouldThrowExceptionCreatingTraineeTypeWithPatch() throws IOException {
    String patch = """
        [{"op":"add","path":"","value":{"id":"%s","name":"New"}}]
        """.formatted(DEFAULT_TIS_ID_1);
    JsonPatch jsonPatch = JsonPatch.fromJson(new ObjectMapper().readTree(patch));

    assertThrows(NotImplementedException.class,
        () -> service.create(new TraineeType(), jsonPatch));

    verify(repository, never()).insert(any(TraineeType.class));
  }

  @Test
  void shouldThrowExceptionUpdatingTraineeTypeWithPatch() throws IOException {
    String patch = """
        [{"op":"replace","path":"","value":{"id":"%s","name":"Updated"}}]
        """.formatted(DEFAULT_TIS_ID_1);
    JsonPatch jsonPatch = JsonPatch.fromJson(new ObjectMapper().readTree(patch));

    when(repository.findByTisId(DEFAULT_TIS_ID_1)).thenReturn(traineeType1);

    assertThrows(NotImplementedException.class, () -> service.update(DEFAULT_TIS_ID_1, jsonPatch));

    verify(repository, never()).save(any());
  }
}
