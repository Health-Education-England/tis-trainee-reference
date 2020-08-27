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
import uk.nhs.hee.tis.trainee.reference.mapper.GenderMapper;
import uk.nhs.hee.tis.trainee.reference.model.Gender;
import uk.nhs.hee.tis.trainee.reference.repository.GenderRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.GenderServiceImpl;

@ExtendWith(MockitoExtension.class)
class GenderServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Male";
  private static final String DEFAULT_LABEL_2 = "Female";

  private GenderServiceImpl service;

  @Mock
  private GenderRepository repository;

  private Gender gender1;
  private Gender gender2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new GenderServiceImpl(repository, Mappers.getMapper(GenderMapper.class));

    gender1 = new Gender();
    gender1.setId(DEFAULT_ID_1);
    gender1.setTisId(DEFAULT_TIS_ID_1);
    gender1.setLabel(DEFAULT_LABEL_1);

    gender2 = new Gender();
    gender2.setId(DEFAULT_ID_2);
    gender2.setTisId(DEFAULT_TIS_ID_2);
    gender2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllGendersShouldReturnAllGenders() {
    List<Gender> genders = new ArrayList<>();
    genders.add(gender1);
    genders.add(gender2);
    when(repository.findAll()).thenReturn(genders);
    List<Gender> allGenders = service.getGender();
    MatcherAssert.assertThat("The size of returned gender list do not match the expected value",
        allGenders.size(), CoreMatchers.equalTo(genders.size()));
    MatcherAssert.assertThat("The returned gender list doesn't not contain the expected gender",
        allGenders, CoreMatchers.hasItem(gender1));
  }

  @Test
  void createGenderShouldCreateGenderWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(Gender.class))).thenAnswer(returnsFirstArg());

    Gender gender = service.createGender(gender2);

    assertThat("Unexpected id.", gender.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", gender.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", gender.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void createGenderShouldUpdateGenderWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(gender1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    Gender gender = service.createGender(gender2);

    assertThat("Unexpected id.", gender.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", gender.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", gender.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateGenderShouldCreateGenderWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(Gender.class))).thenAnswer(returnsFirstArg());

    Gender gender = service.updateGender(gender2);

    assertThat("Unexpected id.", gender.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", gender.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", gender.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateGenderShouldUpdateGenderWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(gender1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    Gender gender = service.updateGender(gender2);

    assertThat("Unexpected id.", gender.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", gender.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", gender.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldDeleteGendersByTisId() {
    service.deleteGender(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
