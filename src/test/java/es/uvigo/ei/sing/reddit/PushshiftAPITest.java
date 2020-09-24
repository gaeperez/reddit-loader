package es.uvigo.ei.sing.reddit;

import es.uvigo.ei.sing.reddit.api.pushshift.PushshiftAPI;
import es.uvigo.ei.sing.reddit.utils.Constants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PushshiftAPITest {

    private static PushshiftAPI pushshiftAPI;

    @BeforeClass
    public static void initPushshiftAPI() {
        // Execute once and share the API with all tests
        pushshiftAPI = new PushshiftAPI();
    }

    @Test
    public void commentsShouldBeRetrievedByUrl() {
        Map<String, String> mapUrlParameters = new HashMap<>();
        mapUrlParameters.put(Constants.PS_QUERY_SIZE, "5");
        mapUrlParameters.put(Constants.PS_QUERY_SUBREDDIT, "DBZDokkanBattle");

        Assert.assertFalse(pushshiftAPI.getComments(mapUrlParameters).getData().isEmpty());
    }

    @Test
    public void commentsShouldNotBeRetrievedByUrl() {
        Map<String, String> mapUrlParameters = new HashMap<>();
        mapUrlParameters.put(Constants.PS_QUERY_SIZE, "5");
        mapUrlParameters.put(Constants.PS_QUERY_SUBREDDIT, "DBZDokkanBattle");
        // Invalid value in the URL
        mapUrlParameters.put(Constants.PS_QUERY_BEFORE, "Not numeric value");

        Assert.assertNull(pushshiftAPI.getComments(mapUrlParameters));
    }

    @Test
    public void commentsShouldBeRetrievedByIDs() {
        Assert.assertFalse(pushshiftAPI.getComments("er28j7y,er2kco3,er2mgi1").getData().isEmpty());
    }

    @Test
    public void commentIDsShouldBeRetrieved() {
        Assert.assertFalse(pushshiftAPI.getCommentIds("c2tar3").getCommentIds().isEmpty());
    }

    @Test
    public void submissionsShouldBeRetrievedByUrl() {
        Map<String, String> mapUrlParameters = new HashMap<>();
        mapUrlParameters.put(Constants.PS_QUERY_SIZE, "5");
        mapUrlParameters.put(Constants.PS_QUERY_SUBREDDIT, "DBZDokkanBattle");

        Assert.assertFalse(pushshiftAPI.getSubmissions(mapUrlParameters).getData().isEmpty());
    }

    @Test
    public void submissionsShouldBeRetrievedByIDs() {
        Assert.assertFalse(pushshiftAPI.getSubmissions("c2tar3").getData().isEmpty());
    }
}
