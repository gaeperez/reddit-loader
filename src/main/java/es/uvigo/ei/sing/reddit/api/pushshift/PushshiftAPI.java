package es.uvigo.ei.sing.reddit.api.pushshift;

import com.google.common.util.concurrent.RateLimiter;
import es.uvigo.ei.sing.reddit.entities.pushshift.DataCommentIdsJson;
import es.uvigo.ei.sing.reddit.entities.pushshift.DataCommentJson;
import es.uvigo.ei.sing.reddit.entities.pushshift.DataSubmissionJson;
import es.uvigo.ei.sing.reddit.utils.Constants;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
public class PushshiftAPI {

    private final PushshiftEndpoints pushshiftEndpoints;
    private final RateLimiter rateLimiter;

    public PushshiftAPI() {
        // Pushshift endpoints instantiation
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Constants.PS_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.PS_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.PS_WRITE_TIMEOUT, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.pushshiftEndpoints = retrofit.create(PushshiftEndpoints.class);

        // Create a limiter of 1 concurrent thread (= 1 request/second)
        this.rateLimiter = RateLimiter.create(1);
    }

    public DataCommentJson getComments(Map<String, String> mapUrlParameters) {
        String formedUrl;

        // Lock the API calls
        rateLimiter.acquire(1);

        Call<DataCommentJson> request = pushshiftEndpoints.getComments(
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_IDS, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_Q, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SIZE, "500"),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SORT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SORTTYPE, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_AUTHOR, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SUBREDDIT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_AFTER, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_BEFORE, ""));
        formedUrl = request.request().url().toString();
        log.debug(formedUrl);

        return (DataCommentJson) executeRequest(request, formedUrl);
    }

    public DataCommentJson getComments(String commentIDs) {
        String formedUrl;

        // Lock the API calls
        rateLimiter.acquire(1);

        Call request = pushshiftEndpoints.getComments(commentIDs);
        formedUrl = request.request().url().toString();
        log.debug(formedUrl);

        return (DataCommentJson) executeRequest(request, formedUrl);
    }

    public DataCommentIdsJson getCommentIds(String submissionId) {
        String formedUrl;

        // Lock the API calls
        rateLimiter.acquire(1);

        Call request = pushshiftEndpoints.getCommentIds(submissionId);
        formedUrl = request.request().url().toString();
        log.debug(formedUrl);

        return (DataCommentIdsJson) executeRequest(request, formedUrl);
    }

    public DataSubmissionJson getSubmissions(Map<String, String> mapUrlParameters) {
        String formedUrl;

        // Lock the API calls
        rateLimiter.acquire(1);

        Call request = pushshiftEndpoints.getSubmissions(
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_IDS, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_Q, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_Q_NOT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_TITLE, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_TITLE_NOT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SELFTEXT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SELFTEXT_NOT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SIZE, "500"),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SORT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SORTTYPE, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_AUTHOR, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SUBREDDIT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_AFTER, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_BEFORE, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SCORE, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_NUMCOMMENTS, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_ADULT, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_VIDEO, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_LOCKED, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_STICKIED, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_SPOILER, ""),
                mapUrlParameters.getOrDefault(Constants.PS_QUERY_CONTEST, ""));
        formedUrl = request.request().url().toString();
        log.debug(formedUrl);

        return (DataSubmissionJson) executeRequest(request, formedUrl);
    }

    public DataSubmissionJson getSubmissions(String submissionIds) {
        String formedUrl;

        // Lock the API calls
        rateLimiter.acquire(1);

        Call request = pushshiftEndpoints.getSubmissions(submissionIds);
        formedUrl = request.request().url().toString();
        log.debug(formedUrl);

        return (DataSubmissionJson) executeRequest(request, formedUrl);
    }

    private Object executeRequest(Call request, String formedUrl) {
        Response response = null;

        try {
            response = request.execute();

            // Check response code and message
            checkResponse(formedUrl, response.code(), response.message());
        } catch (Exception e) {
            log.warn(Constants.PS_WARN_RUNTIME, formedUrl, e.getMessage());
        }

        return response.body();
    }

    private void checkResponse(String formedUrl, int code, String message) {
        if (code != 200)
            log.warn(Constants.PS_WARN_RESPONSE, formedUrl, code, message);
    }
}
