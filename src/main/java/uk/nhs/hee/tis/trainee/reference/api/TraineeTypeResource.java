/*
 * The MIT License (MIT)
 *
 * Copyright 2026 Crown Copyright (Health Education England)
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
import uk.nhs.hee.tis.trainee.reference.dto.TraineeTypeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.TraineeTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.TraineeType;
import uk.nhs.hee.tis.trainee.reference.service.TraineeTypeService;

/**
 * Resource for trainee types.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@XRayEnabled
public class TraineeTypeResource {

  private final TraineeTypeService service;
  private final TraineeTypeMapper mapper;

  public TraineeTypeResource(TraineeTypeService service,
      TraineeTypeMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get TraineeTypes from reference table.
   *
   * @return list of TraineeTypes.
   */
  @GetMapping("/trainee-type")
  public List<TraineeTypeDto> getTraineeTypes() {
    log.trace("Get all TraineeTypes");
    List<TraineeType> traineeTypes = service.get();
    return mapper.toDtos(traineeTypes);
  }

  /**
   * Create a TraineeType, or update an existing TraineeType if the tisId
   * matches.
   *
   * @param traineeTypeDto The TraineeType to create.
   * @return The created (or updated) TraineeType.
   */
  @PostMapping("/trainee-type")
  public ResponseEntity<TraineeTypeDto> createTraineeType(
      @RequestBody TraineeTypeDto traineeTypeDto) {
    TraineeType traineeType = mapper.toEntity(traineeTypeDto);
    traineeType = service.create(traineeType);
    return ResponseEntity.created(URI.create("/api/trainee-type"))
        .body(mapper.toDto(traineeType));
  }

  /**
   * Update a TraineeType with a matching tisId value, or creates a new
   * TraineeType if the ID was not found.
   *
   * @param traineeTypeDto The TraineeType details to update.
   * @return The updated (or created) TraineeType.
   */
  @PutMapping("/trainee-type")
  public ResponseEntity<TraineeTypeDto> updateTraineeType(
      @RequestBody TraineeTypeDto traineeTypeDto) {
    TraineeType traineeType = mapper.toEntity(traineeTypeDto);
    traineeType = service.update(traineeType);
    return ResponseEntity.ok(mapper.toDto(traineeType));
  }

  /**
   * Delete the TraineeType with the given tisId.
   *
   * @param tisId The tisId of the TraineeType to delete.
   */
  @DeleteMapping("/trainee-type/{tisId}")
  public ResponseEntity<Void> deleteTraineeType(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
