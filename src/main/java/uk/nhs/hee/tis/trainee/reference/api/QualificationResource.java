package uk.nhs.hee.tis.trainee.reference.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.trainee.reference.dto.QualificationDto;
import uk.nhs.hee.tis.trainee.reference.mapper.QualificationMapper;
import uk.nhs.hee.tis.trainee.reference.model.Qualification;
import uk.nhs.hee.tis.trainee.reference.service.QualificationService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QualificationResource {

  private static final Logger log = LoggerFactory.getLogger(QualificationResource.class);
  @Autowired
  private QualificationService qualificationService;
  @Autowired
  private QualificationMapper qualificationMapper;

  /**
   * Get Qualification options from reference table.
   *
   * @return The {@link Qualification} representing the trainee profile.
   */
  @GetMapping("/qualification")
  public List<QualificationDto> getQualification() {
    log.trace("Get all Qualifications");
    List<Qualification> qualifications = qualificationService.getQualification();
    return qualificationMapper.toDtos(qualifications);
  }
}
