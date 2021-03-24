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

package uk.nhs.hee.tis.trainee.reference.service.impl;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.mapper.DbcMapper;
import uk.nhs.hee.tis.trainee.reference.model.Dbc;
import uk.nhs.hee.tis.trainee.reference.repository.DbcRepository;
import uk.nhs.hee.tis.trainee.reference.service.DbcService;

@Service
public class DbcServiceImpl implements DbcService {

  private final DbcRepository repository;
  private final DbcMapper mapper;

  public DbcServiceImpl(DbcRepository repository, DbcMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public List<Dbc> getDbcs() {
    return repository.findAll(Sort.by("name"));
  }

  @Override
  public Dbc updateDbc(Dbc dbc) {
    Dbc persistedDbc = repository.findByTisId(dbc.getTisId());

    if (persistedDbc == null) {
      return createDbc(dbc);
    }

    persistedDbc = mapper.update(persistedDbc, dbc);
    return repository.save(persistedDbc);
  }

  @Override
  public Dbc createDbc(Dbc dbc) {
    Dbc persistedDbc = repository.findByTisId(dbc.getTisId());

    if (persistedDbc != null) {
      return updateDbc(dbc);
    }

    return repository.insert(dbc);
  }

  @Override
  public void deleteDbc(String tisId) {
    repository.deleteByTisId(tisId);
  }
}
