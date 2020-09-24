package es.uvigo.ei.sing.reddit.api.jraw;

import es.uvigo.ei.sing.reddit.utils.Constants;
import lombok.extern.log4j.Log4j2;
import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.*;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.pagination.SearchPaginator;
import net.dean.jraw.references.SubredditReference;
import net.dean.jraw.tree.CommentNode;
import net.dean.jraw.tree.RootCommentNode;

import java.util.Iterator;
import java.util.List;

@Log4j2
public class RedditAPI {

    private RedditClient reddit;

    public RedditAPI() {
        initializeReddit();
    }

    private void initializeReddit() {
        // Create a descriptive userAgent for the registered application
        UserAgent userAgent = new UserAgent(Constants.REDDIT_USERAGENT_PLATFORM, Constants.REDDIT_USERAGENT_APPID,
                Constants.REDDIT_USERAGENT_VERSION, Constants.REDDIT_USERNAME);
        // Create our credentials
        Credentials credentials = Credentials.script(Constants.REDDIT_USERNAME, Constants.REDDIT_PASSWORD,
                Constants.REDDIT_CLIENT_ID, Constants.REDDIT_CLIENT_SECRET);
        // This is what really sends HTTP requests
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

        // Authenticate and get a RedditClient instance
        this.reddit = OAuthHelper.automatic(adapter, credentials);
        // Force to refresh the token each request (to avoid timeouts when waiting too much time)
        this.reddit.setAutoRenew(true);
    }

    public void renewToken() {
        reddit.getAuthManager().renew();
    }

    public SubredditReference obtainSubreddit(String subredditSearch) throws ApiException, NetworkException {
        return reddit.subreddit(subredditSearch);
    }

    public List<Submission> obtainSubmissionsBySubreddit(SubredditReference subredditReference, SubredditSort subredditSort,
                                                         TimePeriod timePeriod) throws ApiException, NetworkException {
        // Get submissions from the subreddit. They can be obtained from the RedditClient using .subreddit
        DefaultPaginator<Submission> submissionsPaginator = subredditReference.posts()
                .limit(Paginator.RECOMMENDED_MAX_LIMIT) // Send as few requests as possible by requesting as much data as possible
                .sorting(subredditSort) // new posts
                .timePeriod(timePeriod) // of all time
                .build();

        // Fetch all subscriptions and put them into a single list
        return submissionsPaginator.accumulateMerged(-1);
    }

    public List<Submission> obtainSubmissionsByQuery(String query, SearchSort searchSort, TimePeriod timePeriod)
            throws ApiException, NetworkException {
        // Get submissions from the query
        SearchPaginator submissionsPaginator = reddit.search()
                .query(query)
                .limit(Paginator.RECOMMENDED_MAX_LIMIT) // Send as few requests as possible by requesting as much data as possible
                .sorting(searchSort) // Sort by relevance
                .timePeriod(timePeriod) // of all time
                .build();

        // Fetch all subscriptions and put them into a single list
        return submissionsPaginator.accumulateMerged(-1);
    }

    public Iterator<CommentNode<PublicContribution<?>>> obtainComments(String submissionId) throws ApiException,
            NetworkException {
        // Request the comments for the submission (new request to the API)
        RootCommentNode root = reddit.submission(submissionId).comments();
        // Expand all the not-expanded comments
        root.loadFully(reddit);

        return root.walkTree().iterator();
    }

    public Account obtainUserAccount(String userName) {
        return this.reddit.user(userName).query().getAccount();
    }
}
