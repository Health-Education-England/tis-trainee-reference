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
import com.github.fge.jsonpatch.JsonPatch;
import java.io.IOException;
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

  /**
   * Determines the type of CDC event by inspecting the operation at the root path.
   *
   * <p>An {@code add} at root indicates an INSERT, a {@code remove} at root indicates a DELETE,
   * and any other operation at root indicates an UPDATE. If no root-path operation is found,
   * UPDATE is assumed.
   *
   * @return The {@link CdcEventType} for this event.
   */
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

  /**
   * Extracts the TIS ID for the record affected by this event.
   *
   * <p>Prefers {@code keys.id()} if populated by the lambda. Falls back to extracting the
   * {@code id} field from the root operation's value in the patch.
   *
   * @return The TIS ID.
   * @throws IllegalArgumentException if the TIS ID cannot be determined.
   */
  public String getTisId() {
    if (keys != null && keys.id() != null) {
      return keys.id();
    }
    for (JsonNode op : rawPatch) {
      if ("".equals(op.path("path").asText())) {
        // remove operations (DELETE) have no value to extract an ID from
        if ("remove".equals(op.path("op").asText())) {
          throw new IllegalArgumentException(
              "Cannot extract TIS ID for DELETE event without keys: " + rawPatch);
        }
        return op.path("value").path("id").asText();
      }
    }
    throw new IllegalArgumentException("Cannot extract TIS ID from patch: " + rawPatch);
  }

  /**
   * Constructs a {@link JsonPatch} from all patch operations.
   *
   * @return A JsonPatch representing all CDC patch operations.
   * @throws IOException if the patch cannot be constructed.
   */
  public JsonPatch getPatch() throws IOException {
    return JsonPatch.fromJson(rawPatch);
  }

  /**
   * Constructs a {@link JsonPatch} from the patch operations, excluding any {@code test}
   * operations.
   *
   * <p>Test operations verify the before-state of a field using TIS column names, which may
   * differ from TSS field names. Stripping them avoids patch failures caused by field name
   * mismatches.
   *
   * @return A JsonPatch with test operations removed.
   * @throws IOException if the patch cannot be constructed.
   */
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

  /**
   * Represents the key fields used to identify a CDC record.
   *
   * @param id The TIS id of the record.
   */
  public record CdcKeys(String id) {
    // TODO: update lambda to include keys.
  }

}