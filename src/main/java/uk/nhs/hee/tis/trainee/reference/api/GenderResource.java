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
import uk.nhs.hee.tis.trainee.reference.dto.GenderDto;
import uk.nhs.hee.tis.trainee.reference.mapper.GenderMapper;
import uk.nhs.hee.tis.trainee.reference.model.Gender;
import uk.nhs.hee.tis.trainee.reference.service.GenderService;

@RestController
@RequestMapping("/api")
@XRayEnabled
public class GenderResource {

  private static final Logger log = LoggerFactory.getLogger(GenderResource.class);

  private final GenderService service;
  private final GenderMapper mapper;

  public GenderResource(GenderService service, GenderMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get Gender options from reference table.
   *
   * @return list of Genders.
   */
  @GetMapping("/gender")
  public List<GenderDto> getGenders() {
    log.trace("Get all Genders");
    List<Gender> genders = service.get();
    return mapper.toDtos(genders);
  }

  /**
   * Create a Gender, or update an existing Gender if the tisID matches.
   *
   * @param genderDto The Gender to create.
   * @return The created (or updated) Gender.
   */
  @PostMapping("/gender")
  public ResponseEntity<GenderDto> createGender(@RequestBody GenderDto genderDto) {
    Gender gender = mapper.toEntity(genderDto);
    gender = service.create(gender);
    return ResponseEntity.created(URI.create("/api/gender")).body(mapper.toDto(gender));
  }

  /**
   * Update a Gender with a matching tisId value, or creates a new Gender if the ID was not found.
   *
   * @param genderDto The Gender details to update.
   * @return The updated (or created) Gender.
   */
  @PutMapping("/gender")
  public ResponseEntity<GenderDto> updateGender(@RequestBody GenderDto genderDto) {
    Gender gender = mapper.toEntity(genderDto);
    gender = service.update(gender);
    return ResponseEntity.ok(mapper.toDto(gender));
  }

  /**
   * Delete the Gender with the given tisId.
   *
   * @param tisId The tisId of the Gender to delete.
   */
  @DeleteMapping("/gender/{tisId}")
  public ResponseEntity<Void> deleteGender(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
