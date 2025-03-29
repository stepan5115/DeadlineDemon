package mainBody;

import lombok.Getter;
import operations.DeleteToken;
import operations.Operation;
import operations.OperationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import sqlTables.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
    private final AdminTokenRepository adminTokenRepository;
    @Getter
    private final AssignmentRepository assignmentRepository;
    final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final String botUsername = "DeadlineDemonBot"; // Имя твоего бота
    private final String botToken = "8054120880:AAF78Qz9kPvwR3p2OhN4GEFCVznCw-Kf2No"; // Токен бота
    final int RECONNECT_PAUSE =10000;

    @Getter
    private final ConcurrentHashMap<String, User> authorizedUsers = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<String, NamePasswordState> logInUserStates = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<String, NamePasswordState> signInUserStates = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentLinkedQueue<String> enterGroupUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<String> exitGroupUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<String> enterTokenUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<String> deleteTokenUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<String> createGroupUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<String> deleteGroupUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentHashMap<String, TitDesGroDeaSubState> createAssignmentUsers = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentLinkedQueue<String> deleteAssignmentUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<String> createSubjectUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<String> deleteSubjectUsers = new ConcurrentLinkedQueue<>();

    public MyTelegramBot(UserRepository userRepository,
                         SubjectRepository subjectRepository,
                         GroupRepository groupRepository,
                         AssignmentRepository assignmentRepository,
                         AdminTokenRepository adminTokenRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.assignmentRepository = assignmentRepository;
        this.adminTokenRepository = adminTokenRepository;
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
