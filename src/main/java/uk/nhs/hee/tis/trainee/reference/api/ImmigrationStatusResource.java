/*
 * The MIT License (MIT)
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

package uk.nhs.hee.tis.trainee.reference.api;

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
import uk.nhs.hee.tis.trainee.reference.dto.ImmigrationStatusDto;
import uk.nhs.hee.tis.trainee.reference.mapper.ImmigrationStatusMapper;
import uk.nhs.hee.tis.trainee.reference.model.ImmigrationStatus;
import uk.nhs.hee.tis.trainee.reference.service.ImmigrationStatusService;

@Slf4j
@RestController
@RequestMapping("/api")
public class ImmigrationStatusResource {

  private ImmigrationStatusService service;
  private ImmigrationStatusMapper mapper;

  public ImmigrationStatusResource(ImmigrationStatusService service,
      ImmigrationStatusMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get ImmigrationStatus options from reference table.
   *
   * @return list of ImmigrationStatus.
   */
  @GetMapping("/immigration-status")
  public List<ImmigrationStatusDto> getImmigrationStatus() {
    log.trace("Get all ImmigrationStatus");
    List<ImmigrationStatus> immigrationStatus = service.get();
    return mapper.toDtos(immigrationStatus);
  }

  /**
   * Create an ImmigrationStatus, or update an existing ImmigrationStatus if the tisID matches.
   *
   * @param immigrationStatusDto The ImmigrationStatus to create.
   * @return The created (or updated) ImmigrationStatus.
   */
  @PostMapping("/immigration-status")
  public ResponseEntity<ImmigrationStatusDto> createImmigrationStatus(
      @RequestBody ImmigrationStatusDto immigrationStatusDto) {
    ImmigrationStatus immigrationStatus = mapper.toEntity(immigrationStatusDto);
    immigrationStatus = service.create(immigrationStatus);
    return ResponseEntity.created(URI.create("/api/immigrationStatus"))
        .body(mapper.toDto(immigrationStatus));
  }

  /**
   * Update an ImmigrationStatus with a matching tisId value, or creates a new ImmigrationStatus if
   * the ID was not found.
   *
   * @param immigrationStatusDto The ImmigrationStatus details to update.
   * @return The updated (or created) ImmigrationStatus.
   */
  @PutMapping("/immigration-status")
  public ResponseEntity<ImmigrationStatusDto> updateImmigrationStatus(
      @RequestBody ImmigrationStatusDto immigrationStatusDto) {
    ImmigrationStatus immigrationStatus = mapper.toEntity(immigrationStatusDto);
    immigrationStatus = service.update(immigrationStatus);
    return ResponseEntity.ok(mapper.toDto(immigrationStatus));
  }

  /**
   * Delete the ImmigrationStatus with the given tisId.
   *
   * @param tisId The tisId of the ImmigrationStatus to delete.
   */
  @DeleteMapping("/immigration-status/{tisId}")
  public ResponseEntity<Void> deleteImmigrationStatus(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
