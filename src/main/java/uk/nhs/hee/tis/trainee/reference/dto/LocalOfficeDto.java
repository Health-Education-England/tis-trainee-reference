package uk.nhs.hee.tis.trainee.reference.dto;

import lombok.Data;

/**
 * A DTO for LocalOffice entity.
 * Holds all options for LocalOffice.
 */
@Data
public class LocalOfficeDto {

  private String id;
  private String localOfficeTisId;
  private String label;
  private String entityId;
}
