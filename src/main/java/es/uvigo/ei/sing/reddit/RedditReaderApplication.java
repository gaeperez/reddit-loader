package es.uvigo.ei.sing.reddit;

import es.uvigo.ei.sing.reddit.api.jraw.RedditAPI;
import es.uvigo.ei.sing.reddit.api.pushshift.PushshiftAPI;
import es.uvigo.ei.sing.reddit.utils.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class RedditReaderApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RedditReaderApplication.class).web(WebApplicationType.NONE).headless(false).run(args);
    }

    @Bean
    public RedditAPI getRedditAPI() {
        log.info(Constants.REDDIT_INFO_INIT);
        return new RedditAPI();
    }

    @Bean
    public PushshiftAPI getPushshiftAPI() {
        log.info(Constants.PS_INFO_INIT);
        return new PushshiftAPI();
    }
}

