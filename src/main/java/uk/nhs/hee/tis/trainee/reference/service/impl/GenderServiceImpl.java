package uk.nhs.hee.tis.trainee.reference.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.model.Gender;
import uk.nhs.hee.tis.trainee.reference.repository.GenderRepository;
import uk.nhs.hee.tis.trainee.reference.service.GenderService;

@Service
public class GenderServiceImpl implements GenderService {

  @Autowired
  GenderRepository genderRepository;

  public List<Gender> getGender() {
    return genderRepository.findAll();
  }
}
