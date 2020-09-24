package es.uvigo.ei.sing.reddit.services;

import es.uvigo.ei.sing.reddit.entities.UserEntity;
import es.uvigo.ei.sing.reddit.repositories.UserRepository;
import es.uvigo.ei.sing.reddit.utils.Functions;
import net.dean.jraw.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity createOrRetrieveUser(String username, Account userAccount) {
        UserEntity userEntity = null;

        Optional<UserEntity> possibleSavedUser = findByUsername(username);
        if (possibleSavedUser.isPresent())
            userEntity = possibleSavedUser.get();
        else {
            userEntity = new UserEntity();
            userEntity.setUsername(username);
            if (userAccount != null) {
                userEntity.setCreated(Functions.convertToLocalDateTime(userAccount.getCreated()));
                userEntity.setModerator(userAccount.isModerator());
                boolean hasVerifiedEmail = userAccount.getHasVerifiedEmail() != null ? userAccount.getHasVerifiedEmail() : true;
                userEntity.setHasVerifiedEmail(hasVerifiedEmail);
                userEntity.setCommentKarma(userAccount.getCommentKarma());
                userEntity.setLinkKarma(userAccount.getLinkKarma());
            }
        }

        return userEntity;
    }
}
