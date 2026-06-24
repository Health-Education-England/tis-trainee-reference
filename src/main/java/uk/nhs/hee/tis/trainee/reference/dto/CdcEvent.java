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
import com.github.fge.jsonpatch.AddOperation;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.RemoveOperation;
import com.github.fge.jsonpatch.ReplaceOperation;
import com.github.fge.jsonpatch.TestOperation;
import java.util.List;

/**
 * Represents a CDC patch event received from an SQS queue.
 *
 * @param operations The list of JSON patch operations.
 * @param keys       The key fields identifying the record, populated by the lambda.
 */
public record CdcEvent(
    @JsonProperty("patch")
    List<JsonPatchOperation> operations,
    CdcKeys keys) {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * Determines the type of CDC event by inspecting the operation at the root path.
   *
   * <p>An {@link AddOperation} at root indicates an INSERT, a {@link RemoveOperation} at root
   * indicates a DELETE, and any other operation at root indicates an UPDATE. If no root-path
   * operation is found, UPDATE is assumed.
   *
   * @return The {@link CdcEventType} for this event.
   */
  public CdcEventType getEventType() {
    return operations.stream()
        .filter(op -> {
          try {
            JsonNode opNode = MAPPER.valueToTree(op);
            return "".equals(opNode.path("path").asText());
          } catch (Exception e) {
            return false;
          }
        })
        .findFirst()
        .map(op -> {
          if (op instanceof AddOperation) {
            return CdcEventType.INSERT;
          }
          if (op instanceof RemoveOperation) {
            return CdcEventType.DELETE;
          }
          return CdcEventType.UPDATE;
        })
        .orElse(CdcEventType.UPDATE);
  }

  /**
   * Constructs a {@link JsonPatch} from all patch operations.
   *
   * @return A JsonPatch representing all CDC patch operations.
   */
  public JsonPatch getPatch() {
    return new JsonPatch(operations);
  }

  /**
   * Constructs a {@link JsonPatch} from the patch operations, excluding any {@link TestOperation}s.
   *
   * <p>Test operations verify the before-state of a field using TIS column names, which may
   * differ from TSS field names. Stripping them avoids patch failures caused by field name
   * mismatches.
   *
   * @return A JsonPatch with test operations removed.
   */
  public JsonPatch getPatchWithoutTests() {
    List<JsonPatchOperation> filtered = operations.stream()
        .filter(op -> !(op instanceof TestOperation))
        .toList();
    return new JsonPatch(filtered);
  }

  /**
   * Determines whether the CDC event represents an inactive record.
   *
   * <p>Returns true if the root-path add or replace operation's value contains a
   * {@code status} field equal to {@code INACTIVE}.
   *
   * @return true if the record status is INACTIVE, false otherwise.
   */
  public boolean isInactive() {
    return operations.stream()
        .filter(op -> op instanceof ReplaceOperation || op instanceof AddOperation)
        .map(op -> (JsonNode) MAPPER.valueToTree(op))
        .filter(node -> node.path("path").asText().equals(""))
        .map(node -> node.path("value").path("status").asText(null))
        .anyMatch("INACTIVE"::equals);
  }

  /**
   * Extracts the TIS ID from the root-path add or replace operation's value.
   *
   * <p>Returns the {@code id} field from the patch value, or null if no root-path
   * add or replace operation is present (e.g. for delete events).
   *
   * @return The TIS ID, or null if not present in the patch.
   */
  public String getTisId() {
    return operations.stream()
        .filter(op -> op instanceof ReplaceOperation || op instanceof AddOperation)
        .map(op -> (JsonNode) MAPPER.valueToTree(op))
        .filter(node -> node.path("path").asText().equals(""))
        .map(node -> node.path("value").path("id").asText(null))
        .findFirst()
        .orElse(null);
  }

  /**
   * Represents the key fields used to identify a CDC record.
   *
   * @param uuid The TIS UUID of the record.
   */
  public record CdcKeys(String uuid) {}
}
