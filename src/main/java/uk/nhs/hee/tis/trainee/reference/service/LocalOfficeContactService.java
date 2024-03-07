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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.facade.LocalOfficeContactEnricherFacade;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactRepository;

/**
 * Service for local office contacts.
 */
@Service
@XRayEnabled
@Slf4j
public class LocalOfficeContactService extends AbstractReferenceService<LocalOfficeContact> {

  private LocalOfficeContactMapper mapper;
  private LocalOfficeContactRepository repository;
  private LocalOfficeContactEnricherFacade facade;

  protected LocalOfficeContactService(LocalOfficeContactRepository repository,
      LocalOfficeContactMapper mapper, LocalOfficeContactEnricherFacade facade) {
    super(repository);
    this.mapper = mapper;
    this.repository = repository;
    this.facade = facade;
  }

  public List<LocalOfficeContact> getByLocalOfficeUuid(String localOfficeId) {
    return repository.findByLocalOfficeId(localOfficeId);
  }

  public List<LocalOfficeContact> getByLocalOfficeName(String localOfficeName) {
    return repository.findByLocalOfficeName(localOfficeName);
  }

  /**
   * Override the default create to enrich the entity before saving it.
   *
   * @param entity The entity to create.
   * @return The saved entity.
   */
  @Override
  public LocalOfficeContact create(LocalOfficeContact entity) {
    entity = facade.enrich(entity);
    return super.create(entity);
  }

  /**
   * Override the default update to enrich the entity before saving it.
   *
   * @param entity The entity to update.
   * @return The saved entity.
   */
  @Override
  public LocalOfficeContact update(LocalOfficeContact entity) {
    entity = facade.enrich(entity);
    return super.update(entity);
  }

  /**
   * Update all local office contacts for a given local office.
   *
   * @param localOffice The local office that triggered the update.
   */
  public void updateAllForLocalOffice(LocalOffice localOffice) {
    if (localOffice.getUuid() != null) {
      List<LocalOfficeContact> localOfficeContactList = repository
          .findByLocalOfficeId(localOffice.getUuid());
      localOfficeContactList.forEach(c -> {
        log.info("Updating local office contact '{}' with local office '{}'", c.getTisId(),
            localOffice.getLabel());
        LocalOfficeContact enriched = facade.enrichWithLocalOffice(c, localOffice.getLabel());
        super.update(enriched);
      });
    }
  }

  /**
   * Update all local office contacts for a given contact type.
   *
   * @param contactType The contact type that triggered the update.
   */
  public void updateAllForContactType(LocalOfficeContactType contactType) {
    if (contactType.getTisId() != null) {
      List<LocalOfficeContact> localOfficeContactList = repository
          .findByContactTypeId(contactType.getTisId());
      localOfficeContactList.forEach(c -> {
        log.info("Updating local office contact '{}' with contact type '{}'", c.getTisId(),
            contactType.getLabel());
        LocalOfficeContact enriched = facade.enrichWithContactType(c, contactType.getLabel());
        super.update(enriched);
      });
    }
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
