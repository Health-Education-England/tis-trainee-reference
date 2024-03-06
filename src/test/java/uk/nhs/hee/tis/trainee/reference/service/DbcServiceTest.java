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
import uk.nhs.hee.tis.trainee.reference.mapper.DbcMapper;
import uk.nhs.hee.tis.trainee.reference.model.Dbc;
import uk.nhs.hee.tis.trainee.reference.repository.DbcRepository;

@ExtendWith(MockitoExtension.class)
class DbcServiceTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final boolean DEFAULT_INTERNAL_1 = true;
  private static final boolean DEFAULT_INTERNAL_2 = false;

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Health Education England East of England";
  private static final String DEFAULT_LABEL_2 =
      "Northern Ireland Medical and Dental Training Agency";

  private static final String DEFAULT_TYPE_1 = "DEFAULT_TYPE_1";
  private static final String DEFAULT_TYPE_2 = "DEFAULT_TYPE_2";

  private DbcService service;

  @Mock
  private DbcRepository repository;

  private Dbc dbc1;
  private Dbc dbc2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new DbcService(repository, Mappers.getMapper(DbcMapper.class));

    dbc1 = new Dbc();
    dbc1.setId(DEFAULT_ID_1);
    dbc1.setTisId(DEFAULT_TIS_ID_1);
    dbc1.setLabel(DEFAULT_LABEL_1);
    dbc1.setType(DEFAULT_TYPE_1);
    dbc1.setInternal(DEFAULT_INTERNAL_1);

    dbc2 = new Dbc();
    dbc2.setId(DEFAULT_ID_2);
    dbc2.setTisId(DEFAULT_TIS_ID_2);
    dbc2.setLabel(DEFAULT_LABEL_2);
    dbc2.setType(DEFAULT_TYPE_2);
    dbc2.setInternal(DEFAULT_INTERNAL_2);
  }

  @Test
  void getAllDbcShouldReturnAllDbcs() {
    List<Dbc> dbcs = new ArrayList<>();
    dbcs.add(dbc1);
    dbcs.add(dbc2);
    when(repository.findAll(Sort.by("label"))).thenReturn(dbcs);
    List<Dbc> allDbcs = service.get();
    assertThat("Unexpected size of returned Dbc list",
        allDbcs.size(), equalTo(dbcs.size()));
    assertThat("The returned dbc list doesn't contain the expected Dbc",
        allDbcs, hasItem(dbc1));
  }

  @Test
  void createDbcShouldCreateDbcWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(Dbc.class))).thenAnswer(returnsFirstArg());

    Dbc dbc = service.create(dbc2);

    assertThat("Unexpected id.", dbc.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", dbc.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", dbc.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected type.", dbc.getType(), is(DEFAULT_TYPE_2));
    assertThat("Unexpected internal flag.", dbc.isInternal(), is(DEFAULT_INTERNAL_2));
  }

  @Test
  void createDbcShouldUpdateDbcWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(dbc1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    Dbc dbc = service.create(dbc2);

    assertThat("Unexpected id.", dbc.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", dbc.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", dbc.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected type.", dbc.getType(), is(DEFAULT_TYPE_2));
    assertThat("Unexpected internal flag.", dbc.isInternal(), is(DEFAULT_INTERNAL_2));
  }

  @Test
  void updateDbcShouldCreateDbcWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(Dbc.class))).thenAnswer(returnsFirstArg());

    Dbc dbc = service.update(dbc2);

    assertThat("Unexpected id.", dbc.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", dbc.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", dbc.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected type.", dbc.getType(), is(DEFAULT_TYPE_2));
    assertThat("Unexpected internal flag.", dbc.isInternal(), is(DEFAULT_INTERNAL_2));
  }

  @Test
  void updateDbcShouldUpdateDbcWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(dbc1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    Dbc dbc = service.update(dbc2);

    assertThat("Unexpected id.", dbc.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", dbc.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", dbc.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected type.", dbc.getType(), is(DEFAULT_TYPE_2));
    assertThat("Unexpected internal flag.", dbc.isInternal(), is(DEFAULT_INTERNAL_2));
  }

  @Test
  void shouldDeleteDbcsByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
