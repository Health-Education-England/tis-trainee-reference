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

package uk.nhs.hee.tis.trainee.reference.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.nhs.hee.tis.trainee.reference.facade.LocalOfficeContactEnricherFacade;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactRepository;

@ExtendWith(MockitoExtension.class)
class LocalOfficeContactServiceTest {

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LOCAL_OFFICE_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_LOCAL_OFFICE_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_CONTACT_TYPE_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_CONTACT_TYPE_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_CONTACT_1 = "https://hee.freshdesk.com/support/home";
  private static final String DEFAULT_CONTACT_2 = "england.ltft.eoe@nhs.net";

  private static final String DEFAULT_LOCAL_OFFICE_1 = "South London";
  private static final String DEFAULT_LOCAL_OFFICE_2 = "Thames Valley";

  private static final String DEFAULT_CONTACT_TYPE_1 = "Less Than Full Time";
  private static final String DEFAULT_CONTACT_TYPE_2 = "Deferral";

  private static final String DEFAULT_LABEL_1 = "Label 1";
  private static final String DEFAULT_LABEL_2 = "Label 2";

  private LocalOfficeContactService service;

  @Mock
  private LocalOfficeContactRepository repository;

  @Mock
  private LocalOfficeContactEnricherFacade facade;

  private LocalOfficeContact localOfficeContact1;
  private LocalOfficeContact localOfficeContact2;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    service = new LocalOfficeContactService(repository,
        Mappers.getMapper(LocalOfficeContactMapper.class), facade);

    localOfficeContact1 = new LocalOfficeContact();
    localOfficeContact1.setTisId(DEFAULT_TIS_ID_1);
    localOfficeContact1.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    localOfficeContact1.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    localOfficeContact1.setContact(DEFAULT_CONTACT_1);
    localOfficeContact1.setLocalOfficeName(DEFAULT_LOCAL_OFFICE_1);
    localOfficeContact1.setContactTypeName(DEFAULT_CONTACT_TYPE_1);
    localOfficeContact1.setLabel(DEFAULT_LABEL_1);

