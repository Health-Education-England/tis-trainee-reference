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
import uk.nhs.hee.tis.trainee.reference.dto.DeclarationTypeDto;
import uk.nhs.hee.tis.trainee.reference.model.DeclarationType;
import uk.nhs.hee.tis.trainee.reference.repository.DeclarationTypeRepository;
import uk.nhs.hee.tis.trainee.reference.service.impl.DeclarationTypeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class DeclarationTypeServiceImplTest {

  private static final String DEFAULT_ID_1 = "DEFAULT_ID_1";
  private static final String DEFAULT_ID_2 = "DEFAULT_ID_2";

  private static final String DEFAULT_LABEL_1 = "Significant event";
  private static final String DEFAULT_LABEL_2 = "Other investigation";

  @InjectMocks
  private DeclarationTypeServiceImpl declarationTypeServiceImpl;

  @Mock
  private DeclarationTypeRepository declarationTypeRepositoryMock;

  private DeclarationType declarationType1;
  private DeclarationType declarationType2;
  private DeclarationTypeDto declarationTypeDto1;
  private DeclarationTypeDto declarationTypeDto2;

  /**
   * Set up data.
   */
  @BeforeEach
  public void initData() {
    declarationType1 = new DeclarationType();
    declarationType1.setId(DEFAULT_ID_1);
    declarationType1.setLabel(DEFAULT_LABEL_1);

    declarationType2 = new DeclarationType();
    declarationType2.setId(DEFAULT_ID_2);
    declarationType2.setLabel(DEFAULT_LABEL_2);

    declarationTypeDto1 = new DeclarationTypeDto();
    declarationTypeDto1.setId(DEFAULT_ID_1);
    declarationTypeDto1.setLabel(DEFAULT_LABEL_1);

    declarationTypeDto2 = new DeclarationTypeDto();
    declarationTypeDto2.setId(DEFAULT_ID_2);
    declarationTypeDto2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  public void getAllDeclarationTypeShouldReturnAllDeclarationType() {
    List<DeclarationType> declarationType = new ArrayList<>();
    declarationType.add(declarationType1);
    declarationType.add(declarationType2);
    when(declarationTypeRepositoryMock.findAll()).thenReturn(declarationType);
    List<DeclarationType> allDeclarationType =
        declarationTypeServiceImpl.getDeclarationType();
    MatcherAssert.assertThat(
        "The size of returned declaration type list do not match the expected value",
        allDeclarationType.size(), CoreMatchers.equalTo(declarationType.size()));
    MatcherAssert.assertThat(
        "The returned declaration type list doesn't not contain the expected gender",
        allDeclarationType, CoreMatchers.hasItem(declarationType1));
  }
}
