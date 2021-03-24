/*
 * The MIT License (MIT)
 *
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
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeService;

@Slf4j
@RestController
@RequestMapping("/api")
public class LocalOfficeResource {

  private LocalOfficeService service;
  private LocalOfficeMapper mapper;

  public LocalOfficeResource(LocalOfficeService service, LocalOfficeMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get LocalOffice options from reference table.
   *
   * @return list of LocalOffices.
   */
  @GetMapping("/local-office")
  public List<LocalOfficeDto> getLocalOffice() {
    log.trace("Get all LocalOffices");
    List<LocalOffice> localOffices = service.get();
    return mapper.toDtos(localOffices);
  }

  /**
   * Create a LocalOffice, or update an existing LocalOffice if the tisID matches.
   *
   * @param localOfficeDto The LocalOffice to create.
   * @return The created (or updated) LocalOffice.
   */
  @PostMapping("/local-office")
  public ResponseEntity<LocalOfficeDto> createLocalOffice(
      @RequestBody LocalOfficeDto localOfficeDto) {
    LocalOffice localOffice = mapper.toEntity(localOfficeDto);
    localOffice = service.create(localOffice);
    return ResponseEntity.created(URI.create("/api/localOffice")).body(mapper.toDto(localOffice));
  }

  /**
   * Update a LocalOffice with a matching tisId value, or creates a new LocalOffice if the ID was
   * not found.
   *
   * @param localOfficeDto The LocalOffice details to update.
   * @return The updated (or created) LocalOffice.
   */
  @PutMapping("/local-office")
  public ResponseEntity<LocalOfficeDto> updateLocalOffice(
      @RequestBody LocalOfficeDto localOfficeDto) {
    LocalOffice localOffice = mapper.toEntity(localOfficeDto);
    localOffice = service.update(localOffice);
    return ResponseEntity.ok(mapper.toDto(localOffice));
  }

  /**
   * Delete the LocalOffice with the given tisId.
   *
   * @param tisId The tisId of the LocalOffice to delete.
   */
  @DeleteMapping("/local-office/{tisId}")
  public ResponseEntity<Void> deleteLocalOffice(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
