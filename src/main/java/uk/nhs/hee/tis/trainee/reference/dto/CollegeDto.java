package uk.nhs.hee.tis.trainee.reference.dto;

import lombok.Data;

/**
 * A DTO for College entity. Holds all options for College.
 */
@Data
public class CollegeDto {

  private String id;
  private String collegeTisId;
  private String label;
}
