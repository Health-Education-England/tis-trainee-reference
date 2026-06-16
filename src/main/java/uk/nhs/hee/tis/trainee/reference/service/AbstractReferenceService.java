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
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.mapper.PatchMapper;
import uk.nhs.hee.tis.trainee.reference.repository.ReferenceRepository;

/**
 * Abstract base service providing common reference data operations.
 *
 * @param <T> The entity type.
 * @param <D> The patch DTO type, using TIS field names.
 */
@Slf4j
public abstract class AbstractReferenceService<T, D> implements ReferenceService<T> {

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
    T patchedEntity = applyPatch(entity, patch);
    return repository.insert(patchedEntity);
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
    log.debug("Looking up entity with tisId: {}", tisId);
    T persistedEntity = repository.findByTisId(tisId);
    log.debug("Found entity: {}", persistedEntity);

    if (persistedEntity == null) {
      throw new IllegalArgumentException(
          "Unknown entity for tisId [%s].".formatted(tisId));
    }

    T patchedEntity = applyPatch(persistedEntity, patch);
    String patchedTisId = getTisId(patchedEntity);

    if (patchedTisId == null || !patchedTisId.equals(tisId)) {
      throw new IllegalArgumentException(
          "Patched entity tisId [%s] does not match expected tisId [%s]."
              .formatted(patchedTisId, tisId));
    }

    return repository.save(patchedEntity);
  }

  /**
   * Applies a JSON patch to an entity via its patch DTO and returns the patched entity.
   *
   * @param entity The entity to patch.
   * @param patch  The patch to apply.
   * @return The patched entity.
   * @throws JsonProcessingException If the entity cannot be serialised or deserialised.
   * @throws JsonPatchException      If the patch cannot be applied.
   */
  private T applyPatch(T entity, JsonPatch patch)
      throws JsonProcessingException, JsonPatchException {
    PatchMapper<T, D> patchMapper = getPatchMapper();
    D dto = patchMapper.toPatchDto(entity);
    JsonNode dtoNode = mapper.convertValue(dto, JsonNode.class);
    JsonNode patchedNode = patch.apply(dtoNode);

    D patchedDto = (D) mapper.treeToValue(patchedNode, dto.getClass());

    patchMapper.copyEntityId(dto, patchedDto);

    return patchMapper.toEntity(patchedDto);
  }

  @Override
  public void deleteByTisId(String tisId) {
    repository.deleteByTisId(tisId);
  }

  /**
   * Get the patch mapper for this entity type.
   *
   * @return The patch mapper.
   */
  protected PatchMapper<T, D> getPatchMapper() {
    // TODO: make abstract once existing implementations are migrated.
    throw new NotImplementedException("Patch mapping is not implemented for this entity type.");
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
