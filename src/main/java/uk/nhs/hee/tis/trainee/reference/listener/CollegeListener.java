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
package uk.nhs.hee.tis.trainee.reference.listener;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.nhs.hee.tis.trainee.reference.event.CdcPatchEvent;
import uk.nhs.hee.tis.trainee.reference.model.College;
import uk.nhs.hee.tis.trainee.reference.service.CollegeService;
import com.flipkart.zjsonpatch.JsonPatch;

/**
 * A listener for College CDC patch events.
 */
@Slf4j
@Component
@XRayEnabled
public class CollegeListener {

  private final CollegeService service;
  private final ObjectMapper objectMapper;

  public CollegeListener(CollegeService service, ObjectMapper objectMapper) {
    this.service = service;
    this.objectMapper = objectMapper;
  }

  @SqsListener("${application.queues.college-patch}")
  public void handleCdcPatch(CdcPatchEvent event) throws JsonProcessingException {
    log.info("Received CDC patch for College.");

    boolean isDelete = StreamSupport.stream(event.patch().spliterator(), false)
        .anyMatch(op -> "remove".equals(op.get("op").asText())
            && "".equals(op.get("path").asText()));

    if (isDelete) {
      String tisId = event.keys().get("id").asText();
      log.info("Deleting College with tisId: {}", tisId);
      service.deleteByTisId(tisId);
    } else {
      boolean isInsertOrLoad = StreamSupport.stream(event.patch().spliterator(), false)
          .anyMatch(op -> "add".equals(op.get("op").asText())
              && "".equals(op.get("path").asText()));

      String tisId;
      if (isInsertOrLoad) {
        tisId = StreamSupport.stream(event.patch().spliterator(), false)
            .filter(op -> "add".equals(op.get("op").asText()))
            .findFirst()
            .map(op -> op.get("value").get("id").asText())
            .orElseThrow();
      } else {
        tisId = event.keys().get("id").asText();
      }

      College existing = service.findByTisId(tisId).orElse(new College());
      existing.setTisId(tisId);
      JsonNode existingNode = objectMapper.valueToTree(existing);
      JsonNode patchedNode = JsonPatch.apply(event.patch(), existingNode);
      College updated = objectMapper.treeToValue(patchedNode, College.class);
      updated.setTisId(tisId);

      log.info("Upserting College with tisId: {}", updated.getTisId());
      service.update(updated);
    }
  }
}