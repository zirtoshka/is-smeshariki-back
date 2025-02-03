package itma.smesharikiback.services;

import itma.smesharikiback.models.reposirories.SmesharikRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;


@Service
@AllArgsConstructor
public class SmesharikAsyncService {
    static final Logger LOGGER =
            Logger.getLogger(SmesharikAsyncService.class.getName());

    private final SmesharikRepository smesharikRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void checkIsOnline() {
        LOGGER.info("Started checkIsOnline");
        smesharikRepository.updateIsOnline();
        LOGGER.info("Finished checkIsOnline");
    }
}
