/*
 * The MIT License (MIT)
 *
 * Copyright 2021 Crown Copyright (Health Education England)
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
import uk.nhs.hee.tis.trainee.reference.dto.DbcDto;
import uk.nhs.hee.tis.trainee.reference.mapper.DbcMapper;
import uk.nhs.hee.tis.trainee.reference.model.Dbc;
import uk.nhs.hee.tis.trainee.reference.service.DbcService;

@Slf4j
@RestController
@RequestMapping("/api/dbc")
public class DbcResource {

  private DbcService service;
  private DbcMapper mapper;

  public DbcResource(DbcService service, DbcMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Get Dbc options from reference table.
   *
   * @return list of Dbcs.
   */
  @GetMapping
  public List<DbcDto> getDbc() {
    log.trace("Get all Dbcs");
    List<Dbc> dbcs = service.get();
    return mapper.toDtos(dbcs);
  }

  /**
   * Create a Dbc, or update an existing Dbc if the tisID matches.
   *
   * @param dbcDto The Dbc to create.
   * @return The created (or updated) Dbc.
   */
  @PostMapping
  public ResponseEntity<DbcDto> createDbc(
      @RequestBody DbcDto dbcDto) {
    Dbc dbc = mapper.toEntity(dbcDto);
    dbc = service.create(dbc);
    return ResponseEntity.created(URI.create("/api/dbc")).body(mapper.toDto(dbc));
  }

  /**
   * Update a Dbc with a matching tisId value, or creates a new Dbc if the ID was not found.
   *
   * @param dbcDto The Dbc details to update.
   * @return The updated (or created) Dbc.
   */
  @PutMapping
  public ResponseEntity<DbcDto> updateDbc(
      @RequestBody DbcDto dbcDto) {
    Dbc dbc = mapper.toEntity(dbcDto);
    dbc = service.update(dbc);
    return ResponseEntity.ok(mapper.toDto(dbc));
  }

  /**
   * Delete the Dbc with the given tisId.
   *
   * @param tisId The tisId of the Dbc to delete.
   */
  @DeleteMapping("/{tisId}")
  public ResponseEntity<Void> deleteDbc(@PathVariable String tisId) {
    service.deleteByTisId(tisId);
    return ResponseEntity.noContent().build();
  }
}
