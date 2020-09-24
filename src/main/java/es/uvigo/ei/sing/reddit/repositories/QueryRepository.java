package es.uvigo.ei.sing.reddit.repositories;

import es.uvigo.ei.sing.reddit.entities.QueryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends CrudRepository<QueryEntity, Integer> {
    Iterable<QueryEntity> findByIsSuspendedFalse();
}
