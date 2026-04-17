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

package uk.nhs.hee.tis.trainee.reference.mapper;

/**
 * A mapper interface for converting between an entity and its CDC patch DTO representation.
 *
 * @param <T> The entity type.
 * @param <D> The patch DTO type.
 */
public interface PatchMapper<T, D> {

  /**
   * Convert an entity to its patch DTO representation.
   *
   * @param entity The entity to convert.
   * @return The patch DTO.
   */
  D toPatchDto(T entity);

  /**
   * Convert a patch DTO to an entity.
   *
   * @param dto The patch DTO to convert.
   * @return The entity.
   */
  T toEntity(D dto);

  /**
   * Copy the entity ID from one patch DTO to another. This is needed to preserve identity fields
   * that are excluded from JSON serialisation during patch application.
   *
   * @param source The DTO to copy the entity ID from.
   * @param target The DTO to copy the entity ID to.
   */
  void copyEntityId(D source, D target);
}
