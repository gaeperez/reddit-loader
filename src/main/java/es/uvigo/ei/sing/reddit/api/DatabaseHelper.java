package es.uvigo.ei.sing.reddit.api;

import es.uvigo.ei.sing.reddit.api.jraw.RedditAPI;
import es.uvigo.ei.sing.reddit.entities.SubmissionEntity;
import es.uvigo.ei.sing.reddit.entities.SubredditEntity;
import es.uvigo.ei.sing.reddit.entities.UrlEntity;
import es.uvigo.ei.sing.reddit.entities.UserEntity;
import es.uvigo.ei.sing.reddit.services.*;
import es.uvigo.ei.sing.reddit.utils.Constants;
import es.uvigo.ei.sing.reddit.utils.Functions;
import lombok.extern.log4j.Log4j2;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.references.SubredditReference;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Log4j2
public abstract class DatabaseHelper {

    @Autowired
    protected SubredditService subredditService;
    @Autowired
    protected SubmissionService submissionService;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UrlService urlService;
    @Autowired
    protected RedditAPI redditAPI;

    public void renewToken() {
        redditAPI.renewToken();
    }

    protected SubredditEntity createOrRetrieveSubreddit(SubredditReference subredditReference) {
        // Create Subreddit entity
        SubredditEntity subredditEntity;

        // Obtain the subreddit object
        Subreddit subreddit = subredditReference.about();

        // Obtain unique variables
        String externalId = subreddit.getId();
        String name = subreddit.getName();

        // Check in DB if the subreddit already exists
        Optional<SubredditEntity> possibleSavedSubreddit = subredditService.findByExternalIdAndName(externalId, name);
        if (possibleSavedSubreddit.isPresent()) {
            // If exists, retrieve it
            subredditEntity = possibleSavedSubreddit.get();
            log.debug(Constants.REDDIT_DEBUG_SUBREDDIT, subredditEntity.getPermalink());
        } else {
            // Otherwise, create it
            subredditEntity = new SubredditEntity();
            subredditEntity.setExternalId(externalId);
            subredditEntity.setType(Constants.REDDIT_T5_SUBREDDIT);
            subredditEntity.setName(name);
            if (subreddit.getTitle() != null)
                subredditEntity.setTitle(subreddit.getTitle());
            if (subreddit.getPublicDescription() != null)
                subredditEntity.setDescription(subreddit.getPublicDescription());
            if (subreddit.getSidebar() != null)
                subredditEntity.setSidebarDescription(subreddit.getSidebar());
            if (subreddit.getCreated() != null)
                subredditEntity.setCreated(Functions.convertToLocalDateTime(subreddit.getCreated()));
            if (subreddit.getUrl() != null)
                subredditEntity.setPermalink(subreddit.getUrl());
            if (subreddit.getAccountsActive() != null)
                subredditEntity.setSubscribedUsers(subreddit.getAccountsActive());
        }

        return subredditEntity;
    }

    protected SubmissionEntity createOrRetrieveSubmission(String submissionId, Date submissionEdited,
                                                          String submissionTitle, String submissionSelfText,
                                                          Integer submissionScore, String submissionUrl,
                                                          Date submissionCreated, String submissionPermalink,
                                                          String submissionAuthor, Integer submissionCommentCount,
                                                          SubredditEntity subredditEntity,
                                                          Map<String, UserEntity> mapUsernameUsers,
                                                          Map<String, UrlEntity> mapUrlEntity) {
        SubmissionEntity submissionEntity = null;
        LocalDateTime submissionEditDate = Functions.convertToLocalDateTime(submissionEdited);

        try {
            // Check if the submission externalId is already in the database
            Optional<SubmissionEntity> possibleSavedSubmission = submissionService.findByExternalId(submissionId);
            if (possibleSavedSubmission.isPresent()) {
                submissionEntity = possibleSavedSubmission.get();
                log.debug(Constants.REDDIT_DEBUG_SUBMISSION, submissionEntity.getExternalUniqueId());

                // Check the edit date and update the contents if needed
                if (Functions.hasChangesToSave(submissionEntity.getEdited(), submissionEditDate)) {
                    // Set the changed values
                    submissionEntity.setTitle(submissionTitle);
                    submissionEntity.setText(submissionSelfText);
                    submissionEntity.setScore(submissionScore);
                    submissionEntity.setEdited(submissionEditDate);

                    // Set URLs
                    UrlEntity urlEntity = obtainUrl(submissionUrl, mapUrlEntity);
                    urlEntity.getSubmissions().add(submissionEntity);

                    submissionEntity.getUrls().add(urlEntity);
                }
            } else {
                // Submission is not save in the DB, create it
                submissionEntity = new SubmissionEntity();
                submissionEntity.setExternalId(submissionId);
                submissionEntity.setType(Constants.REDDIT_T3_SUBMISSION);
                submissionEntity.setTitle(submissionTitle);
                submissionEntity.setText(submissionSelfText);
                submissionEntity.setScore(submissionScore);
                submissionEntity.setCreated(Functions.convertToLocalDateTime(submissionCreated));
                submissionEntity.setEdited(submissionEditDate);
                submissionEntity.setPermalink(submissionPermalink);

                // Save the user for the submission.
                // It is not necessary to set Submissions and Subreddit in the user because it is done by cascade
                Account userAccount = redditAPI.obtainUserAccount(submissionAuthor);
                UserEntity userEntity = obtainUser(submissionAuthor, userAccount, mapUsernameUsers);
                // Set the values for the parent relations
                submissionEntity.setUser(userEntity);
                subredditEntity.getUsers().add(userEntity);

                // Set URLs
                // It is not necessary to set Submissions in the url because it is done by cascade
                UrlEntity urlEntity = obtainUrl(submissionUrl, mapUrlEntity);
                // Set the values for the parent relations
                submissionEntity.getUrls().add(urlEntity);

                // Set subreddit
                submissionEntity.setSubreddit(subredditEntity);
            }

            // Always update this information
            // Set number of comments
            submissionEntity.setCommentCountOld(submissionEntity.getCommentCount());
            submissionEntity.setCommentCount(submissionCommentCount);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            log.error(submissionId);
        }

        return submissionEntity;
    }

    protected UserEntity obtainUser(String username, Account userAccount, Map<String, UserEntity> mapUsernameUsers) {
        UserEntity userEntity;

        if (mapUsernameUsers.containsKey(username)) {
            userEntity = mapUsernameUsers.get(username);
            log.debug(Constants.REDDIT_DEBUG_USER, userEntity.getUsername());
        } else {
            userEntity = userService.createOrRetrieveUser(username, userAccount);

            // Save user in the map to avoid duplicates
            mapUsernameUsers.put(username, userEntity);
        }

        return userEntity;
    }

    private UrlEntity obtainUrl(String url, Map<String, UrlEntity> mapUrlEntity) {
        UrlEntity urlEntity;

        if (mapUrlEntity.containsKey(url)) {
            urlEntity = mapUrlEntity.get(url);
            log.debug(Constants.REDDIT_DEBUG_URL, urlEntity.getComplete());
        } else {
            urlEntity = urlService.createOrRetrieveUrl(url);

            // Save user in the map to avoid duplicates
            mapUrlEntity.put(url, urlEntity);
        }

        return urlEntity;
    }
}
