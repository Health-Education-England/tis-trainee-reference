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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.trainee.reference.dto.ImmigrationStatusDto;
import uk.nhs.hee.tis.trainee.reference.model.ImmigrationStatus;
import uk.nhs.hee.tis.trainee.reference.repository.ImmigrationStatusRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.ImmigrationStatusServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ImmigrationStatusServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_LABEL_1 = "EEA Resident";
  private static final String DEFAULT_LABEL_2 = "Settled";

  @InjectMocks
  private ImmigrationStatusServiceImpl immigrationStatusServiceImpl;

  @Mock
  private ImmigrationStatusRepository immigrationStatusRepositoryMock;

  private ImmigrationStatus immigrationStatus1;
  private ImmigrationStatus immigrationStatus2;
  private ImmigrationStatusDto immigrationStatusDto1;
  private ImmigrationStatusDto immigrationStatusDto2;

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    immigrationStatus1 = new ImmigrationStatus();
    immigrationStatus1.setId(DEFAULT_ID_1);
    immigrationStatus1.setLabel(DEFAULT_LABEL_1);

    immigrationStatus2 = new ImmigrationStatus();
    immigrationStatus2.setId(DEFAULT_ID_2);
    immigrationStatus2.setLabel(DEFAULT_LABEL_2);

    immigrationStatusDto1 = new ImmigrationStatusDto();
    immigrationStatusDto1.setId(DEFAULT_ID_1);
    immigrationStatusDto1.setLabel(DEFAULT_LABEL_1);

    immigrationStatusDto2 = new ImmigrationStatusDto();
    immigrationStatusDto2.setId(DEFAULT_ID_2);
    immigrationStatusDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  public void getAllImmigrationStatusShouldReturnAllImmigrationStatus() {
    List<ImmigrationStatus> immigrationStatus = new ArrayList<>();
    immigrationStatus.add(immigrationStatus1);
    immigrationStatus.add(immigrationStatus2);
    when(immigrationStatusRepositoryMock.findAll()).thenReturn(immigrationStatus);
    List<ImmigrationStatus> allImmigrationStatus =
        immigrationStatusServiceImpl.getImmigrationStatus();
    MatcherAssert.assertThat(
        "The size of returned immigration status list do not match the expected value",
        allImmigrationStatus.size(), CoreMatchers.equalTo(immigrationStatus.size()));
    MatcherAssert.assertThat(
        "The returned immigration status list doesn't not contain the expected gender",
        allImmigrationStatus, CoreMatchers.hasItem(immigrationStatus1));
  }
}
