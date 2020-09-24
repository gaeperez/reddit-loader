package es.uvigo.ei.sing.reddit.services;

import es.uvigo.ei.sing.reddit.entities.QueryEntity;
import es.uvigo.ei.sing.reddit.repositories.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class QueryService {

    private final QueryRepository queryRepository;

    @Autowired
    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public void save(QueryEntity queryEntity) {
        queryRepository.save(queryEntity);
    }

    public void saveAll(Set<QueryEntity> queryEntities) {
        queryRepository.saveAll(queryEntities);
    }

    public Optional<QueryEntity> findById(int id) {
        return queryRepository.findById(id);
    }

    public Iterable<QueryEntity> findAll() {
        return queryRepository.findAll();
    }

    public Iterable<QueryEntity> findByIsSuspendedFalse() {
        return queryRepository.findByIsSuspendedFalse();
    }
}
