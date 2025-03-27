package mainBody;

import lombok.Getter;
import operations.Operation;
import operations.OperationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sqlTables.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {
    private final int THREAD_POOL_SIZE = 100;
    @Getter
    private final UserRepository userRepository;
    @Getter
    private final SubjectRepository subjectRepository;
    @Getter
    private final GroupRepository groupRepository;
    @Getter
    private final AssignmentRepository assignmentRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final String botUsername = "DeadlineDemonBot"; // Имя твоего бота
    private final String botToken = "8054120880:AAF78Qz9kPvwR3p2OhN4GEFCVznCw-Kf2No"; // Токен бота
    final int RECONNECT_PAUSE =10000;

    @Getter
    private final ConcurrentHashMap<String, User> authorizedUsers = new ConcurrentHashMap<>(); // Храним вошедших пользователей
    @Getter
    private final ConcurrentHashMap<String, State> logInUserStates = new ConcurrentHashMap<>(); // Храним этап ввода
    @Getter
    private final ConcurrentHashMap<String, State> signInUserStates = new ConcurrentHashMap<>(); // Храним этап ввода

    public MyTelegramBot(UserRepository userRepository,
                         SubjectRepository subjectRepository,
                         GroupRepository groupRepository,
                         AssignmentRepository assignmentRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            executorService.execute(OperationManager.getRightOperation(this, chatId.toString(), messageText));
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    //MULTI THREAD LOGIC
    public void execute(Operation operation) {
        executorService.submit(operation);
    }
    public void shutdown() {
        executorService.shutdown();
    }
}
