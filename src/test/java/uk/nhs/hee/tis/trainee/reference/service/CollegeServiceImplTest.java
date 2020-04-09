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
import uk.nhs.hee.tis.trainee.reference.dto.CollegeDto;
import uk.nhs.hee.tis.trainee.reference.model.College;
import uk.nhs.hee.tis.trainee.reference.repository.CollegeRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.CollegeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CollegeServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LABEL_1 = "Faculty Of Dental Surgery";
  private static final String DEFAULT_LABEL_2 = "Faculty of Intensive Care Medicine";

  @InjectMocks
  private CollegeServiceImpl collegeServiceImpl;

  @Mock
  private CollegeRepository collegeRepositoryMock;

  private College college1, college2;
  private CollegeDto collegeDto1, collegeDto2;

  @BeforeEach
  public void initData() {
    college1 = new College();
    college1.setId(DEFAULT_ID_1);
    college1.setCollegeTisId(DEFAULT_TIS_ID_1);
    college1.setLabel(DEFAULT_LABEL_1);

    college2 = new College();
    college2.setId(DEFAULT_ID_2);
    college2.setCollegeTisId(DEFAULT_TIS_ID_2);
    college2.setLabel(DEFAULT_LABEL_2);

    collegeDto1 = new CollegeDto();
    collegeDto1.setId(DEFAULT_ID_1);
    collegeDto1.setCollegeTisId(DEFAULT_TIS_ID_1);
    collegeDto1.setLabel(DEFAULT_LABEL_1);

    collegeDto2 = new CollegeDto();
    collegeDto2.setId(DEFAULT_ID_2);
    collegeDto2.setCollegeTisId(DEFAULT_TIS_ID_2);
    collegeDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  public void getAllCollegeShouldReturnAllColleges() {
    List<College> colleges = new ArrayList<>();
    colleges.add(college1);
    colleges.add(college2);
    when(collegeRepositoryMock.findAll()).thenReturn(colleges);
    List<College> allColleges = collegeServiceImpl.getCollege();
    MatcherAssert.assertThat("The size of returned college list do not match the expected value",
        allColleges.size(), CoreMatchers.equalTo(colleges.size()));
    MatcherAssert.assertThat("The returned college list doesn't not contain the expected college",
        allColleges, CoreMatchers.hasItem(college1));
  }
}
