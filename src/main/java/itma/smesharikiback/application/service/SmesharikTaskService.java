package itma.smesharikiback.application.service;

import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


@Service
@RequiredArgsConstructor
public class SmesharikTaskService {
    static final Logger LOGGER =
            Logger.getLogger(SmesharikTaskService.class.getName());

    private final SmesharikRepository smesharikRepository;
    private final EmailService emailService;
    private final BlockingQueue<Pair<Smesharik, Smesharik>> taskQueue = new LinkedBlockingQueue<>();

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void updateIsOnline() {
        LOGGER.info("Начата проверка текущего онлайна");
        smesharikRepository.updateIsOnline();
        LOGGER.info("Закончена проверка текущего онлайна");
    }

    @Transactional(readOnly = true)
//    @Scheduled(cron = "0 * * * * ?")
    public void searchInactiveUsersFriends() {
        LOGGER.info("Starting inactive users notification");

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<Smesharik> inactiveUsers = smesharikRepository.findByLastActiveBefore(threeDaysAgo);
        List<Tuple> friendships = smesharikRepository.findFriendsForUsers(inactiveUsers);

        if (inactiveUsers.isEmpty()) {
            LOGGER.info("Неавктивных юзеров не было найдено");
            return;
        }

        friendships.forEach(friendShip -> {
            try {
                taskQueue.put(Pair.of((Smesharik) friendShip.get(0), (Smesharik) friendShip.get(1)));
            } catch (InterruptedException e) {
                LOGGER.severe(e.getMessage());
            }
        });

        LOGGER.info("Создание задач на рассылку по неактивным пользователям завершена");
    }

    @Async
    @Scheduled(fixedRate = 500)
    public void sendEmail() {
        try {
            Pair<Smesharik, Smesharik> smesharikPair = taskQueue.take();
            Smesharik user = smesharikPair.getLeft();
            Smesharik friend = smesharikPair.getRight();

            String message = String.format(
                    "Добрый день, %s! Ваш друг %s не заходил в систему более 3 дней.",
                    friend.getName(),
                    user.getName()
            );

            try {
                emailService.sendEmail(
                        friend.getEmail(),
                        "Ваш друг неактивен",
                        message
                );
                LOGGER.info("Успешно отправлено письмо на email: " + friend.getEmail());
            } catch (Exception e) {
                LOGGER.severe("Ошибка при отправке письма на email: " + friend.getEmail());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}













