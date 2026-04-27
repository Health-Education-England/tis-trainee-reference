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
import com.github.fge.jsonpatch.AddOperation;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.RemoveOperation;
import java.util.List;

/**
 * TODO: javadoc
 *
 * @param operations
 * @param keys
 */
public record CdcEvent(
    @JsonProperty("patch")
    List<JsonPatchOperation> operations,
    CdcKeys keys) {

  private static final String EMPTY_PATH = "path: \"\"";

  /**
   * TODO: javadoc
   *
   * @return
   */
  public CdcEventType getEventType() {
    return operations.stream()
        .filter(op -> op.toString().endsWith(EMPTY_PATH))
        .findFirst()
        .map(op -> {
          if (op instanceof AddOperation) {
            return CdcEventType.INSERT;
          }

          if (op instanceof RemoveOperation) {
            return CdcEventType.DELETE;
          }

          return null;
        }).orElse(CdcEventType.UPDATE);

  }

  /**
   * TODO: javadoc
   *
   * @return
   */
  public JsonPatch getPatch() {
    return new JsonPatch(operations);
  }

  /**
   * TODO: javadoc
   *
   * @param id
   */
  public record CdcKeys(String id) { // TODO: update lambda to include `id` even if `uuid` present.

  }
}
