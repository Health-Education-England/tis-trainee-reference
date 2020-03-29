package uk.nhs.hee.tis.trainee.reference.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;
import uk.nhs.hee.tis.trainee.reference.repository.LocalOfficeRepository;
import uk.nhs.hee.tis.trainee.reference.service.LocalOfficeService;

@Service
public class LocalOfficeServiceImpl implements LocalOfficeService {

  @Autowired
  LocalOfficeRepository localOfficeRepository;

  public List<LocalOffice> getLocalOffice()  {
    return localOfficeRepository.findAll();
  }
}
