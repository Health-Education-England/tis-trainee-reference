package uk.nhs.hee.tis.trainee.reference.api;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.trainee.reference.dto.GenderDto;
import uk.nhs.hee.tis.trainee.reference.mapper.GenderMapper;
import uk.nhs.hee.tis.trainee.reference.model.Gender;
import uk.nhs.hee.tis.trainee.reference.service.GenderService;


@RestController
@RequestMapping("/api")
public class GenderResource {

  private static final Logger log = LoggerFactory.getLogger(GenderResource.class);
  @Autowired
  private GenderService genderService;
  @Autowired
  private GenderMapper genderMapper;

  /**
   * Get Gender options from reference table.
   *
   * @return list of Genders.
   */
  @GetMapping("/gender")
  public List<GenderDto> getGender() {
    log.trace("Get all Genders");
    List<Gender> genders = genderService.getGender();
    return genderMapper.toDtos(genders);
  }
}
