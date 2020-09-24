package es.uvigo.ei.sing.reddit.repositories;

import es.uvigo.ei.sing.reddit.entities.CommentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<CommentEntity, Integer> {
    Optional<CommentEntity> findByExternalId(String externalId);
}
