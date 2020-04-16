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
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeDto;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.LocalOfficeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class LocalOfficeServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Health Education England East of England";
  private static final String DEFAULT_LABEL_2 =
      "Northern Ireland Medical and Dental Training Agency";

  private static final String DEFAULT_ENTITY_ID_1 = "1";
  private static final String DEFAULT_ENTITY_ID_2 = "2";

  @InjectMocks
  private LocalOfficeServiceImpl localOfficeServiceImpl;

  @Mock
  private LocalOfficeRepository localOfficeRepositoryMock;

  private LocalOffice localOffice1;
  private LocalOffice localOffice2;
  private LocalOfficeDto localOfficeDto1;
  private LocalOfficeDto localOfficeDto2;

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    localOffice1 = new LocalOffice();
    localOffice1.setId(DEFAULT_ID_1);
    localOffice1.setLocalOfficeTisId(DEFAULT_TIS_ID_1);
    localOffice1.setLabel(DEFAULT_LABEL_1);
    localOffice1.setEntityId(DEFAULT_ENTITY_ID_1);

    localOffice2 = new LocalOffice();
    localOffice2.setId(DEFAULT_ID_2);
    localOffice2.setLocalOfficeTisId(DEFAULT_TIS_ID_2);
    localOffice2.setLabel(DEFAULT_LABEL_2);
    localOffice2.setEntityId(DEFAULT_ENTITY_ID_2);

    localOfficeDto1 = new LocalOfficeDto();
    localOfficeDto1.setId(DEFAULT_ID_1);
    localOfficeDto1.setLocalOfficeTisId(DEFAULT_TIS_ID_1);
    localOfficeDto1.setLabel(DEFAULT_LABEL_1);
    localOfficeDto1.setEntityId(DEFAULT_ENTITY_ID_1);

    localOfficeDto2 = new LocalOfficeDto();
    localOfficeDto2.setId(DEFAULT_ID_2);
    localOfficeDto2.setLocalOfficeTisId(DEFAULT_TIS_ID_2);
    localOfficeDto2.setLabel(DEFAULT_LABEL_2);
    localOfficeDto2.setEntityId(DEFAULT_ENTITY_ID_2);
  }

  @Test
  public void getAllLocalOfficeShouldReturnAllLocalOffices() {
    List<LocalOffice> localOffices = new ArrayList<>();
    localOffices.add(localOffice1);
    localOffices.add(localOffice2);
    when(localOfficeRepositoryMock.findAll()).thenReturn(localOffices);
    List<LocalOffice> allLocalOffices = localOfficeServiceImpl.getLocalOffice();
    MatcherAssert
        .assertThat("The size of returned local office list do not match the expected value",
            allLocalOffices.size(), CoreMatchers.equalTo(localOffices.size()));
    MatcherAssert
        .assertThat("The returned local office list doesn't not contain the expected local office",
            allLocalOffices, CoreMatchers.hasItem(localOffice1));
  }
}
