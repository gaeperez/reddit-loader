package es.uvigo.ei.sing.reddit.services;

import es.uvigo.ei.sing.reddit.api.DatabaseHelper;
import es.uvigo.ei.sing.reddit.api.pushshift.PushshiftAPI;
import es.uvigo.ei.sing.reddit.entities.*;
import es.uvigo.ei.sing.reddit.entities.pushshift.*;
import es.uvigo.ei.sing.reddit.utils.Constants;
import es.uvigo.ei.sing.reddit.utils.Functions;
import lombok.extern.log4j.Log4j2;
import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Account;
import net.dean.jraw.references.SubredditReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@SuppressWarnings("Duplicates")
public class PushshiftService extends DatabaseHelper {

    private final PushshiftAPI pushshiftAPI;

    @Autowired
    public PushshiftService(PushshiftAPI pushshiftAPI) {
        super();

        this.pushshiftAPI = pushshiftAPI;
    }

    public Set<SubredditEntity> runPSComments(String urlQuery) {
        Set<SubredditEntity> subredditEntities = new HashSet<>();

        // Decompose URL query parameters
        log.info(Constants.PS_INFO_DECOMPOSE, urlQuery);
        Map<String, String> mapUrlParameters = Functions.decomposePSParameters(urlQuery);
        log.debug(mapUrlParameters);

        // Get comments from Pushshift
        DataCommentJson commentJson = pushshiftAPI.getComments(mapUrlParameters);
        if (commentJson != null && !commentJson.getData().isEmpty()) {
            // Variables to avoid uniqueness duplicity in the same execution (for users and URLs)
            Map<String, SubredditEntity> mapExternalIdSubreddit = new HashMap<>();
            Map<String, UserEntity> mapUsernameUsers = new HashMap<>();
            Map<String, UrlEntity> mapUrlEntity = new HashMap<>();

            // Get submissions IDs from comments and remove duplicates
            Set<String> submissionIDs = commentJson.getData().stream().map(CommentJson::getLinkId)
                    .collect(Collectors.toSet());

            // Split the IDs based on the URL character limit
            List<String> splitIDs = Functions.splitIDsInSublists(submissionIDs);
            for (String splitID : splitIDs) {
                // Get submissions data
                log.info(Constants.PS_INFO_SUBMISSIONS, splitID);
                DataSubmissionJson submissionJson = pushshiftAPI.getSubmissions(splitID);

                if (submissionJson != null && !submissionJson.getData().isEmpty())
                    // Execute the core method
                    subredditEntities.addAll(runPS(submissionJson.getData(), mapExternalIdSubreddit, mapUsernameUsers, mapUrlEntity));
            }
        }

        return subredditEntities;
    }

    public Set<SubredditEntity> runPSSubmission(String urlQuery) {
        Set<SubredditEntity> subredditEntities = new HashSet<>();

        // Decompose URL query parameters
        log.info(Constants.PS_INFO_DECOMPOSE, urlQuery);
        Map<String, String> mapUrlParameters = Functions.decomposePSParameters(urlQuery);
        log.debug(mapUrlParameters);

        // Get Submissions from Pushshift
        log.info(Constants.REDDIT_INFO_SUBMISSION_START);
        DataSubmissionJson submissionJson = pushshiftAPI.getSubmissions(mapUrlParameters);
        if (submissionJson != null && !submissionJson.getData().isEmpty())
            subredditEntities.addAll(runPS(submissionJson.getData(), new HashMap<>(), new HashMap<>(), new HashMap<>()));

        return subredditEntities;
    }

