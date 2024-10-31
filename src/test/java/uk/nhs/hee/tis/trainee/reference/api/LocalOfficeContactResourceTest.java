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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeContactDetailsDto;
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeContactDto;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactMapperImpl;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeContactService;

class LocalOfficeContactResourceTest {

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LOCAL_OFFICE_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_LOCAL_OFFICE_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_CONTACT_TYPE_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_CONTACT_TYPE_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_CONTACT_1 = "https://hee.freshdesk.com/support/home";
  private static final String DEFAULT_CONTACT_2 = "england.ltft.eoe@nhs.net";

  private static final String DEFAULT_LABEL_1 = "Local office name 1";
  private static final String DEFAULT_LABEL_2 = "Local office name 2";

  private static final String DEFAULT_LOCAL_OFFICE_NAME_1 = "Label 1";
  private static final String DEFAULT_LOCAL_OFFICE_NAME_2 = "Label 2";

  private static final String DEFAULT_CONTACT_TYPE_NAME_1 = "Contact type name 1";
  private static final String DEFAULT_CONTACT_TYPE_NAME_2 = "Contact type name 2";

  private LocalOfficeContactResource controller;
  private LocalOfficeContactService service;

  @BeforeEach
  void setup() {
    service = mock(LocalOfficeContactService.class);
    controller = new LocalOfficeContactResource(service, new LocalOfficeContactMapperImpl());
  }

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    LocalOfficeContact entity1 = new LocalOfficeContact();
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    entity1.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    entity1.setContact(DEFAULT_CONTACT_1);

