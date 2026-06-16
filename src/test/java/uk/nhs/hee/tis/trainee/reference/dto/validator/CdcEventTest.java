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

package uk.nhs.hee.tis.trainee.reference.dto.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.AddOperation;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchOperation;
import com.github.fge.jsonpatch.RemoveOperation;
import com.github.fge.jsonpatch.ReplaceOperation;
import com.github.fge.jsonpatch.TestOperation;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.nhs.hee.tis.trainee.reference.dto.CdcEvent;
import uk.nhs.hee.tis.trainee.reference.dto.CdcEventType;

class CdcEventTest {

  private static final String TIS_ID = "abc-123";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private List<JsonPatchOperation> insertPatch;
  private List<JsonPatchOperation> updatePatch;
  private List<JsonPatchOperation> deletePatch;
  private List<JsonPatchOperation> updatePatchWithTest;

  @BeforeEach
  void setUp() throws IOException, JsonPointerException {
    String patchTemplate = """
        {
          "id": "%s",
          "name": "%s"
        }
        """;

    insertPatch = List.of(
        new AddOperation(new JsonPointer(""),
            MAPPER.readTree(patchTemplate.formatted(TIS_ID, "Test"))));

    updatePatch = List.of(
        new ReplaceOperation(new JsonPointer(""),
            MAPPER.readTree(patchTemplate.formatted(TIS_ID, "Updated"))));

    deletePatch = List.of(
        new RemoveOperation(new JsonPointer("")));

    updatePatchWithTest = List.of(
        new TestOperation(new JsonPointer("/name"),
            MAPPER.readTree("\"Original\"")),
        new ReplaceOperation(new JsonPointer(""),
            MAPPER.readTree(patchTemplate.formatted(TIS_ID, "Updated"))));
  }

  @Test
  void shouldReturnInsertEventType() {
    CdcEvent event = new CdcEvent(insertPatch, null);
    assertThat("Unexpected event type.", event.getEventType(), is(CdcEventType.INSERT));
  }

  @Test
  void shouldReturnUpdateEventType() {
    CdcEvent event = new CdcEvent(updatePatch, null);
    assertThat("Unexpected event type.", event.getEventType(), is(CdcEventType.UPDATE));
  }

  @Test
  void shouldReturnDeleteEventType() {
    CdcEvent event = new CdcEvent(deletePatch, null);
    assertThat("Unexpected event type.", event.getEventType(), is(CdcEventType.DELETE));
  }

  @Test
  void shouldReturnUpdateEventTypeWhenNoRootOperation() throws IOException, JsonPointerException {
    List<JsonPatchOperation> patch = List.of(
        new ReplaceOperation(new JsonPointer("/name"),
            MAPPER.readTree("\"Updated\"")));
    CdcEvent event = new CdcEvent(patch, null);
    assertThat("Unexpected event type.", event.getEventType(), is(CdcEventType.UPDATE));
  }

  @Test
  void shouldGetPatch() throws JsonPatchException {
    CdcEvent event = new CdcEvent(updatePatch, null);
    JsonPatch patch = event.getPatch();

    JsonNode target = MAPPER.createObjectNode();
    JsonNode result = patch.apply(target);

    assertThat("Unexpected name.", result.path("name").asText(), is("Updated"));
    assertThat("Unexpected id.", result.path("id").asText(), is(TIS_ID));
  }

  @Test
  void shouldGetPatchWithoutTests() throws JsonPatchException {
    CdcEvent event = new CdcEvent(updatePatchWithTest, null);
    JsonPatch patch = event.getPatchWithoutTests();

    JsonNode target = MAPPER.createObjectNode();
    JsonNode result = patch.apply(target);

    assertThat("Unexpected name.", result.path("name").asText(), is("Updated"));
  }

  @Test
  void shouldIncludeAllOperationsWhenNoTestsPresent() throws JsonPatchException {
    CdcEvent event = new CdcEvent(updatePatch, null);
    JsonPatch patch = event.getPatchWithoutTests();

    JsonNode target = MAPPER.createObjectNode();
    JsonNode result = patch.apply(target);

    assertThat("Unexpected name.", result.path("name").asText(), is("Updated"));
    assertThat("Unexpected id.", result.path("id").asText(), is(TIS_ID));
  }
}
