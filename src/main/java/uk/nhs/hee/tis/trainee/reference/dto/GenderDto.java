package uk.nhs.hee.tis.trainee.reference.dto;

import lombok.Data;

/**
 * A DTO for Gender entity. Holds all options for gender.
 */
@Data
public class GenderDto {

  private String id;
  private String genderTisId;
  private String label;
}
