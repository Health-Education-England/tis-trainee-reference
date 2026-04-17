/*
 * The MIT License (MIT)
 *
 * Copyright 2026 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.nhs.hee.tis.trainee.reference.event;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import uk.nhs.hee.tis.trainee.reference.model.College;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.Random.class)
class CollegeListenerIntegrationTest {

  private static final String COLLEGE_QUEUE = UUID.randomUUID().toString();

  @Container
  @ServiceConnection
  private static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:6.0.19");

  @Container
  private static final LocalStackContainer localstack = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:3.8.1"))
      .withServices(SQS);

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("application.queues.college", () -> COLLEGE_QUEUE);
    registry.add("application.queues.curriculum", () -> UUID.randomUUID().toString());
    registry.add("application.queues.dbc", () -> UUID.randomUUID().toString());
    registry.add("application.queues.gender", () -> UUID.randomUUID().toString());
    registry.add("application.queues.grade", () -> UUID.randomUUID().toString());
    registry.add("application.queues.immigration-status", () -> UUID.randomUUID().toString());
    registry.add("application.queues.local-office", () -> UUID.randomUUID().toString());
    registry.add("application.queues.local-office-contact", () -> UUID.randomUUID().toString());
    registry.add("application.queues.local-office-contact-type",
        () -> UUID.randomUUID().toString());
    registry.add("application.queues.programme-membership-type",
        () -> UUID.randomUUID().toString());
    registry.add("spring.cloud.aws.region.static", localstack::getRegion);
    registry.add("spring.cloud.aws.credentials.access-key", localstack::getAccessKey);
    registry.add("spring.cloud.aws.credentials.secret-key", localstack::getSecretKey);
    registry.add("spring.cloud.aws.sqs.endpoint",
        () -> localstack.getEndpointOverride(SQS).toString());
    registry.add("spring.cloud.aws.sqs.enabled", () -> true);
  }

  @BeforeAll
  static void setUpBeforeAll() throws IOException, InterruptedException {
    localstack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", COLLEGE_QUEUE);
  }

  @Autowired
  private SqsTemplate sqsTemplate;

  @Autowired
  private MongoTemplate mongoTemplate;

  @AfterEach
  void cleanUp() {
    mongoTemplate.findAllAndRemove(new Query(), College.class);
  }

  @Test
  void shouldInsertCollegeWhenInsertOrLoadPatchReceived() {
    String tisId = UUID.randomUUID().toString();
    String message = """
        {
          "patch": [
            {
              "op": "add",
              "path": "",
              "value": {
                "id": "%s",
                "label": "Test College"
              }
            }
          ],
          "metadata": {
            "schema-name": "reference",
            "table-name": "College"
          }
        }
        """.formatted(tisId);

    sqsTemplate.send(COLLEGE_QUEUE, message);

    await()
        .pollInterval(Duration.ofSeconds(2))
        .atMost(Duration.ofSeconds(10))
        .ignoreExceptions()
        .untilAsserted(() -> {
          College college = mongoTemplate.findOne(
              Query.query(Criteria.where("tisId").is(tisId)), College.class);
          assertThat("College not found.", college != null, is(true));
          assertThat("Unexpected label.", college.getLabel(), is("Test College"));
        });
  }

  @Test
  void shouldUpdateCollegeWhenUpdatePatchReceived() {
    String tisId = UUID.randomUUID().toString();

    College existing = new College();
    existing.setTisId(tisId);
    existing.setLabel("Old Label");
    mongoTemplate.insert(existing);

    String message = """
        {
          "keys": { "id": "%s" },
          "patch": [
            { "op": "test", "path": "/label", "value": "Old Label" },
            { "op": "replace", "path": "/label", "value": "New Label" }
          ],
          "metadata": {
            "schema-name": "reference",
            "table-name": "College"
          }
        }
        """.formatted(tisId);

    sqsTemplate.send(COLLEGE_QUEUE, message);

    await()
        .pollInterval(Duration.ofSeconds(2))
        .atMost(Duration.ofSeconds(10))
        .ignoreExceptions()
        .untilAsserted(() -> {
          College college = mongoTemplate.findOne(
              Query.query(Criteria.where("tisId").is(tisId)), College.class);
          assertThat("College not found.", college != null, is(true));
          assertThat("Unexpected label.", college.getLabel(), is("New Label"));
        });
  }

  @Test
  void shouldDeleteCollegeWhenDeletePatchReceived() {
    String tisId = UUID.randomUUID().toString();

    College existing = new College();
    existing.setTisId(tisId);
    existing.setLabel("To Be Deleted");
    mongoTemplate.insert(existing);

    String message = """
        {
          "keys": { "id": "%s" },
          "patch": [
            { "op": "test", "path": "/label", "value": "To Be Deleted" },
            { "op": "remove", "path": "" }
          ],
          "metadata": {
            "schema-name": "reference",
            "table-name": "College"
          }
        }
        """.formatted(tisId);

    sqsTemplate.send(COLLEGE_QUEUE, message);

    await()
        .pollInterval(Duration.ofSeconds(2))
        .atMost(Duration.ofSeconds(10))
        .ignoreExceptions()
        .untilAsserted(() -> {
          College college = mongoTemplate.findOne(
              Query.query(Criteria.where("tisId").is(tisId)), College.class);
          assertThat("College should have been deleted.", college, nullValue());
        });
  }
}