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

package uk.nhs.hee.tis.trainee.reference.api;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.trainee.reference.dto.ProgrammeMembershipTypeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.ProgrammeMembershipTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.ProgrammeMembershipType;
import uk.nhs.hee.tis.trainee.reference.service.ProgrammeMembershipTypeService;

@RestController
@RequestMapping("/api")
@XRayEnabled
public class ProgrammeMembershipTypeResource {

  private static final Logger log = LoggerFactory.getLogger(ProgrammeMembershipTypeResource.class);

  private final ProgrammeMembershipTypeService service;
  private final ProgrammeMembershipTypeMapper mapper;

  ProgrammeMembershipTypeResource(ProgrammeMembershipTypeService service,
      ProgrammeMembershipTypeMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get ProgrammeMembershipType options from reference table.
   *
   * @return list of ProgrammeMembershipType.
   */
  @GetMapping("/programme-membership-type")
  public ResponseEntity<List<ProgrammeMembershipTypeDto>> getProgrammeMembershipTypes() {
    log.trace("Get all ProgrammeMembershipTypes");
    List<ProgrammeMembershipType> programmeMembershipTypes = service.get();
    return ResponseEntity.ok(mapper.toDtos(programmeMembershipTypes));
  }

  /**
   * Create a ProgrammeMembershipType, or update an existing ProgrammeMembershipType if the
   * tisID matches.
   *
   * @param programmeMembershipTypeDto The ProgrammeMembershipType to create.
   * @return The created (or updated) ProgrammeMembershipType.
   */
  @PostMapping("/programme-membership-type")
  public ResponseEntity<ProgrammeMembershipTypeDto> createProgrammeMembershipType(
      @RequestBody ProgrammeMembershipTypeDto programmeMembershipTypeDto) {

    ProgrammeMembershipType programmeMembershipType = mapper.toEntity(programmeMembershipTypeDto);
    programmeMembershipType = service.create(programmeMembershipType);
    return ResponseEntity.created(URI.create("/api/programme-membership-type"))
        .body(mapper.toDto(programmeMembershipType));
  }

  /**
   * Update a ProgrammeMembershipType with a matching tisId value, or creates a new
   * ProgrammeMembershipType if the ID was not found.
   *
   * @param programmeMembershipTypeDto The ProgrammeMembershipType details to update.
   * @return The updated (or created) ProgrammeMembershipType.
   */
  @PutMapping("/programme-membership-type")
  public ResponseEntity<ProgrammeMembershipTypeDto> updateProgrammeMembershipType(
      @RequestBody ProgrammeMembershipTypeDto programmeMembershipTypeDto) {

    ProgrammeMembershipType programmeMembershipType = mapper.toEntity(programmeMembershipTypeDto);
    programmeMembershipType = service.update(programmeMembershipType);
    return ResponseEntity.ok(mapper.toDto(programmeMembershipType));
  }

  /**
   * Delete the ProgrammeMembershipType with the given tisId.
   *
   * @param tisId The tisId of the ProgrammeMembershipType to delete.
   */
  @DeleteMapping("/programme-membership-type/{tisId}")
  public ResponseEntity<Void> deleteProgrammeMembershipType(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
