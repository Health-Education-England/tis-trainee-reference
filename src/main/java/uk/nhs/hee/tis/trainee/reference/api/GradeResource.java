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
import uk.nhs.hee.tis.trainee.reference.dto.GradeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.GradeMapper;
import uk.nhs.hee.tis.trainee.reference.model.Grade;
import uk.nhs.hee.tis.trainee.reference.service.GradeService;

@RestController
@RequestMapping("/api")
public class GradeResource {

  private static final Logger log = LoggerFactory.getLogger(GradeResource.class);

  private final GradeService service;
  private final GradeMapper mapper;

  public GradeResource(GradeService service, GradeMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get Grade options from reference table.
   *
   * @return list of Grades.
   */
  @GetMapping("/grade")
  public ResponseEntity<List<GradeDto>> getGrades() {
    log.trace("Get all Grades");
    List<Grade> grades = service.get();
    return ResponseEntity.ok(mapper.toDtos(grades));
  }

  /**
   * Create a Grade, or update an existing Grade if the tisID matches.
   *
   * @param gradeDto The Grade to create.
   * @return The created (or updated) Grade.
   */
  @PostMapping("/grade")
  public ResponseEntity<GradeDto> createGrade(@RequestBody GradeDto gradeDto) {
    Grade grade = mapper.toEntity(gradeDto);
    grade = service.create(grade);
    return ResponseEntity.created(URI.create("/api/grade")).body(mapper.toDto(grade));
  }

  /**
   * Update a Grade with a matching tisId value, or creates a new Grade if the ID was not found.
   *
   * @param gradeDto The Grade details to update.
   * @return The updated (or created) Grade.
   */
  @PutMapping("/grade")
  public ResponseEntity<GradeDto> updateGrade(@RequestBody GradeDto gradeDto) {
    Grade grade = mapper.toEntity(gradeDto);
    grade = service.update(grade);
    return ResponseEntity.ok(mapper.toDto(grade));
  }

  /**
   * Delete the Grade with the given tisId.
   *
   * @param tisId The tisId of the Grade to delete.
   */
  @DeleteMapping("/grade/{tisId}")
  public ResponseEntity<Void> deleteGrade(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
