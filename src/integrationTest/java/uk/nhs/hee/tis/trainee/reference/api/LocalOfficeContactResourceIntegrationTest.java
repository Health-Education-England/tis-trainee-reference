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
import java.util.UUID;
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
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class LocalOfficeContactResourceIntegrationTest {

  private static final String TIS_ID = "40";
  private static final String LABEL = "Default Local Office Contact";
  private static final String LOCAL_OFFICE_ID = UUID.randomUUID().toString();
  private static final String LOCAL_OFFICE_NAME = "Default Local Office Name";
  private static final String CONTACT_TYPE_ID = UUID.randomUUID().toString();
  private static final String CONTACT_TYPE_NAME = "Default Contact Type Name";
  private static final String CONTACT = "Default Contact";

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
    mongoTemplate.findAllAndRemove(new Query(), LocalOffice.class);
    mongoTemplate.findAllAndRemove(new Query(), LocalOfficeContactType.class);
    mongoTemplate.findAllAndRemove(new Query(), LocalOfficeContact.class);
  }

  @Test
  void shouldGetEmptyArrayWhenNoLocalOfficeContactsFound() throws Exception {
    mockMvc.perform(get("/api/local-office-contact"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void shouldGetLocalOfficeContactsOrderedByLabel() throws Exception {
    LocalOfficeContact entity1 = new LocalOfficeContact();
    entity1.setTisId(ObjectId.get().toString());
    entity1.setLabel("c");

    LocalOfficeContact entity2 = new LocalOfficeContact();
    entity2.setTisId(ObjectId.get().toString());
    entity2.setLabel("a");

    LocalOfficeContact entity3 = new LocalOfficeContact();
    entity3.setTisId(ObjectId.get().toString());
    entity3.setLabel("b");

    mongoTemplate.insertAll(List.of(entity1, entity2, entity3));

    mockMvc.perform(get("/api/local-office-contact"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$.[0].tisId").value(entity2.getTisId()))
        .andExpect(jsonPath("$.[1].tisId").value(entity3.getTisId()))
        .andExpect(jsonPath("$.[2].tisId").value(entity1.getTisId()));
  }

  @Test
  void shouldGetLocalOfficeContactsByLocalOfficeUuid() throws Exception {
    LocalOfficeContact entity1 = new LocalOfficeContact();
    entity1.setTisId(TIS_ID);
    entity1.setLocalOfficeId(LOCAL_OFFICE_ID);

    LocalOfficeContact entity2 = new LocalOfficeContact();
    entity2.setTisId(ObjectId.get().toString());
    entity2.setLocalOfficeId(LOCAL_OFFICE_ID);

    LocalOfficeContact entity3 = new LocalOfficeContact();
    entity3.setTisId(ObjectId.get().toString());

    mongoTemplate.insertAll(List.of(entity1, entity2, entity3));

    mockMvc.perform(get("/api/local-office-contact-by-lo-uuid/{localOfficeUuid}", LOCAL_OFFICE_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$.[0].tisId").value(entity1.getTisId()))
        .andExpect(jsonPath("$.[1].tisId").value(entity2.getTisId()));
  }

  @Test
  void shouldGetLocalOfficeContactsByLocalOfficeName() throws Exception {
    LocalOfficeContact entity1 = new LocalOfficeContact();
    entity1.setTisId(TIS_ID);
    entity1.setLocalOfficeName(LOCAL_OFFICE_NAME);

    LocalOfficeContact entity2 = new LocalOfficeContact();
    entity2.setTisId(ObjectId.get().toString());
    entity2.setLocalOfficeName(LOCAL_OFFICE_NAME);

    LocalOfficeContact entity3 = new LocalOfficeContact();
    entity3.setTisId(ObjectId.get().toString());

    mongoTemplate.insertAll(List.of(entity1, entity2, entity3));

    mockMvc.perform(
            get("/api/local-office-contact-by-lo-name/{localOfficeName}", LOCAL_OFFICE_NAME))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$.[0].tisId").value(entity1.getTisId()))
        .andExpect(jsonPath("$.[1].tisId").value(entity2.getTisId()));
  }

  @Test
  void shouldCreateLocalOfficeContact() throws Exception {
    LocalOffice localOffice = new LocalOffice();
    localOffice.setUuid(LOCAL_OFFICE_ID);
    localOffice.setLabel(LOCAL_OFFICE_NAME);
    mongoTemplate.insert(localOffice);

    LocalOfficeContactType contactType = new LocalOfficeContactType();
    contactType.setTisId(CONTACT_TYPE_ID);
    contactType.setLabel(CONTACT_TYPE_NAME);
    mongoTemplate.insert(contactType);

    String content = """
        {
          "tisId": "%s",
          "label": "%s",
          "localOfficeId": "%s",
          "localOfficeName": "%s",
          "contactTypeId": "%s",
          "contactTypeName": "%s",
          "contact": "%s"
        }
        """.formatted(TIS_ID, LABEL, LOCAL_OFFICE_ID, LOCAL_OFFICE_NAME, CONTACT_TYPE_ID,
        CONTACT_TYPE_NAME, CONTACT);

    mockMvc.perform(post("/api/local-office-contact")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isCreated());

    List<LocalOfficeContact> entities = mongoTemplate.findAll(LocalOfficeContact.class);
    assertThat("Unexpected local office contact count.", entities, hasSize(1));

    LocalOfficeContact entity = entities.get(0);
    assertThat("Unexpected TIS ID.", entity.getTisId(), is(TIS_ID));
    assertThat("Unexpected label.", entity.getLabel(),
        is("%s (%s) - %s".formatted(CONTACT, CONTACT_TYPE_NAME, LOCAL_OFFICE_NAME)));
    assertThat("Unexpected local office ID.", entity.getLocalOfficeId(), is(LOCAL_OFFICE_ID));
    assertThat("Unexpected local office name.", entity.getLocalOfficeName(), is(LOCAL_OFFICE_NAME));
    assertThat("Unexpected contact type ID.", entity.getContactTypeId(), is(CONTACT_TYPE_ID));
    assertThat("Unexpected contact type name.", entity.getContactTypeName(), is(CONTACT_TYPE_NAME));
    assertThat("Unexpected contact.", entity.getContact(), is(CONTACT));
  }

  @Test
  void shouldUpdateLocalOfficeContact() throws Exception {
    LocalOffice localOffice = new LocalOffice();
    localOffice.setUuid(LOCAL_OFFICE_ID);
    localOffice.setLabel(LOCAL_OFFICE_NAME);
    mongoTemplate.insert(localOffice);

    LocalOfficeContactType contactType = new LocalOfficeContactType();
    contactType.setTisId(CONTACT_TYPE_ID);
    contactType.setLabel(CONTACT_TYPE_NAME);
    mongoTemplate.insert(contactType);

    LocalOfficeContact initialEntity = new LocalOfficeContact();
    initialEntity.setTisId(TIS_ID);
    initialEntity.setLabel(LABEL);
    mongoTemplate.insert(initialEntity);

    String content = """
        {
          "tisId": "%s",
          "label": "New Label",
          "localOfficeId": "%s",
          "localOfficeName": "New Local Office Name",
          "contactTypeId": "%s",
          "contactTypeName": "New Contact Type Name",
          "contact": "New Contact"
        }
        """.formatted(TIS_ID, LOCAL_OFFICE_ID, CONTACT_TYPE_ID);

    mockMvc.perform(put("/api/local-office-contact")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andExpect(status().isOk());

    List<LocalOfficeContact> entities = mongoTemplate.findAll(LocalOfficeContact.class);
    assertThat("Unexpected local office contact count.", entities, hasSize(1));

    LocalOfficeContact updatedEntity = entities.get(0);
    assertThat("Unexpected TIS ID.", updatedEntity.getTisId(), is(TIS_ID));
    assertThat("Unexpected label.", updatedEntity.getLabel(),
        is("New Contact (%s) - %s".formatted(CONTACT_TYPE_NAME, LOCAL_OFFICE_NAME)));
    assertThat("Unexpected local office ID.", updatedEntity.getLocalOfficeId(),
        is(LOCAL_OFFICE_ID));
    assertThat("Unexpected local office name.", updatedEntity.getLocalOfficeName(),
        is(LOCAL_OFFICE_NAME));
    assertThat("Unexpected contact type ID.", updatedEntity.getContactTypeId(),
        is(CONTACT_TYPE_ID));
    assertThat("Unexpected contact type name.", updatedEntity.getContactTypeName(),
        is(CONTACT_TYPE_NAME));
    assertThat("Unexpected contact.", updatedEntity.getContact(), is("New Contact"));
  }

  @Test
  void shouldDeleteLocalOfficeContact() throws Exception {
    LocalOfficeContact entity = new LocalOfficeContact();
    entity.setTisId(TIS_ID);
    entity.setLabel(LABEL);
    mongoTemplate.insert(entity);

    mockMvc.perform(delete("/api/local-office-contact/{tisId}", TIS_ID))
        .andExpect(status().isNoContent());

    long count = mongoTemplate.count(new Query(), LocalOfficeContact.class);
    assertThat("Unexpected local office contact count.", count, is(0L));
  }
}
