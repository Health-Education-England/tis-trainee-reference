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
import uk.nhs.hee.tis.trainee.reference.mapper.CollegeMapper;
import uk.nhs.hee.tis.trainee.reference.model.College;
import uk.nhs.hee.tis.trainee.reference.repository.CollegeRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.CollegeServiceImpl;

@ExtendWith(MockitoExtension.class)
class CollegeServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Faculty Of Dental Surgery";
  private static final String DEFAULT_LABEL_2 = "Faculty of Intensive Care Medicine";

  private CollegeServiceImpl service;

  @Mock
  private CollegeRepository repository;

  private College college1;
  private College college2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new CollegeServiceImpl(repository, Mappers.getMapper(CollegeMapper.class));

    college1 = new College();
    college1.setId(DEFAULT_ID_1);
    college1.setTisId(DEFAULT_TIS_ID_1);
    college1.setLabel(DEFAULT_LABEL_1);

    college2 = new College();
    college2.setId(DEFAULT_ID_2);
    college2.setTisId(DEFAULT_TIS_ID_2);
    college2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllCollegeShouldReturnAllColleges() {
    List<College> colleges = new ArrayList<>();
    colleges.add(college1);
    colleges.add(college2);
    when(repository.findAll(Sort.by("label"))).thenReturn(colleges);
    List<College> allColleges = service.getCollege();
    MatcherAssert.assertThat("The size of returned college list do not match the expected value",
        allColleges.size(), CoreMatchers.equalTo(colleges.size()));
    MatcherAssert.assertThat("The returned college list doesn't not contain the expected college",
        allColleges, CoreMatchers.hasItem(college1));
  }

  @Test
  void createCollegeShouldCreateCollegeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(College.class))).thenAnswer(returnsFirstArg());

    College college = service.createCollege(college2);

    assertThat("Unexpected id.", college.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", college.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", college.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void createCollegeShouldUpdateCollegeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(college1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    College college = service.createCollege(college2);

    assertThat("Unexpected id.", college.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", college.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", college.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateCollegeShouldCreateCollegeWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(College.class))).thenAnswer(returnsFirstArg());

    College college = service.updateCollege(college2);

    assertThat("Unexpected id.", college.getId(), is(DEFAULT_ID_2));
    assertThat("Unexpected TIS id.", college.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected label.", college.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void updateCollegeShouldUpdateCollegeWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(college1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());

    College college = service.updateCollege(college2);

    assertThat("Unexpected id.", college.getId(), is(DEFAULT_ID_1));
    assertThat("Unexpected TIS id.", college.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected label.", college.getLabel(), is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldDeleteCollegesByTisId() {
    service.deleteCollege(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
