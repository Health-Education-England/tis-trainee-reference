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

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeRepository;

@Service
@XRayEnabled
public class LocalOfficeService extends AbstractReferenceService<LocalOffice> {

  private LocalOfficeMapper mapper;
  private LocalOfficeContactService localOfficeContactService;

  protected LocalOfficeService(LocalOfficeRepository repository, LocalOfficeMapper mapper,
      LocalOfficeContactService localOfficeContactService) {
    super(repository);
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
  public LocalOffice create(LocalOffice entity) {
    entity = super.create(entity);
    localOfficeContactService.updateAllForLocalOffice(entity);
    return entity;
  }

  /**
   * Override the default update to enrich any related local office contacts after saving it.
   *
   * @param entity The entity to update.
   * @return The saved entity.
   */
  @Override
  public LocalOffice update(LocalOffice entity) {
    entity = super.update(entity);
    localOfficeContactService.updateAllForLocalOffice(entity);
    return entity;
  }

  @Override
  protected String getTisId(LocalOffice entity) {
    return entity.getTisId();
  }

  @Override
  protected void copyAttributes(LocalOffice target, LocalOffice source) {
    mapper.update(target, source);
  }
}