    localOfficeContact2 = new LocalOfficeContact();
    localOfficeContact2.setTisId(DEFAULT_TIS_ID_2);
    localOfficeContact2.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_2);
    localOfficeContact2.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_2);
    localOfficeContact2.setContact(DEFAULT_CONTACT_2);
    localOfficeContact2.setLocalOfficeName(DEFAULT_LOCAL_OFFICE_2);
    localOfficeContact2.setContactTypeName(DEFAULT_CONTACT_TYPE_2);
    localOfficeContact2.setLabel(DEFAULT_LABEL_2);
  }

  @Test
  void getAllLocalOfficeContactShouldReturnAllLocalOfficeContacts() {
    List<LocalOfficeContact> localOfficeContacts = new ArrayList<>();
    localOfficeContacts.add(localOfficeContact1);
    localOfficeContacts.add(localOfficeContact2);
    when(repository.findAll(Sort.by("label"))).thenReturn(localOfficeContacts);

    List<LocalOfficeContact> allLocalOfficeContacts = service.get();

    assertThat("Unexpected size of returned LocalOfficeContact list",
        allLocalOfficeContacts.size(), equalTo(localOfficeContacts.size()));
    assertThat("The returned local office contact list doesn't contain the expected "
        + "local office contact", allLocalOfficeContacts, hasItem(localOfficeContact1));
  }

  @Test
  void getLocalOfficeContactByLocalOfficeUuidShouldReturnCorrectLocalOfficeContacts() {
    List<LocalOfficeContact> localOfficeContacts = new ArrayList<>();
    localOfficeContacts.add(localOfficeContact1);
    when(repository.findByLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1)).thenReturn(localOfficeContacts);

    List<LocalOfficeContact> foundLocalOfficeContacts
        = service.getByLocalOfficeUuid(DEFAULT_LOCAL_OFFICE_ID_1);

    assertThat("Unexpected size of returned LocalOfficeContact list",
        foundLocalOfficeContacts.size(), equalTo(localOfficeContacts.size()));
    assertThat("The returned local office contact list doesn't contain the expected "
        + "local office contact", foundLocalOfficeContacts, hasItem(localOfficeContact1));
  }

  @Test
  void getLocalOfficeContactByLocalOfficeNameShouldReturnCorrectLocalOfficeContacts() {
    List<LocalOfficeContact> localOfficeContacts = new ArrayList<>();
    localOfficeContacts.add(localOfficeContact1);
    when(repository.findByLocalOfficeName(DEFAULT_LOCAL_OFFICE_1)).thenReturn(localOfficeContacts);

    List<LocalOfficeContact> foundLocalOfficeContacts
        = service.getByLocalOfficeName(DEFAULT_LOCAL_OFFICE_1);

    assertThat("Unexpected size of returned LocalOfficeContact list",
        foundLocalOfficeContacts.size(), equalTo(localOfficeContacts.size()));
    assertThat("The returned local office contact list doesn't contain the expected "
        + "local office contact", foundLocalOfficeContacts, hasItem(localOfficeContact1));
  }

  @Test
  void createLocalOfficeContactShouldCreateLocalOfficeContactWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(LocalOfficeContact.class))).thenAnswer(returnsFirstArg());
    when(facade.enrich(localOfficeContact2)).thenReturn(localOfficeContact2);

    LocalOfficeContact localOfficeContact = service.create(localOfficeContact2);

    verify(facade).enrich(localOfficeContact2);

    assertThat("Unexpected TIS id.", localOfficeContact.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected local office id.", localOfficeContact.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_2));
    assertThat("Unexpected contact type id.", localOfficeContact.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_2));
    assertThat("Unexpected contact.", localOfficeContact.getContact(),
        is(DEFAULT_CONTACT_2));
    assertThat("Unexpected local office name.", localOfficeContact.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_2));
    assertThat("Unexpected contact type name.", localOfficeContact.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_2));
    assertThat("Unexpected label.", localOfficeContact.getLabel(),
        is(DEFAULT_LABEL_2));
  }

  @Test
  void createLocalOfficeContactShouldUpdateLocalOfficeContactWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(localOfficeContact1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());
    when(facade.enrich(localOfficeContact2)).thenReturn(localOfficeContact2);

    LocalOfficeContact localOfficeContact = service.create(localOfficeContact2);

    verify(facade, atLeastOnce()).enrich(localOfficeContact2);

    assertThat("Unexpected TIS id.", localOfficeContact.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected local office id.", localOfficeContact.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_2));
    assertThat("Unexpected contact type id.", localOfficeContact.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_2));
    assertThat("Unexpected contact.", localOfficeContact.getContact(),
        is(DEFAULT_CONTACT_2));
    assertThat("Unexpected local office name.", localOfficeContact.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_2));
    assertThat("Unexpected contact type name.", localOfficeContact.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_2));
    assertThat("Unexpected label.", localOfficeContact.getLabel(),
        is(DEFAULT_LABEL_2));
  }

  @Test
  void updateLocalOfficeContactShouldCreateLocalOfficeContactWhenNewTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(null);
    when(repository.insert(any(LocalOfficeContact.class))).thenAnswer(returnsFirstArg());
    when(facade.enrich(localOfficeContact2)).thenReturn(localOfficeContact2);

    LocalOfficeContact localOfficeContact = service.update(localOfficeContact2);

    verify(facade, atLeastOnce()).enrich(localOfficeContact2);

    assertThat("Unexpected TIS id.", localOfficeContact.getTisId(), is(DEFAULT_TIS_ID_2));
    assertThat("Unexpected local office id.", localOfficeContact.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_2));
    assertThat("Unexpected contact type id.", localOfficeContact.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_2));
    assertThat("Unexpected contact.", localOfficeContact.getContact(),
        is(DEFAULT_CONTACT_2));
    assertThat("Unexpected local office name.", localOfficeContact.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_2));
    assertThat("Unexpected contact type name.", localOfficeContact.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_2));
    assertThat("Unexpected label.", localOfficeContact.getLabel(),
        is(DEFAULT_LABEL_2));
  }

  @Test
  void updateLocalOfficeContactShouldUpdateLocalOfficeContactWhenExistingTisId() {
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(localOfficeContact1);
    when(repository.save(any())).thenAnswer(returnsFirstArg());
    when(facade.enrich(localOfficeContact2)).thenReturn(localOfficeContact2);

    LocalOfficeContact localOfficeContact = service.update(localOfficeContact2);

    verify(facade).enrich(localOfficeContact2);

    assertThat("Unexpected TIS id.", localOfficeContact.getTisId(), is(DEFAULT_TIS_ID_1));
    assertThat("Unexpected local office id.", localOfficeContact.getLocalOfficeId(),
        is(DEFAULT_LOCAL_OFFICE_ID_2));
    assertThat("Unexpected contact type id.", localOfficeContact.getContactTypeId(),
        is(DEFAULT_CONTACT_TYPE_ID_2));
    assertThat("Unexpected contact.", localOfficeContact.getContact(),
        is(DEFAULT_CONTACT_2));
    assertThat("Unexpected local office name.", localOfficeContact.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_2));
    assertThat("Unexpected contact type name.", localOfficeContact.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_2));
    assertThat("Unexpected label.", localOfficeContact.getLabel(),
        is(DEFAULT_LABEL_2));
  }

  @Test
  void shouldDeleteLocalOfficeContactByTisId() {
    service.deleteByTisId(DEFAULT_TIS_ID_1);

    verify(repository).deleteByTisId(DEFAULT_TIS_ID_1);
  }

  @Test
  void updateAllForLocalOfficeShouldUpdateAllRelevantContacts() {
    List<LocalOfficeContact> localOfficeContacts = new ArrayList<>();
    localOfficeContacts.add(localOfficeContact1);
    localOfficeContact2.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    localOfficeContacts.add(localOfficeContact2);

    LocalOffice localOffice = new LocalOffice();
    localOffice.setUuid(DEFAULT_LOCAL_OFFICE_ID_1);
    localOffice.setLabel("some local office");

    when(repository.save(any())).thenAnswer(returnsFirstArg());
    when(repository.findByLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1)).thenReturn(localOfficeContacts);
    when(repository.findByTisId(DEFAULT_TIS_ID_1)).thenReturn(localOfficeContact1);
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(localOfficeContact2);
    when(facade.enrichWithLocalOffice(any(), any())).thenAnswer(returnsFirstArg());

    service.updateAllForLocalOffice(localOffice);

    verify(repository).save(localOfficeContact1);
    verify(repository).save(localOfficeContact2);
  }

  @Test
  void updateAllForLocalOfficeShouldUpdateNothingIfLocalOfficeEmpty() {

    service.updateAllForLocalOffice(new LocalOffice());

    verify(repository, never()).save(any());
  }

  @Test
  void updateAllForContactTypeShouldUpdateAllRelevantContacts() {
    List<LocalOfficeContact> localOfficeContacts = new ArrayList<>();
    localOfficeContacts.add(localOfficeContact1);
    localOfficeContact2.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    localOfficeContacts.add(localOfficeContact2);

    LocalOfficeContactType contactType = new LocalOfficeContactType();
    contactType.setTisId(DEFAULT_CONTACT_TYPE_ID_1);
    contactType.setLabel("some label");

    when(repository.save(any())).thenAnswer(returnsFirstArg());
    when(repository.findByContactTypeId(DEFAULT_CONTACT_TYPE_ID_1)).thenReturn(localOfficeContacts);
    when(repository.findByTisId(DEFAULT_TIS_ID_1)).thenReturn(localOfficeContact1);
    when(repository.findByTisId(DEFAULT_TIS_ID_2)).thenReturn(localOfficeContact2);
    when(facade.enrichWithContactType(any(), any())).thenAnswer(returnsFirstArg());

    service.updateAllForContactType(contactType);

    verify(repository).save(localOfficeContact1);
    verify(repository).save(localOfficeContact2);
  }

  @Test
  void updateAllForContactTypeShouldUpdateNothingIfContactTypeEmpty() {

    service.updateAllForContactType(new LocalOfficeContactType());

    verify(repository, never()).save(any());
  }
}
