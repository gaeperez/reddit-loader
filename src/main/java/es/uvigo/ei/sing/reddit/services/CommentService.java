package es.uvigo.ei.sing.reddit.services;

import es.uvigo.ei.sing.reddit.entities.CommentEntity;
import es.uvigo.ei.sing.reddit.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Optional<CommentEntity> findByExternalId(String externalId) {
        return commentRepository.findByExternalId(externalId);
    }
}
