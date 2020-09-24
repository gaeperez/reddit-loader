package es.uvigo.ei.sing.reddit.services;

import es.uvigo.ei.sing.reddit.entities.UrlEntity;
import es.uvigo.ei.sing.reddit.repositories.UrlRepository;
import es.uvigo.ei.sing.reddit.utils.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Log4j2
@Service
public class UrlService {

    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Optional<UrlEntity> findByComplete(String urlComplete) {
        return urlRepository.findByComplete(urlComplete);
    }

    public UrlEntity createOrRetrieveUrl(String urlComplete) {
        UrlEntity urlEntity;

        Optional<UrlEntity> possibleSavedUrl = findByComplete(urlComplete);
        if (possibleSavedUrl.isPresent())
            urlEntity = possibleSavedUrl.get();
        else {
            urlEntity = new UrlEntity();
            urlEntity.setComplete(urlComplete);

            try {
                URI uri = new URI(urlComplete);
                urlEntity.setProtocol(uri.getScheme());
                urlEntity.setDomain(uri.getHost());
                urlEntity.setPath(uri.getPath());
                urlEntity.setParameters(uri.getQuery());
                urlEntity.setPort(uri.getPort());
                urlEntity.setFragment(uri.getFragment());
            } catch (URISyntaxException e) {
                log.warn(Constants.URL_WARN_DECOMPOSE, urlComplete);
            }
        }

        return urlEntity;
    }
}
