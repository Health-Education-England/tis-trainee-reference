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

package uk.nhs.hee.tis.trainee.reference.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import java.util.List;

public interface ReferenceService<T> {

  /**
   * Get all entities.
   *
   * @return The list of entities.
   */
  List<T> get();

  /**
   * Create the given entity.
   *
   * @param entity The entity to create.
   * @return The created entity.
   */
  T create(T entity);

  /**
   * Create an entity with the given patch data.
   *
   * @param patch The patch to create the entity from.
   * @return The created entity.
   */
  T create(T entity, JsonPatch patch) throws JsonPatchException, JsonProcessingException;

  /**
   * Update the given entity.
   *
   * @param entity The entity to update.
   * @return The updated entity.
   */
  T update(T entity);

  /**
   * Update an entity by its TIS ID, by applying a JSON Patch to it.
   *
   * @param tisId The TID id of the entity to update.
   * @param patch The patch to apply to the entity.
   * @return The updated entity.
   */
  T update(String tisId, JsonPatch patch) throws JsonPatchException, JsonProcessingException;

  /**
   * Delete an entity by its TIS id.
   *
   * @param tisId The TIS id to delete the entity for.
   */
  void deleteByTisId(String tisId);
}
