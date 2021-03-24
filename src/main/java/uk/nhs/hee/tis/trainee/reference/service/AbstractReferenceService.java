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

import java.util.List;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.repository.ReferenceRepository;

public abstract class AbstractReferenceService<T> implements ReferenceService<T> {

  private ReferenceRepository<T> repository;

  protected AbstractReferenceService(ReferenceRepository<T> repository) {
    this.repository = repository;
  }

  @Override
  public List<T> get() {
    return repository.findAll(getSort());
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
  public T update(T entity) {
    T persistedEntity = repository.findByTisId(getTisId(entity));

    if (persistedEntity == null) {
      return create(entity);
    }

    copyAttributes(persistedEntity, entity);
    return repository.save(persistedEntity);
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
   * Get the sort to apply to returned results.
   *
   * @return The Sort to apply.
   */
  protected abstract Sort getSort();

  /**
   * Copy the attributes from one entity to the other.
   *
   * @param target The entity to copy attributes to.
   * @param source The entity to copy attributes from.
   */
  protected abstract void copyAttributes(T target, T source);
}
