package es.uvigo.ei.sing.reddit.services;

import es.uvigo.ei.sing.reddit.entities.SubredditEntity;
import es.uvigo.ei.sing.reddit.repositories.SubredditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class SubredditService {

    private final SubredditRepository subredditRepository;

    @Autowired
    public SubredditService(SubredditRepository subredditRepository) {
        this.subredditRepository = subredditRepository;
    }

    public void save(SubredditEntity subredditEntity) {
        subredditRepository.save(subredditEntity);
    }

    public void saveAll(Set<SubredditEntity> subredditEntities) {
        subredditRepository.saveAll(subredditEntities);
    }

    public Optional<SubredditEntity> findByExternalIdAndName(String externalId, String name) {
        return subredditRepository.findByExternalIdAndName(externalId, name);
    }
}
