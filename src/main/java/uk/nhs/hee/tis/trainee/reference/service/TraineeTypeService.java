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

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.mapper.TraineeTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.TraineeType;
import uk.nhs.hee.tis.trainee.reference.repository.TraineeTypeRepository;

/**
 * Service for trainee types.
 */
@Service
@XRayEnabled
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
   * Override the default create to enrich any related local office contacts after saving it.
   *
   * @param entity The entity to create.
   * @return The saved entity.
   */
  @Override
  public TraineeType create(TraineeType entity) {
    entity = super.create(entity);
    localOfficeContactService.updateAllForTraineeType(entity);
    return entity;
  }

  /**
   * Override the default update to enrich any related local office contacts after saving it.
   *
   * @param entity The entity to update.
   * @return The saved entity.
   */
  @Override
  public TraineeType update(TraineeType entity) {
    entity = super.update(entity);
    localOfficeContactService.updateAllForTraineeType(entity);
    return entity;
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
