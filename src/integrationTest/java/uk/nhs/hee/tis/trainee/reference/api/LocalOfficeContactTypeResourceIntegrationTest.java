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
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class LocalOfficeContactTypeResourceIntegrationTest {

  private static final String TIS_ID = "40";
  private static final String LABEL = "Default Local Office Contact Type";
  private static final String CODE = "Default Code";

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
    mongoTemplate.findAllAndRemove(new Query(), LocalOfficeContactType.class);
  }

  @Test
  void shouldGetEmptyArrayWhenNoLocalOfficeContactTypesFound() throws Exception {
    mockMvc.perform(get("/api/local-office-contact-type"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void shouldGetLocalOfficeContactTypesOrderedByLabel() throws Exception {
    LocalOfficeContactType entity1 = new LocalOfficeContactType();
    entity1.setTisId(ObjectId.get().toString());
    entity1.setLabel("c");

    LocalOfficeContactType entity2 = new LocalOfficeContactType();
    entity2.setTisId(ObjectId.get().toString());
    entity2.setLabel("a");

    LocalOfficeContactType entity3 = new LocalOfficeContactType();
    entity3.setTisId(ObjectId.get().toString());
    entity3.setLabel("b");

    mongoTemplate.insertAll(List.of(entity1, entity2, entity3));

    mockMvc.perform(get("/api/local-office-contact-type"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$.[0].tisId").value(entity2.getTisId()))
        .andExpect(jsonPath("$.[1].tisId").value(entity3.getTisId()))
        .andExpect(jsonPath("$.[2].tisId").value(entity1.getTisId()));
  }

  @Test
  void shouldCreateLocalOfficeContactType() throws Exception {
    String content = """
        {
          "tisId": "%s",
          "label": "%s",
          "code": "%s"
        }
        """.formatted(TIS_ID, LABEL, CODE);

    mockMvc.perform(post("/api/local-office-contact-type")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isCreated());

    List<LocalOfficeContactType> entities = mongoTemplate.findAll(LocalOfficeContactType.class);
    assertThat("Unexpected local office contact type count.", entities, hasSize(1));

    LocalOfficeContactType entity = entities.get(0);
    assertThat("Unexpected TIS ID.", entity.getTisId(), is(TIS_ID));
    assertThat("Unexpected label.", entity.getLabel(), is(LABEL));
    assertThat("Unexpected code.", entity.getCode(), is(CODE));
  }

  @Test
  void shouldUpdateLocalOfficeContactType() throws Exception {
    LocalOfficeContactType initialEntity = new LocalOfficeContactType();
    initialEntity.setTisId(TIS_ID);
    initialEntity.setLabel(LABEL);
    mongoTemplate.insert(initialEntity);

    String content = """
        {
          "tisId": "%s",
          "label": "New Label",
          "code": "New Code"
        }
        """.formatted(TIS_ID);

    mockMvc.perform(put("/api/local-office-contact-type")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isOk());

    List<LocalOfficeContactType> entities = mongoTemplate.findAll(LocalOfficeContactType.class);
    assertThat("Unexpected local office contact type count.", entities, hasSize(1));

    LocalOfficeContactType updatedEntity = entities.get(0);
    assertThat("Unexpected TIS ID.", updatedEntity.getTisId(), is(TIS_ID));
    assertThat("Unexpected label.", updatedEntity.getLabel(), is("New Label"));
    assertThat("Unexpected code.", updatedEntity.getCode(), is("New Code"));
  }

  @Test
  void shouldDeleteLocalOfficeContactType() throws Exception {
    LocalOfficeContactType entity = new LocalOfficeContactType();
    entity.setTisId(TIS_ID);
    entity.setLabel(LABEL);
    mongoTemplate.insert(entity);

    mockMvc.perform(delete("/api/local-office-contact-type/{tisId}", TIS_ID))
        .andExpect(status().isNoContent());

    long count = mongoTemplate.count(new Query(), LocalOfficeContactType.class);
    assertThat("Unexpected local office contact type count.", count, is(0L));
  }
}
