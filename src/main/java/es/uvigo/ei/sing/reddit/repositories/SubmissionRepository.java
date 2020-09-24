package es.uvigo.ei.sing.reddit.repositories;

import es.uvigo.ei.sing.reddit.entities.SubmissionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends CrudRepository<SubmissionEntity, Integer> {
    Optional<SubmissionEntity> findByExternalId(String externalId);
}
