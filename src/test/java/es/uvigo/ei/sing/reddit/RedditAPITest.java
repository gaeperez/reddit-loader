package es.uvigo.ei.sing.reddit;

import es.uvigo.ei.sing.reddit.api.jraw.RedditAPI;
import net.dean.jraw.models.SearchSort;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.references.SubredditReference;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RedditAPITest {

    private static RedditAPI redditAPI;

    @BeforeClass
    public static void initRedditAPI() {
        // Execute once and share the API with all tests
        redditAPI = new RedditAPI();
    }

    @Test
    public void subredditShouldBeRetrieved() {
        Assert.assertNotNull("Cannot retrieve subreddit", redditAPI.obtainSubreddit("DBZDokkanBattle").about());
    }

    @Test
    public void submissionsShouldBeRetrievedBySubreddit() {
        SubredditReference subredditReference = redditAPI.obtainSubreddit("DBZDokkanBattle");
        Assert.assertFalse(redditAPI.obtainSubmissionsBySubreddit(subredditReference, SubredditSort.HOT,
                TimePeriod.HOUR).isEmpty());
    }

    @Test
    public void submissionsShouldbeRetrievedByQuery() {
        Assert.assertFalse(redditAPI.obtainSubmissionsByQuery("Dragon Ball", SearchSort.RELEVANCE,
                TimePeriod.HOUR).isEmpty());
    }

    @Test
    public void commentsShouldBeObtained() {
        Assert.assertNotNull("Cannot retrieve comments", redditAPI.obtainComments("bcccjh"));
    }
}
