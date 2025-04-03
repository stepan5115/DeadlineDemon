package mainBody;

import lombok.RequiredArgsConstructor;
import operations.OperationManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import sqlTables.PendingNotification;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class BotShutdownListener implements ApplicationListener<ContextClosedEvent> {
    private final int THREAD_POOL_SIZE = 100;
    private final MyTelegramBot bot;
    final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (bot.getAuthorizedUsers() == null
                || bot.getAuthorizedUsers().isEmpty()) {
            bot.executorService.shutdown();
            return;
        }
        bot.getPendingNotificationRepository().deleteAll();
        List<String> chatIds = new LinkedList<>();
        for (IdPair userId : bot.getAuthorizedUsers().keySet())
            if (!chatIds.contains(userId.getChatId())) {
                executorService.execute(OperationManager.getShutDownOperation(bot, userId,
                        null,
                        "⚡ Бот будет отключен для технического обслуживания. " +
                                "Приносим извинения за временные неудобства."));
                chatIds.add(userId.getChatId());
                PendingNotification pendingNotification = new PendingNotification();
                pendingNotification.setChatId(Long.parseLong(userId.getChatId()));
                bot.getPendingNotificationRepository().save(pendingNotification);
            }
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        bot.executorService.shutdown();
    }
}