    LocalOfficeContact entity2 = new LocalOfficeContact();
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_2);
    entity2.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_2);
    entity2.setContact(DEFAULT_CONTACT_2);
  }

  @Test
  void shouldGetAllLocalOfficeContacts() {
    LocalOfficeContact entity1 = new LocalOfficeContact();
    entity1.setTisId(DEFAULT_TIS_ID_1);
    entity1.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    entity1.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    entity1.setContact(DEFAULT_CONTACT_1);
    entity1.setLabel(DEFAULT_LABEL_1);
    entity1.setLocalOfficeName(DEFAULT_LOCAL_OFFICE_NAME_1);
    entity1.setContactTypeName(DEFAULT_CONTACT_TYPE_NAME_1);

    LocalOfficeContact entity2 = new LocalOfficeContact();
    entity2.setTisId(DEFAULT_TIS_ID_2);
    entity2.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_2);
    entity2.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_2);
    entity2.setContact(DEFAULT_CONTACT_2);
    entity2.setLabel(DEFAULT_LABEL_2);
    entity2.setLocalOfficeName(DEFAULT_LOCAL_OFFICE_NAME_2);
    entity2.setContactTypeName(DEFAULT_CONTACT_TYPE_NAME_2);

    when(service.get()).thenReturn(List.of(entity1, entity2));

    List<LocalOfficeContactDetailsDto> dtos = controller.getLocalOfficeContacts();

    assertThat("Unexpected response count.", dtos, hasSize(2));

    LocalOfficeContactDetailsDto dto1 = dtos.get(0);
    assertThat("Unexpected TIS ID.", dto1.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected local office ID.", dto1.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_1));
    assertThat("Unexpected contact type ID.", dto1.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_1));
    assertThat("Unexpected contact.", dto1.getContact(), is(DEFAULT_CONTACT_1));
    assertThat("Unexpected label.", dto1.getLabel(), is(DEFAULT_LABEL_1));
    assertThat("Unexpected local office name.", dto1.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_NAME_1));
    assertThat("Unexpected contact type name.", dto1.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_NAME_1));

    LocalOfficeContactDetailsDto dto2 = dtos.get(1);
    assertThat("Unexpected TIS ID.", dto2.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected local office ID.", dto2.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_2));
    assertThat("Unexpected contact type ID.", dto2.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_2));
    assertThat("Unexpected contact.", dto2.getContact(), is(DEFAULT_CONTACT_2));
    assertThat("Unexpected label.", dto2.getLabel(), is(DEFAULT_LABEL_2));
    assertThat("Unexpected local office name.", dto2.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_NAME_2));
    assertThat("Unexpected contact type name.", dto2.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_NAME_2));
  }

  @Test
  void shouldGetLocalOfficeContactsByLoUuid() {
    LocalOfficeContact entity = new LocalOfficeContact();
    entity.setTisId(DEFAULT_TIS_ID_1);
    entity.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    entity.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    entity.setContact(DEFAULT_CONTACT_1);
    entity.setLabel(DEFAULT_LABEL_1);
    entity.setLocalOfficeName(DEFAULT_LOCAL_OFFICE_NAME_1);
    entity.setContactTypeName(DEFAULT_CONTACT_TYPE_NAME_1);

    when(service.getByLocalOfficeUuid(DEFAULT_LOCAL_OFFICE_ID_1)).thenReturn(List.of(entity));

    List<LocalOfficeContactDetailsDto> dtos = controller.getLocalOfficeContactsByLoUuid(
        DEFAULT_LOCAL_OFFICE_ID_1);

    assertThat("Unexpected response count.", dtos, hasSize(1));

    LocalOfficeContactDetailsDto dto1 = dtos.get(0);
    assertThat("Unexpected TIS ID.", dto1.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected local office ID.", dto1.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_1));
    assertThat("Unexpected contact type ID.", dto1.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_1));
    assertThat("Unexpected contact.", dto1.getContact(), is(DEFAULT_CONTACT_1));
    assertThat("Unexpected label.", dto1.getLabel(), is(DEFAULT_LABEL_1));
    assertThat("Unexpected local office name.", dto1.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_NAME_1));
    assertThat("Unexpected contact type name.", dto1.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_NAME_1));
  }

  @Test
  void shouldGetLocalOfficeContactsByLoName() {
    LocalOfficeContact entity = new LocalOfficeContact();
    entity.setTisId(DEFAULT_TIS_ID_1);
    entity.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    entity.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    entity.setContact(DEFAULT_CONTACT_1);
    entity.setLabel(DEFAULT_LABEL_1);
    entity.setLocalOfficeName(DEFAULT_LOCAL_OFFICE_NAME_1);
    entity.setContactTypeName(DEFAULT_CONTACT_TYPE_NAME_1);

    when(service.getByLocalOfficeName(DEFAULT_LOCAL_OFFICE_NAME_1)).thenReturn(List.of(entity));

    List<LocalOfficeContactDetailsDto> dtos = controller.getLocalOfficeContactsByLoName(
        DEFAULT_LOCAL_OFFICE_NAME_1);

    assertThat("Unexpected response count.", dtos, hasSize(1));

    LocalOfficeContactDetailsDto dto1 = dtos.get(0);
    assertThat("Unexpected TIS ID.", dto1.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected local office ID.", dto1.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_1));
    assertThat("Unexpected contact type ID.", dto1.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_1));
    assertThat("Unexpected contact.", dto1.getContact(), is(DEFAULT_CONTACT_1));
    assertThat("Unexpected label.", dto1.getLabel(), is(DEFAULT_LABEL_1));
    assertThat("Unexpected local office name.", dto1.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_NAME_1));
    assertThat("Unexpected contact type name.", dto1.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_NAME_1));
  }

  @Test
  void shouldCreateLocalOfficeContact() {
    LocalOfficeContactDto dto = new LocalOfficeContactDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    dto.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    dto.setContact(DEFAULT_CONTACT_1);

    when(service.create(any())).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<LocalOfficeContactDetailsDto> response = controller.createLocalOfficeContact(
        dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(CREATED));
    assertThat("Unexpected location.", response.getHeaders().getLocation(),
        is(URI.create("/api/local-office-contact")));

    LocalOfficeContactDetailsDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected local office ID.", body.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_1));
    assertThat("Unexpected contact type ID.", body.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_1));
    assertThat("Unexpected contact.", body.getContact(), is(DEFAULT_CONTACT_1));
    assertThat("Unexpected label.", body.getLabel(), nullValue());
    assertThat("Unexpected local office name.", body.getLocalOfficeName(), nullValue());
    assertThat("Unexpected contact type name.", body.getContactTypeName(), nullValue());
  }

  @Test
  void shouldUpdateLocalOfficeContact() {
    LocalOfficeContactDto dto = new LocalOfficeContactDto();
    dto.setTisId(DEFAULT_TIS_ID_1);
    dto.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    dto.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    dto.setContact(DEFAULT_CONTACT_1);

    when(service.update(any())).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<LocalOfficeContactDetailsDto> response = controller.updateLocalOfficeContact(
        dto);

    assertThat("Unexpected status code.", response.getStatusCode(), is(OK));

    LocalOfficeContactDetailsDto body = response.getBody();
    assertThat("Unexpected body.", body, notNullValue());
    assertThat("Unexpected TIS ID.", body.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected local office ID.", body.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_1));
    assertThat("Unexpected contact type ID.", body.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_1));
    assertThat("Unexpected contact.", body.getContact(), is(DEFAULT_CONTACT_1));
    assertThat("Unexpected label.", body.getLabel(), nullValue());
    assertThat("Unexpected local office name.", body.getLocalOfficeName(), nullValue());
    assertThat("Unexpected contact type name.", body.getContactTypeName(), nullValue());
  }

  @Test
  void shouldDeleteLocalOfficeContact() {
    ResponseEntity<Void> response = controller.deleteLocalOfficeContact(DEFAULT_TIS_ID_1);

    assertThat("Unexpected status code.", response.getStatusCode(), is(NO_CONTENT));
    assertThat("Unexpected response body presence.", response.hasBody(), is(false));

    verify(service).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
