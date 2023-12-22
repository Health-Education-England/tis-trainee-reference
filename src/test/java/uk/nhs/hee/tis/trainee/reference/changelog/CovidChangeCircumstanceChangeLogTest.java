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

package uk.nhs.hee.tis.trainee.reference.changelog;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.nhs.hee.tis.trainee.reference.model.CovidChangeCircumstance;

@ExtendWith(MockitoExtension.class)
class CovidChangeCircumstanceChangeLogTest {

  private CovidChangeCircumstanceChangeLog changeLog;

  @Mock
  private MongoTemplate template;

  @BeforeEach
  void setUp() {
    changeLog = new CovidChangeCircumstanceChangeLog();
  }

  @Test
  void shouldAddInitialDeclarationTypes() {
    changeLog.insertInitialCovidChangeCircumstances(template);

    ArgumentCaptor<Collection> collectionCaptor = ArgumentCaptor.forClass(Collection.class);
    verify(template).insertAll(collectionCaptor.capture());

    Collection<?> covidChangeCircumstances = collectionCaptor.getValue();
    assertThat("Unexpected collection size.", covidChangeCircumstances.size(), is(5));
    Set<String> declarationTypeValues = covidChangeCircumstances.stream()
        .filter(dt -> dt instanceof CovidChangeCircumstance)
        .map(dt -> ((CovidChangeCircumstance) dt).getLabel())
        .collect(Collectors.toSet());

    assertThat("Unexpected number of declaration type values.", declarationTypeValues.size(),
        is(5));
    assertThat("Unexpected declaration type values.", declarationTypeValues,
        hasItems("Any Period of self-isolation",
            "Moving from front line services for those in high risk groups",
            "Redeployed to support Covid-19 services",
            "Limited opportunities to curricula requirements",
            "Other"));
  }
}
