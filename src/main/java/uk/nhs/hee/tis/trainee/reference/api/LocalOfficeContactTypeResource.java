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
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeContactTypeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeContactTypeService;

/**
 * Resource for local office contact types.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@XRayEnabled
public class LocalOfficeContactTypeResource {

  private LocalOfficeContactTypeService service;
  private LocalOfficeContactTypeMapper mapper;

  public LocalOfficeContactTypeResource(LocalOfficeContactTypeService service,
      LocalOfficeContactTypeMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get LocalOfficeContactTypes from reference table.
   *
   * @return list of LocalOfficeContactTypes.
   */
  @GetMapping("/local-office-contact-type")
  public List<LocalOfficeContactTypeDto> getLocalOfficeContactType() {
    log.trace("Get all LocalOfficeContactTypes");
    List<LocalOfficeContactType> localOfficeContactTypes = service.get();
    return mapper.toDtos(localOfficeContactTypes);
  }

  /**
   * Create a LocalOfficeContactType, or update an existing LocalOfficeContactType if the tisId
   * matches.
   *
   * @param localOfficeContactTypeDto The LocalOfficeContactType to create.
   * @return The created (or updated) LocalOfficeContactType.
   */
  @PostMapping("/local-office-contact-type")
  public ResponseEntity<LocalOfficeContactTypeDto> createLocalOfficeContactType(
      @RequestBody LocalOfficeContactTypeDto localOfficeContactTypeDto) {
    LocalOfficeContactType localOfficeContactType = mapper.toEntity(localOfficeContactTypeDto);
    localOfficeContactType = service.create(localOfficeContactType);
    return ResponseEntity.created(URI.create("/api/localOfficeContactType"))
        .body(mapper.toDto(localOfficeContactType));
  }

  /**
   * Update a LocalOfficeContactType with a matching tisId value, or creates a new
   * LocalOfficeContactType if the ID was not found.
   *
   * @param localOfficeContactTypeDto The LocalOfficeContactType details to update.
   * @return The updated (or created) LocalOfficeContactType.
   */
  @PutMapping("/local-office-contact-type")
  public ResponseEntity<LocalOfficeContactTypeDto> updateLocalOfficeContactType(
      @RequestBody LocalOfficeContactTypeDto localOfficeContactTypeDto) {
    LocalOfficeContactType localOfficeContactType = mapper.toEntity(localOfficeContactTypeDto);
    localOfficeContactType = service.update(localOfficeContactType);
    return ResponseEntity.ok(mapper.toDto(localOfficeContactType));
  }

  /**
   * Delete the LocalOfficeContactType with the given tisId.
   *
   * @param tisId The tisId of the LocalOfficeContactType to delete.
   */
  @DeleteMapping("/local-office-contact-type/{tisId}")
  public ResponseEntity<Void> deleteLocalOfficeContactType(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
