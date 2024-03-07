package uk.nhs.hee.tis.trainee.reference.facade;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContactType;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeContactTypeRepository;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeRepository;

@ExtendWith(MockitoExtension.class)
class LocalOfficeContactEnricherFacadeTest {
  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_LOCAL_OFFICE_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_CONTACT_TYPE_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_CONTACT_1 = "https://hee.freshdesk.com/support/home";
  private static final String DEFAULT_LOCAL_OFFICE_1 = "Health Education England South London";
  private static final String DEFAULT_CONTACT_TYPE_1 = "Less Than Full Time";

  private LocalOfficeContact localOfficeContact1;

  private LocalOfficeContactEnricherFacade facade;

  @Mock
  private LocalOfficeRepository localOfficeRepository;

  @Mock
  private LocalOfficeContactTypeRepository contactTypeRepository;

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    facade = new LocalOfficeContactEnricherFacade(localOfficeRepository, contactTypeRepository);

    localOfficeContact1 = new LocalOfficeContact();
    localOfficeContact1.setTisId(DEFAULT_TIS_ID_1);
    localOfficeContact1.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    localOfficeContact1.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    localOfficeContact1.setContact(DEFAULT_CONTACT_1);
  }

  @Test
  void enrichShouldUpdateLocalOfficeContact() {
    LocalOffice localOffice = new LocalOffice();
    localOffice.setUuid(DEFAULT_LOCAL_OFFICE_ID_1);
    localOffice.setLabel(DEFAULT_LOCAL_OFFICE_1);
    when(localOfficeRepository.findByUuid(DEFAULT_LOCAL_OFFICE_ID_1)).thenReturn(localOffice);

    LocalOfficeContactType contactType = new LocalOfficeContactType();
    contactType.setTisId(DEFAULT_CONTACT_TYPE_ID_1);
    contactType.setLabel(DEFAULT_CONTACT_TYPE_1);
    when(contactTypeRepository.findByTisId(DEFAULT_CONTACT_TYPE_ID_1)).thenReturn(contactType);

    facade.enrich(localOfficeContact1);

    assertThat("Unexpected local office name", localOfficeContact1.getLocalOfficeName(),
        is(DEFAULT_LOCAL_OFFICE_1));
    assertThat("Unexpected contact type name", localOfficeContact1.getContactTypeName(),
        is(DEFAULT_CONTACT_TYPE_1));
    assertThat("Unexpected label", localOfficeContact1.getLabel(),
        is(facade.generateLabel(localOfficeContact1)));
  }

  @Test
  void enrichWithLocalOfficeShouldUpdateLocalOfficeContact() {

    facade.enrichWithLocalOffice(localOfficeContact1, "a local office name");

    assertThat("Unexpected local office name", localOfficeContact1.getLocalOfficeName(),
        is("a local office name"));
    assertThat("Unexpected label", localOfficeContact1.getLabel(),
        is(facade.generateLabel(localOfficeContact1)));
  }

  @Test
  void enrichWithContactTypeShouldUpdateLocalOfficeContact() {

    facade.enrichWithContactType(localOfficeContact1, "a contact type name");

    assertThat("Unexpected contact type name", localOfficeContact1.getContactTypeName(),
        is("a contact type name"));
    assertThat("Unexpected label", localOfficeContact1.getLabel(),
        is(facade.generateLabel(localOfficeContact1)));
  }

  @Test
  void enrichWithLocalOfficeAndContactTypeShouldUpdateLocalOfficeContact() {

    facade.enrichWithLocalOfficeAndContactType(localOfficeContact1, "a local office name",
        "a contact type name");

    assertThat("Unexpected local office name", localOfficeContact1.getLocalOfficeName(),
        is("a local office name"));
    assertThat("Unexpected contact type name", localOfficeContact1.getContactTypeName(),
        is("a contact type name"));
    assertThat("Unexpected label", localOfficeContact1.getLabel(),
        is(facade.generateLabel(localOfficeContact1)));
  }

  @Test
  void enrichShouldReturnNullLocalOfficeContact() {
    LocalOfficeContact enriched = facade.enrich(null);
    assertNull(enriched, "Unexpected local office contact");
  }

  @Test
  void enrichWithLocalOfficeShouldReturnNullLocalOfficeContact() {
    LocalOfficeContact enriched = facade.enrichWithLocalOffice(null, "any name");
    assertNull(enriched, "Unexpected local office contact");
  }

  @Test
  void enrichWithContactTypeShouldReturnNullLocalOfficeContact() {
    LocalOfficeContact enriched = facade.enrichWithContactType(null, "any name");
    assertNull(enriched, "Unexpected local office contact");
  }

  @Test
  void enrichShouldIgnoreMissingLocalOfficeRecord() {
    when(localOfficeRepository.findByUuid(any())).thenReturn(null);

    LocalOfficeContactType contactType = new LocalOfficeContactType();
    contactType.setTisId(DEFAULT_CONTACT_TYPE_ID_1);
    contactType.setLabel(DEFAULT_CONTACT_TYPE_1);
    when(contactTypeRepository.findByTisId(DEFAULT_CONTACT_TYPE_ID_1)).thenReturn(contactType);

    facade.enrich(localOfficeContact1);

    assertNull(localOfficeContact1.getLocalOfficeName(), "Unexpected local office name");
    assertThat("Unexpected label", localOfficeContact1.getLabel(),
        is(facade.generateLabel(localOfficeContact1)));
  }

  @Test
  void enrichShouldIgnoreMissingContactTypeRecord() {
    LocalOffice localOffice = new LocalOffice();
    localOffice.setUuid(DEFAULT_LOCAL_OFFICE_ID_1);
    localOffice.setLabel(DEFAULT_LOCAL_OFFICE_1);
    when(localOfficeRepository.findByUuid(DEFAULT_LOCAL_OFFICE_ID_1)).thenReturn(localOffice);

    when(contactTypeRepository.findByTisId(any())).thenReturn(null);

    facade.enrich(localOfficeContact1);

    assertNull(localOfficeContact1.getContactTypeName(), "Unexpected contact type name");
    assertThat("Unexpected label", localOfficeContact1.getLabel(),
        is(facade.generateLabel(localOfficeContact1)));
  }

  @Test
  void enrichShouldIgnoreMissingLocalOfficeId() {
    localOfficeContact1.setLocalOfficeId(null);

    LocalOfficeContactType contactType = new LocalOfficeContactType();
    contactType.setTisId(DEFAULT_CONTACT_TYPE_ID_1);
    contactType.setLabel(DEFAULT_CONTACT_TYPE_1);
    when(contactTypeRepository.findByTisId(DEFAULT_CONTACT_TYPE_ID_1)).thenReturn(contactType);

    facade.enrich(localOfficeContact1);

    assertNull(localOfficeContact1.getLocalOfficeName(), "Unexpected local office name");
    assertThat("Unexpected label", localOfficeContact1.getLabel(),
        is(facade.generateLabel(localOfficeContact1)));
  }

  @Test
  void enrichShouldIgnoreMissingContactType() {
    localOfficeContact1.setContactTypeId(null);

    LocalOffice localOffice = new LocalOffice();
    localOffice.setUuid(DEFAULT_LOCAL_OFFICE_ID_1);
    localOffice.setLabel(DEFAULT_LOCAL_OFFICE_1);
    when(localOfficeRepository.findByUuid(DEFAULT_LOCAL_OFFICE_ID_1)).thenReturn(localOffice);

    facade.enrich(localOfficeContact1);

    assertNull(localOfficeContact1.getContactTypeName(), "Unexpected contact type name");
    assertThat("Unexpected label", localOfficeContact1.getLabel(),
        is(facade.generateLabel(localOfficeContact1)));
  }

  @Test
  void generateLabelShouldCreateLabelWithAllDetails() {
    localOfficeContact1.setLocalOfficeName(DEFAULT_LOCAL_OFFICE_1);
    localOfficeContact1.setContactTypeName(DEFAULT_CONTACT_TYPE_1);
    String expectedLabel = DEFAULT_CONTACT_1 + " (" + DEFAULT_CONTACT_TYPE_1 + ") - "
        + DEFAULT_LOCAL_OFFICE_1;
    assertThat("Unexpected label.", facade.generateLabel(localOfficeContact1),
        is(expectedLabel));
  }

  @Test
  void generateLabelShouldCreateLabelWithoutContactType() {
    localOfficeContact1.setLocalOfficeName(DEFAULT_LOCAL_OFFICE_1);
    String expectedLabel = DEFAULT_CONTACT_1 + " - " + DEFAULT_LOCAL_OFFICE_1;
    assertThat("Unexpected label.", facade.generateLabel(localOfficeContact1),
        is(expectedLabel));
  }

  @Test
  void generateLabelShouldCreateLabelWithoutLocalOffice() {
    localOfficeContact1.setContactTypeName(DEFAULT_CONTACT_TYPE_1);
    String expectedLabel = DEFAULT_CONTACT_1 + " (" + DEFAULT_CONTACT_TYPE_1 + ")";
    assertThat("Unexpected label.", facade.generateLabel(localOfficeContact1),
        is(expectedLabel));
  }
}
