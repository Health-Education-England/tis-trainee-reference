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

package uk.nhs.hee.tis.trainee.reference.facade;

import java.util.List;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactRepository;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactTypeRepository;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeRepository;

public class LocalOfficeContactEnricherFacade {

  private LocalOfficeRepository localOfficeRepository;
  private LocalOfficeContactTypeRepository contactTypeRepository;
  private LocalOfficeContactRepository contactRepository;

  LocalOfficeContactEnricherFacade(LocalOfficeRepository localOfficeRepository,
      LocalOfficeContactTypeRepository contactTypeRepository,
      LocalOfficeContactRepository contactRepository) {
    this.localOfficeRepository = localOfficeRepository;
    this.contactTypeRepository = contactTypeRepository;
    this.contactRepository = contactRepository;
  }

  /**
   * Enrich a local office contact with data from the local office and the contact type. If local
   * office or contact type do not exist (which should be at most a temporary situation) they are
   * ignored.
   *
   * @param localOfficeContact The local office contact to enrich.
   * @return The enriched local office contact.
   */
  public LocalOfficeContact enrich(LocalOfficeContact localOfficeContact) {
    if (localOfficeContact.getLocalOfficeId() != null) {
      LocalOffice localOffice = localOfficeRepository
          .findByTisId(localOfficeContact.getLocalOfficeId());
      if (localOffice != null) {
        localOfficeContact.setLocalOfficeName(localOffice.getLabel());
      }
    }
    if (localOfficeContact.getContactTypeId() != null) {
      LocalOfficeContactType contactType = contactTypeRepository
          .findByTisId(localOfficeContact.getContactTypeId());
      if (contactType != null) {
        localOfficeContact.setContactTypeName(contactType.getLabel());
      }
    }
    return localOfficeContact;
  }

  /**
   * Enrich all local office contacts with the name of an updated local office.
   *
   * @param localOffice The updated local office.
   */
  public void enrich(LocalOffice localOffice) {
    if (localOffice.getId() != null) {
      List<LocalOfficeContact> localOfficeContactList = contactRepository
          .findByLocalOfficeId(localOffice.getId());
      localOfficeContactList.forEach(c -> {
        c.setLocalOfficeName(localOffice.getLabel());
        contactRepository.save(c);
      });
    }
  }

  /**
   * Enrich all local office contacts with the name of an updated contact type.
   *
   * @param contactType The updated contact type.
   */
  public void enrich(LocalOfficeContactType contactType) {
    if (contactType.getTisId() != null) {
      List<LocalOfficeContact> localOfficeContactList = contactRepository
          .findByContactTypeId(contactType.getTisId());
      localOfficeContactList.forEach(c -> {
        c.setContactTypeName(contactType.getLabel());
        contactRepository.save(c);
      });
    }
  }
}
