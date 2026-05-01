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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.repository.ReferenceRepository;

@Slf4j
public abstract class AbstractReferenceService<T> implements ReferenceService<T> {

  private final ReferenceRepository<T> repository;
  private final ObjectMapper mapper;

  protected AbstractReferenceService(ReferenceRepository<T> repository, ObjectMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public List<T> get() {
    return repository.findAll(getSort());
  }

  /**
   * Get the sort to apply to returned results.
   *
   * @return The Sort to apply.
   */
  protected Sort getSort() {
    return Sort.by("label");
  }

  @Override
  public T create(T entity) {
    T persistedEntity = repository.findByTisId(getTisId(entity));

    if (persistedEntity != null) {
      return update(entity);
    }

    return repository.insert(entity);
  }

  @Override
  public T create(T entity, JsonPatch patch) throws JsonPatchException, JsonProcessingException {
    return patch(entity, patch);
  }

  @Override
  public T update(T entity) {
    T persistedEntity = repository.findByTisId(getTisId(entity));

    if (persistedEntity == null) {
      return create(entity);
    }

    copyAttributes(persistedEntity, entity);
    return repository.save(persistedEntity);
  }

  @Override
  public T update(String tisId, JsonPatch patch)
      throws JsonPatchException, JsonProcessingException {
    T persistedEntity = repository.findByTisId(tisId);

    if (persistedEntity == null) {
      // TODO: may be unclear what entity type we're dealing with, check stacktrace gives context.
      throw new IllegalArgumentException("Unknown entity with id [%s].".formatted(tisId));
    }

    return patch(persistedEntity, patch);
  }

  /**
   * Applies a JSON patch to an entity and saves the result.
   *
   * @param entity The entity to patch.
   * @param patch  The patch to apply.
   * @return The patched and saved entity.
   * @throws JsonProcessingException If the entity cannot be serialised or deserialised.
   * @throws JsonPatchException      If the patch cannot be applied.
   */
  private T patch(T entity, JsonPatch patch) throws JsonProcessingException, JsonPatchException {
    JsonNode entityNode = mapper.convertValue(entity, JsonNode.class);
    JsonNode patchedNode = patch.apply(entityNode);
    T patchedEntity = (T) mapper.treeToValue(patchedNode, entity.getClass());
    return repository.save(patchedEntity);
  }

  @Override
  public void deleteByTisId(String tisId) {
    repository.deleteByTisId(tisId);
  }

  /**
   * Get the TIS id from the given entity.
   *
   * @param entity The entity to get the TIS id of.
   * @return The TIS id.
   */
  protected abstract String getTisId(T entity);

  /**
   * Copy the attributes from one entity to the other.
   *
   * @param target The entity to copy attributes to.
   * @param source The entity to copy attributes from.
   */
  protected abstract void copyAttributes(T target, T source);
}