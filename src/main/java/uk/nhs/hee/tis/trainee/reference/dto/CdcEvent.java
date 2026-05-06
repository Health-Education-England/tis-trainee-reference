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

package uk.nhs.hee.tis.trainee.reference.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jsonpatch.AddOperation;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.RemoveOperation;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Represents a CDC patch event received from an SQS queue.
 *
 * @param rawPatch   The raw JSON patch node, used for extracting values not exposed by the typed
 *                   operations.
 * @param keys       The key fields identifying the record, populated by the lambda if present.
 * @param metadata   Additional metadata about the event.
 */
public record CdcEvent(
    @JsonProperty("patch")
    JsonNode rawPatch,
    CdcKeys keys,
    Map<String, String> metadata) {

  public CdcEventType getEventType() {
    for (JsonNode op : rawPatch) {
      if ("".equals(op.path("path").asText())) {
        return switch (op.path("op").asText()) {
          case "add" -> CdcEventType.INSERT;
          case "remove" -> CdcEventType.DELETE;
          default -> CdcEventType.UPDATE;
        };
      }
    }
    return CdcEventType.UPDATE;
  }

  public String getTisId() {
    if (keys != null && keys.id() != null) {
      return keys.id();
    }
    for (JsonNode op : rawPatch) {
      if ("".equals(op.path("path").asText())) {
        return op.path("value").path("id").asText();
      }
    }
    throw new IllegalArgumentException("Cannot extract TIS ID from patch: " + rawPatch);
  }

  public JsonPatch getPatch() throws IOException {
    return JsonPatch.fromJson(rawPatch);
  }

  public JsonPatch getPatchWithoutTests() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ArrayNode filtered = mapper.createArrayNode();
    for (JsonNode op : rawPatch) {
      if (!"test".equals(op.path("op").asText())) {
        filtered.add(op);
      }
    }
    return JsonPatch.fromJson(filtered);
  }

  public record CdcKeys(String id) {
    // TODO: update lambda to include keys.
  }

}