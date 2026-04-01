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

import static uk.nhs.hee.tis.trainee.reference.dto.TraineeType.FOUNDATION;
import static uk.nhs.hee.tis.trainee.reference.dto.TraineeType.SPECIALTY;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.dto.TraineeType;
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

  private final LocalOfficeContactMapper mapper;
  private final LocalOfficeContactRepository repository;
  private final LocalOfficeContactEnricherFacade facade;

  protected LocalOfficeContactService(LocalOfficeContactRepository repository,
      LocalOfficeContactMapper mapper, LocalOfficeContactEnricherFacade facade) {
    super(repository);
    this.mapper = mapper;
    this.repository = repository;
    this.facade = facade;
  }

  @Override
  public List<LocalOfficeContact> get() {
    return get(SPECIALTY);
  }

  /**
   * Get all local office contacts, filtered by trainee type.
   *
   * @param traineeType The trainee type to filter by.
   * @return The list of local office contacts matching the trainee type.
   */
  public List<LocalOfficeContact> get(TraineeType traineeType) {
    return filterByTraineeType(super.get(), traineeType);
  }

  /**
   * Get local office contacts by local office ID, filtered by trainee type.
   *
   * @param localOfficeId The local office ID to filter by.
   * @param traineeType   The trainee type to filter by.
   * @return The list of local office contacts matching the local office ID and trainee type.
   */
  public List<LocalOfficeContact> getByLocalOfficeUuid(String localOfficeId,
      TraineeType traineeType) {
    return filterByTraineeType(repository.findByLocalOfficeId(localOfficeId), traineeType);
  }

  /**
   * Get local office contacts by local office name, filtered by trainee type.
   *
   * @param localOfficeName The local office name to filter by.
   * @param traineeType     The trainee type to filter by.
   * @return The list of local office contacts matching the local office name and trainee type.
   */
  public List<LocalOfficeContact> getByLocalOfficeName(String localOfficeName,
      TraineeType traineeType) {
    return filterByTraineeType(repository.findByLocalOfficeName(localOfficeName), traineeType);
  }

  /**
   * Filter a list of local office contacts by trainee type. Foundation contacts are identified by a
   * " - Foundation" suffix in their contact type name, while specialty contacts have no suffix.
   *
   * @param contacts    The list of local office contacts to filter.
   * @param traineeType The trainee type to filter by.
   * @return The list of local office contacts matching the trainee type.
   */
  private List<LocalOfficeContact> filterByTraineeType(List<LocalOfficeContact> contacts,
      TraineeType traineeType) {
    return contacts.stream()
        .filter(c -> {
          String contactTypeName = c.getContactTypeName();

          if (traineeType == FOUNDATION) {
            // Foundation contact types are suffixed with " - Foundation".
            return contactTypeName.endsWith(" - Foundation");
          } else {
            // Assume specialty, which has no suffix.
            return !contactTypeName.endsWith(" - Foundation");
          }
        })
        .toList();
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
    log.info("Creating local office contact '{}'", entity);
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
    log.info("Updating local office contact '{}'", entity);
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
