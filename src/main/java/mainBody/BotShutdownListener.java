package mainBody;

import lombok.RequiredArgsConstructor;
import operations.OperationManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class BotShutdownListener implements ApplicationListener<ContextClosedEvent> {
    private final int THREAD_POOL_SIZE = 100;
    private final MyTelegramBot telegramBot;
    final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        telegramBot.executorService.shutdown();
        if (telegramBot.getAuthorizedUsers() == null
                || telegramBot.getAuthorizedUsers().isEmpty()) {
            return;
        }
        for (String chatId : telegramBot.getAuthorizedUsers().keySet())
            executorService.execute(OperationManager.getShutDownOperation(telegramBot, chatId,
                    "⚡ Бот будет отключен для технического обслуживания. " +
                            "Приносим извинения за временные неудобства."));
    }
}