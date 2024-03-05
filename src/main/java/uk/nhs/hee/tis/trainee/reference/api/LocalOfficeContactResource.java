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

package uk.nhs.hee.tis.trainee.reference.api;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeContactDetailsDto;
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeContactDto;
import uk.nhs.hee.tis.trainee.reference.facade.LocalOfficeContactEnricherFacade;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeContactService;

@Slf4j
@RestController
@RequestMapping("/api")
@XRayEnabled
public class LocalOfficeContactResource {

  private LocalOfficeContactService service;
  private LocalOfficeContactMapper mapper;
  private LocalOfficeContactEnricherFacade facade;

  public LocalOfficeContactResource(LocalOfficeContactService service,
      LocalOfficeContactMapper mapper, LocalOfficeContactEnricherFacade facade) {
    this.service = service;
    this.mapper = mapper;
    this.facade = facade;
  }

  /**
   * Get LocalOfficeContacts from reference table for the provided local office.
   *
   * @return list of LocalOfficeContacts.
   */
  @GetMapping("/local-office-contact-by-lo-name/{localOfficeName}")
  public List<LocalOfficeContactDetailsDto> getLocalOfficeContactByLoName(
      @PathVariable String localOfficeName) {
    log.trace("Get all LocalOfficeContacts for '{}'", localOfficeName);
    List<LocalOfficeContactDetailsDto> localOfficeContacts = service.getByLocalOfficeName(localOfficeName);
    return  localOfficeContacts; // mapper.toDtos(localOfficeContacts);
  }

  /**
   * Get LocalOfficeContacts from reference table for the provided local office UUID.
   *
   * @return list of LocalOfficeContacts.
   */
  @GetMapping("/local-office-contact-by-lo-id/{localOfficeId}")
  public List<LocalOfficeContactDto> getLocalOfficeContactByLoId(
      @PathVariable String localOfficeId) {
    log.trace("Get all LocalOfficeContacts for '{}'", localOfficeId);
    List<LocalOfficeContact> localOfficeContacts = service.getByLocalOfficeId(localOfficeId);
    return mapper.toDtos(localOfficeContacts);
  }

  /**
   * Get all LocalOfficeContacts from reference table.
   *
   * @return list of LocalOfficeContacts.
   */
  @GetMapping("/local-office-contacts")
  public List<LocalOfficeContactDto> getLocalOfficeContacts() {
    log.trace("Get all LocalOfficeContacts");
    List<LocalOfficeContact> localOfficeContacts = service.get();
    return mapper.toDtos(localOfficeContacts);
  }

  /**
   * Create a LocalOfficeContact, or update an existing LocalOfficeContact if the tisID matches.
   *
   * @param localOfficeContactDto The LocalOfficeContact to create.
   * @return The created (or updated) LocalOfficeContact.
   */
  @PostMapping("/local-office-contact")
  public ResponseEntity<LocalOfficeContactDto> createLocalOfficeContact(
      @RequestBody LocalOfficeContactDto localOfficeContactDto) {
    LocalOfficeContact localOfficeContact = mapper.toEntity(localOfficeContactDto);
    localOfficeContact = facade.enrich(localOfficeContact);
    localOfficeContact = service.create(localOfficeContact);
    return ResponseEntity.created(URI.create("/api/localOfficeContact"))
        .body(mapper.toDto(localOfficeContact));
  }

  /**
   * Update a LocalOfficeContact with a matching tisId value, or creates a new LocalOfficeContact
   * if the ID was not found.
   *
   * @param localOfficeContactDto The LocalOfficeContact details to update.
   * @return The updated (or created) LocalOfficeContact.
   */
  @PutMapping("/local-office-contact")
  public ResponseEntity<LocalOfficeContactDto> updateLocalOfficeContact(
      @RequestBody LocalOfficeContactDto localOfficeContactDto) {
    LocalOfficeContact localOfficeContact = mapper.toEntity(localOfficeContactDto);
    localOfficeContact = facade.enrich(localOfficeContact);
    localOfficeContact = service.update(localOfficeContact);
    return ResponseEntity.ok(mapper.toDto(localOfficeContact));
  }

  /**
   * Delete the LocalOfficeContact with the given tisId.
   *
   * @param tisId The tisId of the LocalOfficeContact to delete.
   */
  @DeleteMapping("/local-office-contact/{tisId}")
  public ResponseEntity<Void> deleteLocalOfficeContact(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
