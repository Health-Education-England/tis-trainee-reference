package uk.nhs.hee.tis.trainee.reference.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.trainee.reference.model.Qualification;

@Repository
public interface QualificationRepository extends MongoRepository<Qualification, String> {
}
