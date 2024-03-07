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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactMapper;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeContactTypeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOfficeContact;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeContactService;

@ContextConfiguration(classes = {LocalOfficeContactTypeMapper.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(LocalOfficeContactResource.class)
class LocalOfficeContactResourceTest {

  private static final String DEFAULT_TIS_ID_1 = "1";
  private static final String DEFAULT_TIS_ID_2 = "2";

  private static final String DEFAULT_LOCAL_OFFICE_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_LOCAL_OFFICE_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_CONTACT_TYPE_ID_1 = UUID.randomUUID().toString();
  private static final String DEFAULT_CONTACT_TYPE_ID_2 = UUID.randomUUID().toString();

  private static final String DEFAULT_CONTACT_1 = "https://hee.freshdesk.com/support/home";
  private static final String DEFAULT_CONTACT_2 = "england.ltft.eoe@nhs.net";

  private static final String DEFAULT_LOCAL_OFFICE_NAME_1 = "Local office name";

  @Autowired
  private MappingJackson2HttpMessageConverter jacksonMessageConverter;

  @Autowired
  private ObjectMapper mapper;

  private MockMvc mockMvc;

  @MockBean
  private LocalOfficeContactService localOfficeContactServiceMock;

  private LocalOfficeContact contact1;
  private LocalOfficeContact contact2;

  /**
   * Set up mocks before each test.
   */
  @BeforeEach
  void setup() {
    LocalOfficeContactMapper mapper = Mappers.getMapper(LocalOfficeContactMapper.class);
    LocalOfficeContactResource localOfficeResource
        = new LocalOfficeContactResource(localOfficeContactServiceMock, mapper);
    mockMvc = MockMvcBuilders.standaloneSetup(localOfficeResource)
        .setMessageConverters(jacksonMessageConverter)
        .build();
  }

  /**
   * Set up data.
   */
  @BeforeEach
  void initData() {
    contact1 = new LocalOfficeContact();
    contact1.setTisId(DEFAULT_TIS_ID_1);
    contact1.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_1);
    contact1.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_1);
    contact1.setContact(DEFAULT_CONTACT_1);

    contact2 = new LocalOfficeContact();
    contact2.setTisId(DEFAULT_TIS_ID_2);
    contact2.setLocalOfficeId(DEFAULT_LOCAL_OFFICE_ID_2);
    contact2.setContactTypeId(DEFAULT_CONTACT_TYPE_ID_2);
    contact2.setContact(DEFAULT_CONTACT_2);
  }

  @Test
  void testGetAllLocalOfficeContacts() throws Exception {
    List<LocalOfficeContact> contacts = new ArrayList<>();
    contacts.add(contact1);
    contacts.add(contact2);
    when(localOfficeContactServiceMock.get()).thenReturn(contacts);
    this.mockMvc.perform(get("/api/local-office-contact")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(2)))
        .andExpect(jsonPath("$.[*].tisId").value(hasItem(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.[*].tisId").value(hasItem(DEFAULT_TIS_ID_2)));
  }

  @Test
  void testGetLocalOfficeContactsByLoUuid() throws Exception {
    List<LocalOfficeContact> contacts = new ArrayList<>();
    contacts.add(contact1);
    when(localOfficeContactServiceMock.getByLocalOfficeUuid(DEFAULT_LOCAL_OFFICE_ID_1))
        .thenReturn(contacts);
    this.mockMvc.perform(get("/api/local-office-contact-by-lo-uuid/{loUuid}",
            DEFAULT_LOCAL_OFFICE_ID_1)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(1)))
        .andExpect(jsonPath("$.[*].tisId").value(hasItem(DEFAULT_TIS_ID_1)));
  }

  @Test
  void testGetLocalOfficeContactsByLoName() throws Exception {
    List<LocalOfficeContact> contacts = new ArrayList<>();
    contacts.add(contact1);
    when(localOfficeContactServiceMock.getByLocalOfficeName(DEFAULT_LOCAL_OFFICE_NAME_1))
        .thenReturn(contacts);
    this.mockMvc.perform(get("/api/local-office-contact-by-lo-name/{loName}",
            DEFAULT_LOCAL_OFFICE_NAME_1)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(hasSize(1)))
        .andExpect(jsonPath("$.[*].tisId").value(hasItem(DEFAULT_TIS_ID_1)));
  }

  @Test
  void testCreateLocalOfficeContact() throws Exception {
    when(localOfficeContactServiceMock.create(contact1)).thenReturn(contact1);

    mockMvc.perform(post("/api/local-office-contact")
            .content(mapper.writeValueAsBytes(contact1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.tisId").value(is(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.localOfficeId").value(is(DEFAULT_LOCAL_OFFICE_ID_1)))
        .andExpect(jsonPath("$.contactTypeId").value(is(DEFAULT_CONTACT_TYPE_ID_1)))
        .andExpect(jsonPath("$.contact").value(is(DEFAULT_CONTACT_1)));
  }

  @Test
  void testUpdateLocalOfficeContact() throws Exception {
    when(localOfficeContactServiceMock.update(contact1)).thenReturn(contact1);

    mockMvc.perform(put("/api/local-office-contact")
            .content(mapper.writeValueAsBytes(contact1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.tisId").value(is(DEFAULT_TIS_ID_1)))
        .andExpect(jsonPath("$.localOfficeId").value(is(DEFAULT_LOCAL_OFFICE_ID_1)))
        .andExpect(jsonPath("$.contactTypeId").value(is(DEFAULT_CONTACT_TYPE_ID_1)))
        .andExpect(jsonPath("$.contact").value(is(DEFAULT_CONTACT_1)));
  }

  @Test
  void testDeleteLocalOfficeContact() throws Exception {
    mockMvc.perform(delete("/api/local-office-contact/{tisId}", DEFAULT_TIS_ID_1)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());

    verify(localOfficeContactServiceMock).deleteByTisId(DEFAULT_TIS_ID_1);
  }
}
