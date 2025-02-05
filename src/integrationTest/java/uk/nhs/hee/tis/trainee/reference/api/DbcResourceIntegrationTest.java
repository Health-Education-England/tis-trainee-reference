/*
 * The MIT License (MIT)
 *
 * Copyright 2024 Crown Copyright (Health Education England)
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.nhs.hee.tis.trainee.reference.DockerImageNames;
import uk.nhs.hee.tis.trainee.reference.model.Dbc;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class DbcResourceIntegrationTest {

  private static final String TIS_ID = "40";
  private static final String LABEL = "Default Dbc";
  private static final String TYPE = "Default Type";

  @Container
  @ServiceConnection
  private static final MongoDBContainer mongoContainer = new MongoDBContainer(
      DockerImageNames.MONGO);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MongoTemplate mongoTemplate;

  @AfterEach
  void cleanUp() {
    mongoTemplate.findAllAndRemove(new Query(), Dbc.class);
  }

  @Test
  void shouldGetEmptyArrayWhenNoDbcFound() throws Exception {
    mockMvc.perform(get("/api/dbc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void shouldGetDbcOrderedByLabel() throws Exception {
    Dbc entity1 = new Dbc();
    entity1.setId(ObjectId.get().toString());
    entity1.setLabel("c");

    Dbc entity2 = new Dbc();
    entity2.setId(ObjectId.get().toString());
    entity2.setLabel("a");

    Dbc entity3 = new Dbc();
    entity3.setId(ObjectId.get().toString());
    entity3.setLabel("b");

    mongoTemplate.insertAll(List.of(entity1, entity2, entity3));

    mockMvc.perform(get("/api/dbc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$.[0].id").value(entity2.getId()))
        .andExpect(jsonPath("$.[1].id").value(entity3.getId()))
        .andExpect(jsonPath("$.[2].id").value(entity1.getId()));
  }

  @Test
  void shouldCreateDbc() throws Exception {
    String content = """
        {
          "tisId": "%s",
          "label": "%s",
          "type": "%s",
          "internal": true
        }
        """.formatted(TIS_ID, LABEL, TYPE);

    mockMvc.perform(post("/api/dbc")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isCreated());

    List<Dbc> entities = mongoTemplate.findAll(Dbc.class);
    assertThat("Unexpected dbc count.", entities, hasSize(1));

    Dbc entity = entities.get(0);
    assertThat("Unexpected ID.", entity.getId(), notNullValue());
    assertThat("Unexpected TIS ID.", entity.getTisId(), is(TIS_ID));
    assertThat("Unexpected label.", entity.getLabel(), is(LABEL));
    assertThat("Unexpected type.", entity.getType(), is(TYPE));
    assertThat("Unexpected internal flag.", entity.isInternal(), is(true));
  }

  @Test
  void shouldUpdateDbc() throws Exception {
    String id = ObjectId.get().toString();
    Dbc initialEntity = new Dbc();
    initialEntity.setId(id);
    initialEntity.setTisId(TIS_ID);
    initialEntity.setLabel(LABEL);
    initialEntity.setType(TYPE);
    initialEntity.setInternal(true);
    mongoTemplate.insert(initialEntity);

    String content = """
        {
          "id": "%s",
          "tisId": "%s",
          "label": "New Label",
          "type": "New Type",
          "internal": "false"
        }
        """.formatted(id, TIS_ID);

    mockMvc.perform(put("/api/dbc")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isOk());

    List<Dbc> entities = mongoTemplate.findAll(Dbc.class);
    assertThat("Unexpected dbc count.", entities, hasSize(1));

    Dbc updatedEntity = entities.get(0);
    assertThat("Unexpected ID.", updatedEntity.getId(), is(id));
    assertThat("Unexpected TIS ID.", updatedEntity.getTisId(), is(TIS_ID));
    assertThat("Unexpected label.", updatedEntity.getLabel(), is("New Label"));
    assertThat("Unexpected type.", updatedEntity.getType(), is("New Type"));
    assertThat("Unexpected internal flag.", updatedEntity.isInternal(), is(false));
  }

  @Test
  void shouldDeleteDbc() throws Exception {
    Dbc entity = new Dbc();
    entity.setTisId(TIS_ID);
    entity.setLabel(LABEL);
    mongoTemplate.insert(entity);

    mockMvc.perform(delete("/api/dbc/{tisId}", TIS_ID))
        .andExpect(status().isNoContent());

    long count = mongoTemplate.count(new Query(), Dbc.class);
    assertThat("Unexpected dbc count.", count, is(0L));
  }
}
