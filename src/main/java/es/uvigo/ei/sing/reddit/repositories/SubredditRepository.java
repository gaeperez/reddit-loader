package es.uvigo.ei.sing.reddit.repositories;

import es.uvigo.ei.sing.reddit.entities.SubredditEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubredditRepository extends CrudRepository<SubredditEntity, Integer> {
    Optional<SubredditEntity> findByExternalIdAndName(String externalId, String name);
}
