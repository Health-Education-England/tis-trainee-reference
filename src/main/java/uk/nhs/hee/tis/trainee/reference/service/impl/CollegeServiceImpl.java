package uk.nhs.hee.tis.trainee.reference.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.trainee.reference.model.College;
import uk.nhs.hee.tis.trainee.reference.repository.CollegeRepository;
import uk.nhs.hee.tis.trainee.reference.service.CollegeService;

import java.util.List;

@Service
public class CollegeServiceImpl implements CollegeService {

  @Autowired
  CollegeRepository collegeRepository;

  public List<College> getCollege()  {
    return collegeRepository.findAll();
  }
}
