package es.uvigo.ei.sing.reddit.utils;

import es.uvigo.ei.sing.reddit.controllers.AppController;
import es.uvigo.ei.sing.reddit.entities.QueryEntity;
import es.uvigo.ei.sing.reddit.services.QueryService;
import lombok.extern.log4j.Log4j2;
import net.dean.jraw.models.SubredditSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Log4j2
@Configuration
public class Scheduler {

    @Autowired
    private AppController appController;
    @Autowired
    private QueryService queryService;

    // Wait 10 minutes to start again when all the queries finish
    @Scheduled(fixedDelay = 600000)
    public void runController() {
        // Retrieved all non suspended queries from database
        Iterable<QueryEntity> queryEntities = queryService.findByIsSuspendedFalse();
        // Iterate the queries
        for (QueryEntity queryEntity : queryEntities) {
            String type = String.valueOf(queryEntity.getType());
            int timeToWait = Constants.APP_SECONDS_SUCCESS;

            try {
                switch (type) {
                    case Constants.RUN_MODE_UPDATE:
                        // Retrieve information using all the sorts (maximum number of posts)
                        for (SubredditSort sortValue : SubredditSort.values()) {
                            // Ignore HOT value (deprecated in Reddit)
                            if (!sortValue.equals(SubredditSort.HOT))
                                timeToWait = appController.runUpdate(queryEntity, sortValue);
                        }
                        break;
                    case Constants.RUN_MODE_QUERY:
                        timeToWait = appController.runQuery(queryEntity);
                        break;
                    case Constants.RUN_MODE_PSCOMMENT:
                        timeToWait = appController.runPSComment(queryEntity);
                        break;
                    case Constants.RUN_MODE_PSSUBMISSION:
                        timeToWait = appController.runPSSubmission(queryEntity);
                        break;
                    default:
                        log.error(Constants.APP_ERROR_INVALID_MODE, type, Constants.RUN_MODE_QUERY,
                                Constants.RUN_MODE_UPDATE, Constants.RUN_MODE_PSCOMMENT,
                                Constants.RUN_MODE_PSSUBMISSION);
                        break;
                } // End switch
            } catch (Exception e) {
                // Log the possible unknown exceptions
                log.error(Constants.APP_ERROR_RETRIEVAL, type, e.getMessage(), queryEntity.getValue());
            } finally {
                try {
                    // Waiting among queries
                    log.info(Constants.APP_INFO_WAIT, timeToWait);
                    TimeUnit.SECONDS.sleep(timeToWait);
                } catch (InterruptedException e) {
                    log.error(Constants.APP_ERROR_SLEEP);
                }
            }
        }
    }
}
