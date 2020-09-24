package es.uvigo.ei.sing.reddit.controllers;

import es.uvigo.ei.sing.reddit.entities.QueryEntity;
import es.uvigo.ei.sing.reddit.entities.SubredditEntity;
import es.uvigo.ei.sing.reddit.services.PushshiftService;
import es.uvigo.ei.sing.reddit.services.QueryService;
import es.uvigo.ei.sing.reddit.services.RedditService;
import es.uvigo.ei.sing.reddit.utils.Constants;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import net.dean.jraw.models.SearchSort;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Log4j2
@Transactional
@Controller
public class AppController {

    private final RedditService redditService;
    private final PushshiftService pushshiftService;
    private final QueryService queryService;

    @Autowired
    public AppController(RedditService redditService, PushshiftService pushshiftService, QueryService queryService) {
        this.redditService = redditService;
        this.pushshiftService = pushshiftService;
        this.queryService = queryService;
    }

    @Synchronized
    public int runUpdate(QueryEntity queryEntity, SubredditSort sortValue) {
        String message = "";
        int timeToWait = Constants.APP_SECONDS_SUCCESS;
        String subredditSearch = queryEntity.getValue();
        Set<SubredditEntity> subredditEntities = new HashSet<>();

        try {
            log.info(Constants.APP_INFO_UPDATE_START, subredditSearch, sortValue, TimePeriod.ALL);

            // Retrieve information
            subredditEntities.add(redditService.runUpdate(subredditSearch, sortValue, TimePeriod.ALL));
        } catch (Exception e) {
            e.printStackTrace();
            message = String.format(Constants.APP_ERROR_RUN, e.getMessage(), subredditSearch);
            log.error(message);
            timeToWait = Constants.APP_SECONDS_ERROR;
        } finally {
            // Only increment this query one time
            boolean increment = false;
            if (sortValue.equals(SubredditSort.TOP))
                increment = true;

            // Save information in the database
            saveQueryInformation(queryEntity, subredditEntities, message, increment);

            redditService.renewToken();
            log.info(Constants.APP_INFO_UPDATE_END, subredditSearch, sortValue, TimePeriod.ALL);
        }

        return timeToWait;
    }

    @Synchronized
    public int runQuery(QueryEntity queryEntity) {
        String message = "";
        int timeToWait = Constants.APP_SECONDS_SUCCESS;
        String value = queryEntity.getValue();
        Set<SubredditEntity> subredditEntities = new HashSet<>();

        try {
            log.info(Constants.APP_INFO_QUERY_START, value, SearchSort.RELEVANCE, TimePeriod.ALL);

            // Retrieve information from the last 24 hours
            subredditEntities = redditService.runQuery(value, SearchSort.RELEVANCE, TimePeriod.ALL);
        } catch (Exception e) {
            e.printStackTrace();
            message = String.format(Constants.APP_ERROR_RUN, e.getMessage(), value);
            log.error(message);
            timeToWait = Constants.APP_SECONDS_ERROR;
        } finally {
            // Save information in the database
            saveQueryInformation(queryEntity, subredditEntities, message, true);

            redditService.renewToken();
            log.info(Constants.APP_INFO_QUERY_END, value, SearchSort.RELEVANCE, TimePeriod.DAY);
        }

        return timeToWait;
    }

    @Synchronized
    public int runPSComment(QueryEntity queryEntity) {
        String message = "";
        int timeToWait = Constants.APP_SECONDS_SUCCESS;
        String urlQuery = queryEntity.getValue();
        Set<SubredditEntity> subredditEntities = new HashSet<>();

        if (urlQuery.length() <= Constants.PS_URL_LIMIT) {
            try {
                log.info(Constants.APP_INFO_PSCOMMENT_START, urlQuery);

                // Retrieve information from pushshift
                subredditEntities = pushshiftService.runPSComments(urlQuery);
            } catch (Exception e) {
                e.printStackTrace();
                message = String.format(Constants.APP_ERROR_RUN, e.getMessage(), urlQuery);
                log.error(message);
                timeToWait = Constants.APP_SECONDS_ERROR;
            } finally {
                // Save information in the database
                saveQueryInformation(queryEntity, subredditEntities, message, true);

                redditService.renewToken();
                log.info(Constants.APP_INFO_PSCOMMENT_END, urlQuery);
            }
        } else {
            message = String.format(Constants.APP_ERROR_INVALID_URL, urlQuery, Constants.PS_URL_LIMIT);
            log.error(message);

            // Save information in the database
            saveQueryInformation(queryEntity, subredditEntities, message, true);
        }

        return timeToWait;
    }

    @Synchronized
    public int runPSSubmission(QueryEntity queryEntity) {
        String message = "";
        int timeToWait = Constants.APP_SECONDS_SUCCESS;
        String urlQuery = queryEntity.getValue();
        Set<SubredditEntity> subredditEntities = new HashSet<>();

        if (urlQuery.length() <= Constants.PS_URL_LIMIT) {
            try {
                log.info(Constants.APP_INFO_PSSUBMISSION_START, urlQuery);

                // Retrieve information from pushshift
                subredditEntities = pushshiftService.runPSSubmission(urlQuery);
            } catch (Exception e) {
                e.printStackTrace();
                message = String.format(Constants.APP_ERROR_RUN, e.getMessage(), urlQuery);
                log.error(message);
                timeToWait = Constants.APP_SECONDS_ERROR;
            } finally {
                // Save information in the database
                saveQueryInformation(queryEntity, subredditEntities, message, true);

                redditService.renewToken();
                log.info(Constants.APP_INFO_PSSUBMISSION_END, urlQuery);
            }
        } else {
            message = String.format(Constants.APP_ERROR_INVALID_URL, urlQuery, Constants.PS_URL_LIMIT);
            log.error(message);

            // Save information in the database
            saveQueryInformation(queryEntity, subredditEntities, message, true);
        }

        return timeToWait;
    }

    private void saveQueryInformation(QueryEntity queryEntity, Set<SubredditEntity> subredditEntities, String message,
                                      boolean increment) {
        if (!subredditEntities.isEmpty()) {
            // Save using cascades (transactional method)
            message = String.format(Constants.APP_INFO_SAVE_ALL, subredditEntities.size());
            log.info(message);

            // Save query information
            queryEntity.setSubreddits(subredditEntities);
        }

        if (increment)
            queryEntity.incrementTimesExecuted();
        queryEntity.setUpdated(LocalDateTime.now());
        queryEntity.setMessage(message);
        queryService.save(queryEntity);
    }
}
