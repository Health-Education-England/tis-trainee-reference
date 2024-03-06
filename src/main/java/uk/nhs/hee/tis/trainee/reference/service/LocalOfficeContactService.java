/*
 * The MIT License (MIT)
 *
 * Copyright 2024 Crown Copyright (Health Education England)
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
import java.util.List;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactRepository;

/**
 * Service for local office contacts.
 */
@Service
@XRayEnabled
public class LocalOfficeContactService extends AbstractReferenceService<LocalOfficeContact> {

  private LocalOfficeContactMapper mapper;
  private LocalOfficeContactRepository repository;

  protected LocalOfficeContactService(LocalOfficeContactRepository repository,
      LocalOfficeContactMapper mapper) {
    super(repository);
    this.mapper = mapper;
    this.repository = repository;
  }

  public List<LocalOfficeContact> getByLocalOfficeUuid(String localOfficeId) {
    return repository.findByLocalOfficeId(localOfficeId);
  }

  @Override
  protected String getTisId(LocalOfficeContact entity) {
    return entity.getTisId();
  }

  @Override
  protected void copyAttributes(LocalOfficeContact target, LocalOfficeContact source) {
    mapper.update(target, source);
  }
}
