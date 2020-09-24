package es.uvigo.ei.sing.reddit.services;

import es.uvigo.ei.sing.reddit.entities.SubmissionEntity;
import es.uvigo.ei.sing.reddit.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Optional<SubmissionEntity> findByExternalId(String externalId) {
        return submissionRepository.findByExternalId(externalId);
    }
}
