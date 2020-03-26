package uk.nhs.hee.tis.trainee.reference.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.trainee.reference.model.LocalOffice;

@Repository
public interface LocalOfficeRepository extends MongoRepository<LocalOffice, String> {

}
