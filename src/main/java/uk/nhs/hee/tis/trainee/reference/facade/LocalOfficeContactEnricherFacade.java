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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactRepository;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactTypeRepository;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeRepository;

@Slf4j
@Component
public class LocalOfficeContactEnricherFacade {

  private LocalOfficeRepository localOfficeRepository;
  private LocalOfficeContactTypeRepository contactTypeRepository;


  LocalOfficeContactEnricherFacade(LocalOfficeRepository localOfficeRepository,
      LocalOfficeContactTypeRepository contactTypeRepository,
      LocalOfficeContactRepository contactRepository) {
    this.localOfficeRepository = localOfficeRepository;
    this.contactTypeRepository = contactTypeRepository;
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
    LocalOffice localOffice;
    LocalOfficeContactType contactType;
    if (localOfficeContact.getLocalOfficeId() != null) {
      localOffice = localOfficeRepository.findByTisId(localOfficeContact.getLocalOfficeId());
      if (localOffice != null) {
        localOfficeContact.setLocalOfficeName(localOffice.getLabel());
      }
    }
    if (localOfficeContact.getContactTypeId() != null) {
      contactType = contactTypeRepository.findByTisId(localOfficeContact.getContactTypeId());
      if (contactType != null) {
        localOfficeContact.setContactTypeName(contactType.getLabel());
      }
    }
    localOfficeContact.setLabel(generateLabel(localOfficeContact));
    return localOfficeContact;
  }

  /**
   * Enrich a local office contact with its local office name.
   *
   * @param localOfficeContact The local office contact to enrich.
   * @return The enriched local office contact.
   */
  public LocalOfficeContact enrichWithLocalOffice(LocalOfficeContact localOfficeContact,
      String localOfficeName) {
    localOfficeContact.setLocalOfficeName(localOfficeName);
    localOfficeContact.setLabel(generateLabel(localOfficeContact));
    return localOfficeContact;
  }

  /**
   * Enrich a local office contact with its contact type.
   *
   * @param localOfficeContact The local office contact to enrich.
   * @return The enriched local office contact.
   */
  public LocalOfficeContact enrichWithContactType(LocalOfficeContact localOfficeContact,
      String contactTypeName) {
    localOfficeContact.setContactTypeName(contactTypeName);
    localOfficeContact.setLabel(generateLabel(localOfficeContact));
    return localOfficeContact;
  }

  /**
   * Enrich a local office contact with its local office name and contact type name.
   *
   * @param localOfficeContact The local office contact to enrich.
   * @return The enriched local office contact.
   */
  public LocalOfficeContact enrichWithLocalOfficeAndContactType(
      LocalOfficeContact localOfficeContact, String localOfficeName, String contactTypeName) {
    localOfficeContact.setLocalOfficeName(localOfficeName);
    localOfficeContact.setContactTypeName(contactTypeName);
    localOfficeContact.setLabel(generateLabel(localOfficeContact));
    return localOfficeContact;
  }

  /**
   * Generates a local office contact label string.
   *
   * @param localOfficeContact The local office contact.
   * @return The label string.
   */
  private String generateLabel(LocalOfficeContact localOfficeContact) {
    String label = localOfficeContact.getContact();
    if (localOfficeContact.getContactTypeName() != null) {
      label = label + " (" + localOfficeContact.getContactTypeName() + ")";
    }
    if (localOfficeContact.getLocalOfficeName() != null) {
      label = label + " - " + localOfficeContact.getLocalOfficeName();
    }
    return label;
  }
}
