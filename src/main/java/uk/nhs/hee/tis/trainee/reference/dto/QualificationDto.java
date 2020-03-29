package uk.nhs.hee.tis.trainee.reference.dto;

import lombok.Data;

/**
 * A DTO for Qualification entity.
 * Holds all options for Qualification.
 */
@Data
public class QualificationDto {

  private String id;
  private String qualificationTisId;
  private String label;
}
