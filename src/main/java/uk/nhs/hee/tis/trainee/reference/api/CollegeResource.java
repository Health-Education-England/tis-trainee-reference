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
import uk.nhs.hee.tis.trainee.reference.dto.CollegeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.CollegeMapper;
import uk.nhs.hee.tis.trainee.reference.model.College;
import uk.nhs.hee.tis.trainee.reference.service.CollegeService;

@Slf4j
@RestController
@RequestMapping("/api")
public class CollegeResource {

  private CollegeService service;
  private CollegeMapper mapper;

  public CollegeResource(CollegeService service, CollegeMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get College options from reference table.
   *
   * @return list of Colleges.
   */
  @GetMapping("/college")
  public List<CollegeDto> getCollege() {
    log.trace("Get all College");
    List<College> colleges = service.get();
    return mapper.toDtos(colleges);
  }

  /**
   * Create a College, or update an existing College if the tisID matches.
   *
   * @param collegeDto The College to create.
   * @return The created (or updated) College.
   */
  @PostMapping("/college")
  public ResponseEntity<CollegeDto> createCollege(@RequestBody CollegeDto collegeDto) {
    College college = mapper.toEntity(collegeDto);
    college = service.create(college);
    return ResponseEntity.created(URI.create("/api/college")).body(mapper.toDto(college));
  }

  /**
   * Update a College with a matching tisId value, or creates a new College if the ID was not
   * found.
   *
   * @param collegeDto The College details to update.
   * @return The updated (or created) College.
   */
  @PutMapping("/college")
  public ResponseEntity<CollegeDto> updateCollege(@RequestBody CollegeDto collegeDto) {
    College college = mapper.toEntity(collegeDto);
    college = service.update(college);
    return ResponseEntity.ok(mapper.toDto(college));
  }

  /**
   * Delete the College with the given tisId.
   *
   * @param tisId The tisId of the College to delete.
   */
  @DeleteMapping("/college/{tisId}")
  public ResponseEntity<Void> deleteCollege(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
