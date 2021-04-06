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

package uk.nhs.hee.tis.trainee.reference.dto.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.nhs.hee.tis.trainee.reference.dto.GradeDto;
import uk.nhs.hee.tis.trainee.reference.dto.Status;

class GradeValidatorTest {

  private GradeValidator validator;

  @BeforeEach
  void setUp() {
    validator = new GradeValidator();
  }

  @ParameterizedTest(
      name = "Valid should be {3} when status {0}, placementGrade {1} and trainingGrade {2}."
  )
  @CsvSource({
      "INACTIVE,false,false,false",
      "INACTIVE,false,true,false",
      "INACTIVE,true,false,false",
      "INACTIVE,true,true,false",
      "CURRENT,false,false,false",
      "CURRENT,false,true,false",
      "CURRENT,true,false,false",
      "CURRENT,true,true,true"
  })
  void shouldValidateGrade(Status status, boolean placementGrade, boolean trainingGrade,
      boolean result) {
    GradeDto dto = new GradeDto();
    dto.setStatus(status);
    dto.setPlacementGrade(placementGrade);
    dto.setTrainingGrade(trainingGrade);

    boolean valid = validator.isValid(dto);
    assertThat("Unexpected validity.", valid, is(result));
  }
}
