package uk.nhs.hee.tis.trainee.reference.api;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.trainee.reference.dto.LocalOfficeDto;
import uk.nhs.hee.tis.trainee.reference.mapper.LocalOfficeMapper;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeService;

@RestController
@RequestMapping("/api")
public class LocalOfficeResource {

  private static final Logger log = LoggerFactory.getLogger(LocalOfficeResource.class);
  @Autowired
  private LocalOfficeService localOfficeService;
  @Autowired
  private LocalOfficeMapper localOfficeMapper;

  /**
   * Get LocalOffice options from reference table.
   *
   * @return list of LocalOffices.
   */
  @GetMapping("/localoffice")
  public List<LocalOfficeDto> getLocalOffice() {
    log.trace("Get all LocalOffices");
    List<LocalOffice> localOffices = localOfficeService.getLocalOffice();
    return localOfficeMapper.toDtos(localOffices);
  }
}
