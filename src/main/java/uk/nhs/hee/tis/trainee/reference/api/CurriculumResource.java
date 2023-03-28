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
import uk.nhs.hee.tis.trainee.reference.dto.CurriculumDto;
import uk.nhs.hee.tis.trainee.reference.dto.validator.CurriculumValidator;
import uk.nhs.hee.tis.trainee.reference.mapper.CurriculumMapper;
import uk.nhs.hee.tis.trainee.reference.model.Curriculum;
import uk.nhs.hee.tis.trainee.reference.service.CurriculumService;

@Slf4j
@RestController
@RequestMapping("/api")
@XRayEnabled
public class CurriculumResource {

  private final CurriculumService service;
  private final CurriculumMapper mapper;
  private final CurriculumValidator validator;

  CurriculumResource(CurriculumService service, CurriculumMapper mapper,
      CurriculumValidator validator) {
    this.service = service;
    this.mapper = mapper;
    this.validator = validator;
  }

  /**
   * Get Curriculum options from reference table.
   *
   * @return list of Curricula.
   */
  @GetMapping("/curriculum")
  public List<CurriculumDto> getCurricula() {
    log.trace("Get all Curricula");
    List<Curriculum> curricula = service.get();
    return mapper.toDtos(curricula);
  }

  /**
   * Create a Curriculum, or update an existing Curriculum if the tisID matches.
   *
   * @param curriculumDto The Curriculum to create.
   * @return The created (or updated) Curriculum.
   */
  @PostMapping("/curriculum")
  public ResponseEntity<CurriculumDto> createCurriculum(@RequestBody CurriculumDto curriculumDto) {
    if (!validator.isValid(curriculumDto)) {
      // TODO: refactor service to take DTO instead of entity and move business logic there?
      service.deleteByTisId(curriculumDto.getTisId());
      return ResponseEntity.unprocessableEntity().build();
    }

    Curriculum curriculum = mapper.toEntity(curriculumDto);
    curriculum = service.create(curriculum);
    return ResponseEntity.created(URI.create("/api/curriculum")).body(mapper.toDto(curriculum));
  }

  /**
   * Update a Curriculum with a matching tisId value, or creates a new Curriculum if the ID was not
   * found.
   *
   * @param curriculumDto The Curriculum details to update.
   * @return The updated (or created) Curriculum.
   */
  @PutMapping("/curriculum")
  public ResponseEntity<CurriculumDto> updateCurriculum(@RequestBody CurriculumDto curriculumDto) {
    if (!validator.isValid(curriculumDto)) {
      // TODO: refactor service to take DTO instead of entity and move business logic there?
      service.deleteByTisId(curriculumDto.getTisId());
      return ResponseEntity.unprocessableEntity().build();
    }

    Curriculum curriculum = mapper.toEntity(curriculumDto);
    curriculum = service.update(curriculum);
    return ResponseEntity.ok(mapper.toDto(curriculum));
  }

  /**
   * Delete the Curriculum with the given tisId.
   *
   * @param tisId The tisId of the Curriculum to delete.
   */
  @DeleteMapping("/curriculum/{tisId}")
  public ResponseEntity<Void> deleteCurriculum(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
