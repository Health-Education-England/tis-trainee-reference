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
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeContactService;

/**
 * Resource for local office contacts.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@XRayEnabled
public class LocalOfficeContactResource {

  private final LocalOfficeContactService service;
  private final LocalOfficeContactMapper mapper;

  public LocalOfficeContactResource(LocalOfficeContactService service,
      LocalOfficeContactMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get LocalOfficeContacts from reference table for the provided local office UUID.
   *
   * @return list of LocalOfficeContacts.
   */
  @GetMapping("/local-office-contact-by-lo-uuid/{localOfficeUuid}")
  public List<LocalOfficeContactDetailsDto> getLocalOfficeContactsByLoUuid(
      @PathVariable String localOfficeUuid) {
    log.trace("Get all LocalOfficeContacts for Local office UUID '{}'", localOfficeUuid);
    List<LocalOfficeContact> localOfficeContacts = service.getByLocalOfficeUuid(localOfficeUuid);
    return mapper.toDetailsDtos(localOfficeContacts);
  }

  /**
   * Get LocalOfficeContacts from reference table for the provided local office name.
   *
   * @return list of LocalOfficeContacts.
   */
  @GetMapping("/local-office-contact-by-lo-name/{localOfficeName}")
  public List<LocalOfficeContactDetailsDto> getLocalOfficeContactsByLoName(
      @PathVariable String localOfficeName) {
    log.trace("Get all LocalOfficeContacts for Local office '{}'", localOfficeName);
    List<LocalOfficeContact> localOfficeContacts = service.getByLocalOfficeName(localOfficeName);
    return mapper.toDetailsDtos(localOfficeContacts);
  }

  /**
   * Get all LocalOfficeContacts from reference table.
   *
   * @return list of LocalOfficeContacts.
   */
  @GetMapping("/local-office-contact")
  public List<LocalOfficeContactDetailsDto> getLocalOfficeContacts() {
    log.trace("Get all LocalOfficeContacts");
    List<LocalOfficeContact> localOfficeContacts = service.get();
    return mapper.toDetailsDtos(localOfficeContacts);
  }

  /**
   * Create a LocalOfficeContact, or update an existing LocalOfficeContact if the tisID matches.
   *
   * @param localOfficeContactDto The LocalOfficeContact to create.
   * @return The created (or updated) LocalOfficeContact.
   */
  @PostMapping("/local-office-contact")
  public ResponseEntity<LocalOfficeContactDetailsDto> createLocalOfficeContact(
      @RequestBody LocalOfficeContactDto localOfficeContactDto) {
    LocalOfficeContact localOfficeContact = mapper.toEntity(localOfficeContactDto);
    localOfficeContact = service.create(localOfficeContact);
    return ResponseEntity.created(URI.create("/api/local-office-contact"))
        .body(mapper.toDetailsDto(localOfficeContact));
  }

  /**
   * Update a LocalOfficeContact with a matching tisId value, or creates a new LocalOfficeContact if
   * the ID was not found.
   *
   * @param localOfficeContactDto The LocalOfficeContact details to update.
   * @return The updated (or created) LocalOfficeContact.
   */
  @PutMapping("/local-office-contact")
  public ResponseEntity<LocalOfficeContactDetailsDto> updateLocalOfficeContact(
      @RequestBody LocalOfficeContactDto localOfficeContactDto) {
    LocalOfficeContact localOfficeContact = mapper.toEntity(localOfficeContactDto);
    localOfficeContact = service.update(localOfficeContact);
    return ResponseEntity.ok(mapper.toDetailsDto(localOfficeContact));
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