    private Set<SubredditEntity> runPS(List<SubmissionJson> submissions, Map<String, SubredditEntity> mapExternalIdSubreddit,
                                       Map<String, UserEntity> mapUsernameUsers, Map<String, UrlEntity> mapUrlEntity) {
        SubredditEntity subredditEntity;

        for (SubmissionJson submission : submissions) {
            // Obtain and create the subreddit
            String subredditFullName = submission.getSubredditId();
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

                // Obtain and create the submission for the previous subreddit
                SubmissionEntity submissionEntity = createOrRetrieveSubmission(submission.getId(), Functions.minimumDate(),
                        submission.getTitle(), submission.getSelftext(), submission.getScore(), submission.getUrl(),
                        Functions.unixTimestampToDate(submission.getCreatedUtc()), submission.getPermalink(), submission.getAuthor(),
                        submission.getNumComments(), subredditEntity, mapUsernameUsers, mapUrlEntity);

                // Check if there are new comments to save
                if (submissionEntity.getCommentCount() > submissionEntity.getCommentCountOld()) {
                    log.info(Constants.REDDIT_INFO_COMMENT_START);

                    // Get all comment ids from the Submission
                    DataCommentIdsJson commentIDsJson = pushshiftAPI.getCommentIds(submission.getId());
                    if (commentIDsJson != null && !commentIDsJson.getCommentIds().isEmpty()) {
                        // Variables needed to keep tracking of each comment depth
                        Set<CommentEntity> commentEntities = new HashSet<>();
                        Map<String, String> mapCommentIdParentId = new HashMap<>();
                        Map<String, CommentEntity> mapExternalIdComment = new HashMap<>();

                        // Split the IDs based on the URL character limit
                        Set<String> commentIds = new HashSet<>(commentIDsJson.getCommentIds());
                        List<String> splitIDs = Functions.splitIDsInSublists(commentIds);
                        for (String splitID : splitIDs) {
                            // Get comment JSON from PS
                            DataCommentJson commentsJson = pushshiftAPI.getComments(splitID);
                            if (commentsJson != null && !commentsJson.getData().isEmpty()) {
                                // Get comment information
                                List<CommentJson> comments = commentsJson.getData();
                                // Create or retrieve comments from database
                                commentEntities.addAll(createOrRetrieveComments(comments, subredditEntity,
                                        submissionEntity, mapUsernameUsers, mapCommentIdParentId, mapExternalIdComment));
                            }
                        }
                        // Calculate the depth for the totality of the comments
                        calculateCommentsDepth(submissionEntity, commentEntities, mapCommentIdParentId, mapExternalIdComment);

                        // Set comments to submission
                        submissionEntity.setComments(commentEntities);
                    }

                    log.info(Constants.REDDIT_INFO_COMMENT_END, submissionEntity.getComments().size(), submissionEntity.getExternalUniqueId());
                }

                // Set entities dependencies
                subredditEntity.getSubmissions().add(submissionEntity);
            } catch (ApiException | NetworkException e) {
                log.warn(Constants.REDDIT_WARN_API_SUBREDDIT, subredditName, e.getMessage());
            }
        }

        return new HashSet<>(mapExternalIdSubreddit.values());
    }

    private Set<CommentEntity> createOrRetrieveComments(List<CommentJson> comments, SubredditEntity subredditEntity,
                                                        SubmissionEntity submissionEntity,
                                                        Map<String, UserEntity> mapUsernameUsers,
                                                        Map<String, String> mapCommentIdParentId,
                                                        Map<String, CommentEntity> mapExternalIdComment) {
        Set<CommentEntity> commentEntities = new HashSet<>();

        // Iterate comments to get main information
        for (CommentJson comment : comments) {
            CommentEntity commentEntity;

            // Get links ids from comment (e.g. t3_bwnk3k)
            String commentParentId = comment.getParentId();
            String externalId = comment.getId();
            LocalDateTime editDate = Functions.minimumLocalDateTime();

            mapCommentIdParentId.put(Constants.REDDIT_T1_COMMENT + externalId, commentParentId);

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
                commentEntity.setCreated(Functions.unixTimestampToLocalDateTime(comment.getCreatedUtc()));
                commentEntity.setEdited(editDate);
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

            mapExternalIdComment.put(Constants.REDDIT_T1_COMMENT + externalId, commentEntity);

            // Add comment
            commentEntities.add(commentEntity);
        }

        return commentEntities;
    }

    private void calculateCommentsDepth(SubmissionEntity submissionEntity, Set<CommentEntity> commentEntities,
                                        Map<String, String> mapCommentIdParentId,
                                        Map<String, CommentEntity> mapExternalIdComment) {
        // Iterate comments to calculate their depth (PS API doesn't give this value)
        String submissionLinkId = Constants.REDDIT_T3_SUBMISSION + submissionEntity.getExternalId();
        int depth;
        for (CommentEntity commentEntity : commentEntities) {
            String commentExternalId = commentEntity.getExternalUniqueId();

            // Calculate depth
            depth = calculateDepth(submissionLinkId, commentExternalId, mapCommentIdParentId);
            commentEntity.setDepth(depth);

            // Set replies (only if depth != 1) and parent is not null. Depth = 1 imply that the parent is the submission
            // If parent is not null the comment was already considered in a previous execution
            if (depth != 1 && commentEntity.getComment() == null) {
                // Get parent from map of comments
                String parentId = mapCommentIdParentId.get(commentExternalId);
                CommentEntity parent = mapExternalIdComment.get(parentId);

                // Set parent and replies
                commentEntity.setComment(parent);
                parent.getReplies().add(commentEntity);
            }
        }
    }

    private int calculateDepth(String submissionLinkId, String commentExternalId, Map<String, String> mapCommentIdParentId) {
        int depth = 0;

        // The stop condition is when the submissionLinkId is equals to the current commentExternalId AND the parent exists
        // If the comment is a child of a dead parent, keep the accumulated depth
        if (!submissionLinkId.equalsIgnoreCase(commentExternalId) && mapCommentIdParentId.containsKey(commentExternalId)) {
            depth++;
            String newCommentExternalId = mapCommentIdParentId.get(commentExternalId);
            depth += calculateDepth(submissionLinkId, newCommentExternalId, mapCommentIdParentId);
        }

        return depth;
    }
}
