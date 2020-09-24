package es.uvigo.ei.sing.reddit.services;

import es.uvigo.ei.sing.reddit.api.DatabaseHelper;
import es.uvigo.ei.sing.reddit.entities.*;
import es.uvigo.ei.sing.reddit.utils.Constants;
import es.uvigo.ei.sing.reddit.utils.Functions;
import lombok.extern.log4j.Log4j2;
import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.*;
import net.dean.jraw.references.SubredditReference;
import net.dean.jraw.tree.CommentNode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Service
@SuppressWarnings("Duplicates")
public class RedditService extends DatabaseHelper {

    public SubredditEntity runUpdate(String subredditSearch, SubredditSort subredditSort, TimePeriod timePeriod) {
        // Variables to avoid uniqueness duplicity in the same execution (for users and URLs)
        Map<String, UserEntity> mapUsernameUsers = new HashMap<>();
        Map<String, UrlEntity> mapUrlEntity = new HashMap<>();
        SubredditEntity subredditEntity = null;

        try {
            // Obtain and create the subreddit
            log.info(Constants.REDDIT_INFO_SUBREDDIT_START, subredditSearch);
            SubredditReference subredditReference = redditAPI.obtainSubreddit(subredditSearch);
            subredditEntity = createOrRetrieveSubreddit(subredditReference);
            log.info(Constants.REDDIT_INFO_SUBREDDIT_END, subredditSearch);

            if (subredditReference != null) {
                try {
                    // Obtain and create the submissions
                    log.info(Constants.REDDIT_INFO_SUBMISSION_START);
                    List<Submission> submissions = redditAPI.obtainSubmissionsBySubreddit(subredditReference, subredditSort, timePeriod);
                    Set<SubmissionEntity> submissionEntities = new HashSet<>();
                    for (Submission submission : submissions)
                        submissionEntities.add(createOrRetrieveSubmission(submission.getId(),
                                submission.getEdited(), submission.getTitle(), submission.getSelfText(), submission.getScore(),
                                submission.getUrl(), submission.getCreated(), submission.getPermalink(), submission.getAuthor(),
                                submission.getCommentCount(), subredditEntity, mapUsernameUsers, mapUrlEntity));
                    log.info(Constants.REDDIT_INFO_SUBMISSION_END, submissions.size(), subredditSearch);

                    // Obtain and create the submissions
                    obtainCommentsForSubmissions(submissionEntities, subredditEntity, mapUsernameUsers);

                    // Set entities dependencies
                    subredditEntity.setSubmissions(submissionEntities);
                } catch (ApiException | NetworkException e) {
                    log.warn(Constants.REDDIT_WARN_API_SUBMISSION, subredditReference.getSubreddit(), e.getMessage());
                }
            }
        } catch (ApiException | NetworkException e) {
            log.warn(Constants.REDDIT_WARN_API_SUBREDDIT, subredditSearch, e.getMessage());
        }

        return subredditEntity;
    }

    public Set<SubredditEntity> runQuery(String queryToSearch, SearchSort searchSort, TimePeriod timePeriod) {
        // Variables to avoid uniqueness duplicity in the same execution (for users and URLs)
        Map<String, UserEntity> mapUsernameUsers = new HashMap<>();
        Map<String, UrlEntity> mapUrlEntity = new HashMap<>();
        Map<String, SubredditEntity> mapExternalIdSubreddit = new HashMap<>();
        List<Submission> submissions;

        try {
            // Obtain and create the submissions from the query
            log.info(Constants.REDDIT_INFO_SUBMISSION_START);
            submissions = redditAPI.obtainSubmissionsByQuery(queryToSearch, searchSort, timePeriod);
            log.info(Constants.REDDIT_INFO_SUBMISSION_END, submissions.size());

            if (submissions != null && !submissions.isEmpty()) {
                SubredditEntity subredditEntity;
                // Iterate the obtained submissions from the query and get their corresponding subreddit
                for (Submission submission : submissions) {
                    // Obtain and create the subreddit
                    String subredditFullName = submission.getSubredditFullName();
                    String subredditName = submission.getSubreddit();

                    try {
                        log.info(Constants.REDDIT_INFO_SUBREDDIT_START, subredditName);
                        if (mapExternalIdSubreddit.containsKey(subredditFullName)) {
                            subredditEntity = mapExternalIdSubreddit.get(subredditFullName);
                        } else {
                            SubredditReference subredditReference = redditAPI.obtainSubreddit(subredditName);
                            subredditEntity = createOrRetrieveSubreddit(subredditReference);

                            // Add to the map to avoid uniqueness
                            mapExternalIdSubreddit.put(subredditFullName, subredditEntity);
                        }
                        log.info(Constants.REDDIT_INFO_SUBREDDIT_END, subredditName);

                        // Obtain and create the submission for the previous subreddit (only one submission)
                        Set<SubmissionEntity> submissionEntities = new HashSet<>();
                        submissionEntities.add(createOrRetrieveSubmission(submission.getId(), submission.getEdited(),
                                submission.getTitle(), submission.getSelfText(), submission.getScore(), submission.getUrl(),
                                submission.getCreated(), submission.getPermalink(), submission.getAuthor(),
                                submission.getCommentCount(), subredditEntity, mapUsernameUsers, mapUrlEntity));

                        // Obtain and create the comments (only one submission)
                        obtainCommentsForSubmissions(submissionEntities, subredditEntity, mapUsernameUsers);

                        // Set entities dependencies
                        subredditEntity.setSubmissions(submissionEntities);
                    } catch (ApiException | NetworkException e) {
                        log.warn(Constants.REDDIT_WARN_API_SUBREDDIT, subredditName, e.getMessage());
                    }
                }
            }
        } catch (ApiException | NetworkException e) {
            log.warn(Constants.REDDIT_WARN_API_QUERY, queryToSearch, e.getMessage());
        }

        return new HashSet<>(mapExternalIdSubreddit.values());
    }

