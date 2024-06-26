/*
 * The MIT License (MIT)
 *
 * Copyright 2023 Crown Copyright (Health Education England)
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.mapper.ProgrammeMembershipTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.ProgrammeMembershipType;
import uk.nhs.hee.tis.trainee.reference.repository.ProgrammeMembershipTypeRepository;

@Service
@XRayEnabled
public class ProgrammeMembershipTypeService
    extends AbstractReferenceService<ProgrammeMembershipType> {

  private final ProgrammeMembershipTypeMapper mapper;
  private final List<String> excludedTypes;

  protected ProgrammeMembershipTypeService(ProgrammeMembershipTypeRepository repository,
      ProgrammeMembershipTypeMapper mapper,
      @Value("${application.exclude-filters.pm-type}") List<String> excludedTypes) {
    super(repository);
    this.mapper = mapper;
    this.excludedTypes = excludedTypes;
  }

  @Override
  public List<ProgrammeMembershipType> get() {
    return super.get().stream()
        .filter(type -> !excludedTypes.contains(type.getLabel()))
        .toList();
  }

  @Override
  protected String getTisId(ProgrammeMembershipType entity) {
    return entity.getTisId();
  }

  @Override
  protected void copyAttributes(ProgrammeMembershipType target, ProgrammeMembershipType source) {
    mapper.update(target, source);
  }
}
