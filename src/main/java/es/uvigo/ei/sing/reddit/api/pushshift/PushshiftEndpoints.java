package es.uvigo.ei.sing.reddit.api.pushshift;

import es.uvigo.ei.sing.reddit.entities.pushshift.DataCommentIdsJson;
import es.uvigo.ei.sing.reddit.entities.pushshift.DataCommentJson;
import es.uvigo.ei.sing.reddit.entities.pushshift.DataSubmissionJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PushshiftEndpoints {

    @Retry
    @GET("/reddit/search/comment/")
    Call<DataCommentJson> getComments(@Query(value = "ids", encoded = true) String base36Ids,
                                      @Query(value = "q", encoded = true) String query, @Query("size") String size,
                                      @Query("sort") String sort, @Query("sort_type") String sortType,
                                      @Query("author") String author, @Query("subreddit") String subreddit,
                                      @Query("after") String after, @Query("before") String before);

    @Retry
    @GET("/reddit/search/comment/")
    Call<DataCommentJson> getComments(@Query(value = "ids", encoded = true) String base36Ids);

    @Retry
    @GET("/reddit/search/submission/")
    Call<DataSubmissionJson> getSubmissions(@Query(value = "ids", encoded = true) String base36Ids,
                                            @Query(value = "q", encoded = true) String query,
                                            @Query(value = "q:not", encoded = true) String queryNot,
                                            @Query(value = "title", encoded = true) String title,
                                            @Query(value = "title:not", encoded = true) String titleNot,
                                            @Query(value = "selftext", encoded = true) String selfText,
                                            @Query(value = "selftext:not", encoded = true) String selfTextNot,
                                            @Query("size") String size, @Query("sort") String sort,
                                            @Query("sort_type") String sortType, @Query("author") String author,
                                            @Query("subreddit") String subreddit, @Query("after") String after,
                                            @Query("before") String before, @Query("score") String score,
                                            @Query("num_comments") String numComments, @Query("over_18") String isAdult,
                                            @Query("is_video") String isVideo, @Query("locked") String locked,
                                            @Query("stickied") String stickied, @Query("spoiler") String spoiler,
                                            @Query("contest_mode") String contestMode);

    @Retry
    @GET("/reddit/search/submission/")
    Call<DataSubmissionJson> getSubmissions(@Query(value = "ids", encoded = true) String base36Ids);

    @Retry
    @GET("/reddit/submission/comment_ids/{submissionId}")
    Call<DataCommentIdsJson> getCommentIds(@Path("submissionId") String submissionId);
}
