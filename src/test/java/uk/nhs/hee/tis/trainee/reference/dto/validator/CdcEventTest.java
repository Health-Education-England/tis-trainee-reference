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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CdcEventTest {

  private static final String TIS_ID = "abc-123";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private JsonNode insertPatch;
  private JsonNode updatePatch;
  private JsonNode deletePatch;
  private JsonNode updatePatchWithTest;

  @BeforeEach
  void setUp() throws IOException {
    insertPatch = MAPPER.readTree("""
        [{"op":"add","path":"","value":{"id":"%s","name":"Test"}}]
        """.formatted(TIS_ID));

    updatePatch = MAPPER.readTree("""
        [{"op":"replace","path":"","value":{"id":"%s","name":"Updated"}}]
        """.formatted(TIS_ID));

    deletePatch = MAPPER.readTree("""
        [{"op":"remove","path":""}]
        """);

    updatePatchWithTest = MAPPER.readTree("""
        [
          {"op":"test","path":"/name","value":"Original"},
          {"op":"replace","path":"","value":{"id":"%s","name":"Updated"}}
        ]
        """.formatted(TIS_ID));
  }

  @Test
  void shouldReturnInsertEventType() {
    CdcEvent event = new CdcEvent(insertPatch, null, null);
    assertThat("Unexpected event type.", event.getEventType(), is(CdcEventType.INSERT));
  }

  @Test
  void shouldReturnUpdateEventType() {
    CdcEvent event = new CdcEvent(updatePatch, null, null);
    assertThat("Unexpected event type.", event.getEventType(), is(CdcEventType.UPDATE));
  }

  @Test
  void shouldReturnDeleteEventType() {
    CdcEvent event = new CdcEvent(deletePatch, null, null);
    assertThat("Unexpected event type.", event.getEventType(), is(CdcEventType.DELETE));
  }

  @Test
  void shouldReturnUpdateEventTypeWhenNoRootOperation() throws IOException {
    JsonNode patch = MAPPER.readTree("""
        [{"op":"replace","path":"/name","value":"Updated"}]
        """);
    CdcEvent event = new CdcEvent(patch, null, null);
    assertThat("Unexpected event type.", event.getEventType(), is(CdcEventType.UPDATE));
  }

  @Test
  void shouldGetTisIdFromKeys() {
    CdcEvent.CdcKeys keys = new CdcEvent.CdcKeys(TIS_ID);
    CdcEvent event = new CdcEvent(updatePatch, keys, null);
    assertThat("Unexpected TIS ID.", event.getTisId(), is(TIS_ID));
  }

  @Test
  void shouldGetTisIdFromPatchValueWhenKeysNull() {
    CdcEvent event = new CdcEvent(updatePatch, null, null);
    assertThat("Unexpected TIS ID.", event.getTisId(), is(TIS_ID));
  }

  @Test
  void shouldGetTisIdFromPatchValueWhenKeysIdNull() {
    CdcEvent.CdcKeys keys = new CdcEvent.CdcKeys(null);
    CdcEvent event = new CdcEvent(updatePatch, keys, null);
    assertThat("Unexpected TIS ID.", event.getTisId(), is(TIS_ID));
  }

  @Test
  void shouldThrowWhenDeleteEventHasNoKeys() {
    CdcEvent event = new CdcEvent(deletePatch, null, null);
    assertThrows(IllegalArgumentException.class, event::getTisId);
  }

  @Test
  void shouldGetPatch() throws IOException, JsonPatchException {
    CdcEvent event = new CdcEvent(updatePatch, null, null);
    JsonPatch patch = event.getPatch();

    JsonNode target = MAPPER.readTree("{}");
    JsonNode result = patch.apply(target);

    assertThat("Unexpected name.", result.path("name").asText(), is("Updated"));
    assertThat("Unexpected id.", result.path("id").asText(), is(TIS_ID));
  }

  @Test
  void shouldGetPatchWithoutTests() throws IOException, JsonPatchException {
    CdcEvent event = new CdcEvent(updatePatchWithTest, null, null);
    JsonPatch patch = event.getPatchWithoutTests();

    JsonNode target = MAPPER.readTree("{}");
    JsonNode result = patch.apply(target);

    assertThat("Unexpected name.", result.path("name").asText(), is("Updated"));
    assertThat("Unexpected id.", result.path("id").asText(), is(TIS_ID));
  }

  @Test
  void shouldIncludeAllOperationsWhenNoTestsPresent() throws IOException, JsonPatchException {
    CdcEvent event = new CdcEvent(updatePatch, null, null);
    JsonPatch patch = event.getPatchWithoutTests();

    JsonNode target = MAPPER.readTree("{}");
    JsonNode result = patch.apply(target);

    assertThat("Unexpected name.", result.path("name").asText(), is("Updated"));
    assertThat("Unexpected id.", result.path("id").asText(), is(TIS_ID));
  }
}