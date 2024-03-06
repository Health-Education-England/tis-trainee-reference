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

package uk.nhs.hee.tis.trainee.reference.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.mapper.ImmigrationStatusMapper;
import uk.nhs.hee.tis.trainee.reference.model.ImmigrationStatus;
import uk.nhs.hee.tis.trainee.reference.repository.ImmigrationStatusRepository;

@ExtendWith(MockitoExtension.class)
class ImmigrationStatusServiceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "EEA Resident";
  private static final String DEFAULT_LABEL_2 = "Settled";

  private ImmigrationStatusService service;

  @Mock
  private ImmigrationStatusRepository repository;

  private ImmigrationStatus immigrationStatus1;
  private ImmigrationStatus immigrationStatus2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new ImmigrationStatusService(repository,
        Mappers.getMapper(ImmigrationStatusMapper.class));

    immigrationStatus1 = new ImmigrationStatus();
    immigrationStatus1.setId(DEFAULT_ID_1);
    immigrationStatus1.setTisId(DEFAULT_TIS_ID_1);
    immigrationStatus1.setLabel(DEFAULT_LABEL_1);

    immigrationStatus2 = new ImmigrationStatus();
    immigrationStatus2.setId(DEFAULT_ID_2);
    immigrationStatus2.setTisId(DEFAULT_TIS_ID_2);
    immigrationStatus2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllImmigrationStatusShouldReturnAllImmigrationStatus() {
    List<ImmigrationStatus> immigrationStatus = new ArrayList<>();
    immigrationStatus.add(immigrationStatus1);
    immigrationStatus.add(immigrationStatus2);
    when(repository.findAll(Sort.by("label"))).thenReturn(immigrationStatus);
    List<ImmigrationStatus> allImmigrationStatus = service.get();
    assertThat("Unexpected size of returned ImmigrationStatus list",
        allImmigrationStatus.size(), equalTo(immigrationStatus.size()));
    assertThat("The returned immigration status list doesn't contain the expected immigration "
        + "status", allImmigrationStatus, hasItem(immigrationStatus1));
  }

  @Test
  void createImmigrationStatusShouldCreateImmigrationStatusWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(ImmigrationStatus.class))).thenAnswer(returnsFirstArg());

    ImmigrationStatus immigrationStatus = service.create(immigrationStatus2);

    assertThat("Unexpected id.", immigrationStatus.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", immigrationStatus.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", immigrationStatus.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void createImmigrationStatusShouldUpdateImmigrationStatusWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(immigrationStatus1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    ImmigrationStatus immigrationStatus = service.create(immigrationStatus2);

    assertThat("Unexpected id.", immigrationStatus.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", immigrationStatus.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", immigrationStatus.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateImmigrationStatusShouldCreateImmigrationStatusWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(ImmigrationStatus.class))).thenAnswer(returnsFirstArg());

    ImmigrationStatus immigrationStatus = service.update(immigrationStatus2);

    assertThat("Unexpected id.", immigrationStatus.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", immigrationStatus.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", immigrationStatus.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateImmigrationStatusShouldUpdateImmigrationStatusWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(immigrationStatus1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    ImmigrationStatus immigrationStatus = service.update(immigrationStatus2);

    assertThat("Unexpected id.", immigrationStatus.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", immigrationStatus.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", immigrationStatus.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldDeleteImmigrationStatussByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
