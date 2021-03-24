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

package uk.nhs.hee.tis.trainee.reference.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeRepository;

@ExtendWith(MockitoExtension.class)
class LocalOfficeServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Health Education England East of England";
  private static final String DEFAULT_LABEL_2 =
      "Northern Ireland Medical and Dental Training Agency";

  private LocalOfficeService service;

  @Mock
  private LocalOfficeRepository repository;

  private LocalOffice localOffice1;
  private LocalOffice localOffice2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new LocalOfficeService(repository, Mappers.getMapper(LocalOfficeMapper.class));

    localOffice1 = new LocalOffice();
    localOffice1.setId(DEFAULT_ID_1);
    localOffice1.setTisId(DEFAULT_TIS_ID_1);
    localOffice1.setLabel(DEFAULT_LABEL_1);

    localOffice2 = new LocalOffice();
    localOffice2.setId(DEFAULT_ID_2);
    localOffice2.setTisId(DEFAULT_TIS_ID_2);
    localOffice2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllLocalOfficeShouldReturnAllLocalOffices() {
    List<LocalOffice> localOffices = new ArrayList<>();
    localOffices.add(localOffice1);
    localOffices.add(localOffice2);
    when(repository.findAll(Sort.by("label"))).thenReturn(localOffices);
    List<LocalOffice> allLocalOffices = service.get();
    MatcherAssert
        .assertThat("The size of returned local office list do not match the expected value",
            allLocalOffices.size(), CoreMatchers.equalTo(localOffices.size()));
    MatcherAssert
        .assertThat("The returned local office list doesn't not contain the expected local office",
            allLocalOffices, CoreMatchers.hasItem(localOffice1));
  }

  @Test
  void createLocalOfficeShouldCreateLocalOfficeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(LocalOffice.class))).thenAnswer(returnsFirstArg());

    LocalOffice localOffice = service.create(localOffice2);

    assertThat("Unexpected id.", localOffice.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", localOffice.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", localOffice.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void createLocalOfficeShouldUpdateLocalOfficeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(localOffice1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    LocalOffice localOffice = service.create(localOffice2);

    assertThat("Unexpected id.", localOffice.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", localOffice.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", localOffice.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateLocalOfficeShouldCreateLocalOfficeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(LocalOffice.class))).thenAnswer(returnsFirstArg());

    LocalOffice localOffice = service.update(localOffice2);

    assertThat("Unexpected id.", localOffice.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", localOffice.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", localOffice.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateLocalOfficeShouldUpdateLocalOfficeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(localOffice1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    LocalOffice localOffice = service.update(localOffice2);

    assertThat("Unexpected id.", localOffice.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", localOffice.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", localOffice.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldDeleteLocalOfficesByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
