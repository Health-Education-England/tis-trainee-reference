/*
 * The MIT License (MIT)
 *
 * Copyright 2026 Crown Copyright (Health Education England)
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.mapper.TraineeTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.TraineeType;
import uk.nhs.hee.tis.trainee.reference.repository.TraineeTypeRepository;

/**
 * Service for trainee types.
 */
@Service
public class TraineeTypeService
    extends AbstractReferenceService<TraineeType, TraineeType> {
  private final TraineeTypeMapper mapper;
  private final LocalOfficeContactService localOfficeContactService;

  protected TraineeTypeService(TraineeTypeRepository repository,
      TraineeTypeMapper mapper, LocalOfficeContactService localOfficeContactService,
      ObjectMapper objectMapper) {
    super(repository, objectMapper);
    this.mapper = mapper;
    this.localOfficeContactService = localOfficeContactService;
  }

  /**
   * Enrich any related local office contacts after saving the trainee type.
   *
   * @param savedEntity The saved trainee type.
   */
  @Override
  protected void afterSave(TraineeType savedEntity) {
    localOfficeContactService.updateAllForTraineeType(savedEntity);
  }

  @Override
  protected String getTisId(TraineeType entity) {
    return entity.getTisId();
  }

  @Override
  protected void copyAttributes(TraineeType target, TraineeType source) {
    mapper.update(target, source);
  }
}
