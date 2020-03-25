package uk.nhs.hee.tis.trainee.reference.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.model.Qualification;
import uk.nhs.hee.tis.trainee.reference.repository.QualificationRepository;
import uk.nhs.hee.tis.trainee.reference.service.QualificationService;

import java.util.List;

@Service
public class QualificationServiceImpl implements QualificationService {

  @Autowired
  QualificationRepository qualificationRepository;

  public List<Qualification> getQualification()  {
    return qualificationRepository.findAll();
  }
}
