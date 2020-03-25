package uk.nhs.hee.tis.trainee.reference.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.trainee.reference.dto.CollegeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.CollegeMapper;
import uk.nhs.hee.tis.trainee.reference.model.College;
import uk.nhs.hee.tis.trainee.reference.service.CollegeService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CollegeResource {

  private static final Logger log = LoggerFactory.getLogger(CollegeResource.class);
  @Autowired
  private CollegeService collegeService;
  @Autowired
  private CollegeMapper collegeMapper;

  /**
   * Get College options from reference table.
   *
   * @return list of Colleges.
   */
  @GetMapping("/college")
  public List<CollegeDto> getCollege() {
    log.trace("Get all College");
    List<College> colleges = collegeService.getCollege();
    return collegeMapper.toDtos(colleges);
  }
}