    private void obtainCommentsForSubmissions(Set<SubmissionEntity> submissionEntities, SubredditEntity subredditEntity,
                                              Map<String, UserEntity> mapUsernameUsers) {
        // Obtain and create the comments (only one submission)
        for (SubmissionEntity submissionEntity : submissionEntities) {
            // TODO: 29/01/2019 Use a flag or sth to check all the comments anyway for possible edits
            // Check if there are new comments to save
            if (submissionEntity.getCommentCount() > submissionEntity.getCommentCountOld()) {
                try {
                    // TODO: 30/01/2019 Bottleneck with a lot of comments
                    log.info(Constants.REDDIT_INFO_COMMENT_START);

                    // Obtain comments from API
                    Iterator<CommentNode<PublicContribution<?>>> commentNodes = redditAPI.obtainComments(submissionEntity.getExternalId());
                    log.info(Constants.REDDIT_INFO_COMMENT_RETRIEVED);

                    // Create or retrieve comments from database and set them to the submission
                    submissionEntity.setComments(createOrRetrieveComments(commentNodes, subredditEntity, submissionEntity, mapUsernameUsers));

                    log.info(Constants.REDDIT_INFO_COMMENT_END, submissionEntity.getComments().size(), submissionEntity.getExternalUniqueId());
                } catch (ApiException | NetworkException e) {
                    log.warn(Constants.REDDIT_WARN_API_COMMENT, submissionEntity.getExternalId(), e.getMessage());
                }
            }
        }
    }

    private Set<CommentEntity> createOrRetrieveComments(Iterator<CommentNode<PublicContribution<?>>> it,
                                                        SubredditEntity subredditEntity,
                                                        SubmissionEntity submissionEntity,
                                                        Map<String, UserEntity> mapUsernameUsers) {
        Map<String, CommentEntity> mapExternalIdComment = new HashMap<>();
        Set<CommentEntity> commentEntities = new HashSet<>();

        while (it.hasNext()) {
            // Get comment nodes (like a JSON structure)
            CommentNode<PublicContribution<?>> commentNode = it.next();
            // Get the PublicContribution (may be a Submission or a Comment)
            PublicContribution<?> comment = commentNode.getSubject();

            int depth = commentNode.getDepth();
            // If depth == 0 then it is the submission (already obtained)
            if (depth > 0) {
                CommentEntity commentEntity;
                String externalId = comment.getId();
                LocalDateTime editDate = Functions.convertToLocalDateTime(comment.getEdited());

                // Check if comment already exists in the database
                Optional<CommentEntity> possibleSavedComment = commentService.findByExternalId(externalId);
                if (possibleSavedComment.isPresent()) {
                    commentEntity = possibleSavedComment.get();
                    log.debug(Constants.REDDIT_DEBUG_COMMENT, commentEntity.getExternalUniqueId());

                    // Check the edit date and update the contents if needed
                    if (Functions.hasChangesToSave(commentEntity.getEdited(), editDate)) {
                        // Set the changed values
                        commentEntity.setText(comment.getBody());
                        commentEntity.setScore(comment.getScore());
                        commentEntity.setEdited(editDate);
                    }
                } else {
                    // Create the comment
                    commentEntity = new CommentEntity();
                    commentEntity.setExternalId(externalId);
                    commentEntity.setType(Constants.REDDIT_T1_COMMENT);
                    commentEntity.setText(comment.getBody());
                    commentEntity.setCreated(Functions.convertToLocalDateTime(comment.getCreated()));
                    commentEntity.setEdited(editDate);
                    commentEntity.setDepth(depth);
                    commentEntity.setScore(comment.getScore());

                    // Save the user for the submission
                    // It is not necessary to set Subreddit, Submissions and Comments in the url because it is done by cascade
                    Account userAccount = redditAPI.obtainUserAccount(comment.getAuthor());
                    UserEntity userEntity = obtainUser(comment.getAuthor(), userAccount, mapUsernameUsers);
                    // Set the values for the parent relations
                    commentEntity.setUser(userEntity);
                    subredditEntity.getUsers().add(userEntity);

                    // Set submission
                    commentEntity.setSubmission(submissionEntity);
                }

                // Set replies (only if depth != 1) and parent is not null. Depth = 1 imply that the parent is the submission
                // If parent is not null the comment was already considered in a previous execution.
                if (depth != 1 && commentEntity.getComment() == null) {
                    // Get parent from map of comments
                    String parentId = commentNode.getParent().getSubject().getFullName();
                    CommentEntity parent = mapExternalIdComment.get(parentId);

                    // Set parent and replies
                    commentEntity.setComment(parent);
                    parent.getReplies().add(commentEntity);
                }

                mapExternalIdComment.put(comment.getFullName(), commentEntity);

                // Add comment
                commentEntities.add(commentEntity);
            }
        }

        return commentEntities;
    }
}
