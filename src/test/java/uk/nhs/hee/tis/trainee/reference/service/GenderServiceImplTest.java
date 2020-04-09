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
import uk.nhs.hee.tis.trainee.reference.dto.GenderDto;
import uk.nhs.hee.tis.trainee.reference.model.Gender;
import uk.nhs.hee.tis.trainee.reference.repository.GenderRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.GenderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GenderServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Male";
  private static final String DEFAULT_LABEL_2 = "Female";

  @InjectMocks
  private GenderServiceImpl genderServiceImpl;

  @Mock
  private GenderRepository genderRepositoryMock;

  private Gender gender1, gender2;
  private GenderDto genderDto1, genderDto2;

  @BeforeEach
  public void initData() {
    gender1 = new Gender();
    gender1.setId(DEFAULT_ID_1);
    gender1.setGenderTisId(DEFAULT_TIS_ID_1);
    gender1.setLabel(DEFAULT_LABEL_1);

    gender2 = new Gender();
    gender2.setId(DEFAULT_ID_2);
    gender2.setGenderTisId(DEFAULT_TIS_ID_2);
    gender2.setLabel(DEFAULT_LABEL_2);

    genderDto1 = new GenderDto();
    genderDto1.setId(DEFAULT_ID_1);
    genderDto1.setGenderTisId(DEFAULT_TIS_ID_1);
    genderDto1.setLabel(DEFAULT_LABEL_1);

    genderDto2 = new GenderDto();
    genderDto2.setId(DEFAULT_ID_2);
    genderDto2.setGenderTisId(DEFAULT_TIS_ID_2);
    genderDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  public void getAllGendersShouldReturnAllGenders() {
    List<Gender> genders = new ArrayList<>();
    genders.add(gender1);
    genders.add(gender2);
    when(genderRepositoryMock.findAll()).thenReturn(genders);
    List<Gender> allGenders = genderServiceImpl.getGender();
    MatcherAssert.assertThat("The size of returned gender list do not match the expected value",
        allGenders.size(), CoreMatchers.equalTo(genders.size()));
    MatcherAssert.assertThat("The returned gender list doesn't not contain the expected gender",
        allGenders, CoreMatchers.hasItem(gender1));
  }
}
