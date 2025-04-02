package mainBody;

import lombok.RequiredArgsConstructor;
import operations.OperationManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class BotShutdownListener implements ApplicationListener<ContextClosedEvent> {
    private final int THREAD_POOL_SIZE = 100;
    private final MyTelegramBot telegramBot;
    final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (telegramBot.getAuthorizedUsers() == null
                || telegramBot.getAuthorizedUsers().isEmpty()) {
            telegramBot.executorService.shutdown();
            return;
        }
        for (String userId : telegramBot.getAuthorizedUsers().keySet())
            executorService.execute(OperationManager.getShutDownOperation(telegramBot, userId,
                    null, userId,
                    "⚡ Бот будет отключен для технического обслуживания. " +
                            "Приносим извинения за временные неудобства."));
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        telegramBot.executorService.shutdown();
    }
}