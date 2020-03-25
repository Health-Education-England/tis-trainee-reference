package uk.nhs.hee.tis.trainee.reference.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "College")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class College {

  @Id
  private String id;

  @Indexed(unique = true)
  @Field(value = "collegeTisId")
  private String collegeTisId;
  private String label;
}
