package mainBody;

import lombok.Getter;
import operations.ExitOperation;
import operations.OperationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import sqlTables.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map.Entry;

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
    @Getter
    private final NotificationSentRepository notificationSentRepository;

    final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final String botUsername = "DeadlineDemonBot"; // Имя твоего бота
    private final String botToken = "8054120880:AAF78Qz9kPvwR3p2OhN4GEFCVznCw-Kf2No"; // Токен бота
    final int RECONNECT_PAUSE =10000;

    @Getter
    private final ConcurrentHashMap<IdPair, User> authorizedUsers = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<IdPair, NamePasswordState> logInUserStates = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<IdPair, NamePasswordState> signInUserStates = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> enterGroupUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> exitGroupUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> enterTokenUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> deleteTokenUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> createGroupUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> deleteGroupUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentHashMap<IdPair, TitDesGroDeaSubState> createAssignmentUsers = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> deleteAssignmentUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> createSubjectUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> deleteSubjectUsers = new ConcurrentLinkedQueue<>();

    public MyTelegramBot(UserRepository userRepository,
                         SubjectRepository subjectRepository,
                         GroupRepository groupRepository,
                         AssignmentRepository assignmentRepository,
                         AdminTokenRepository adminTokenRepository,
                         NotificationSentRepository notificationSentRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.assignmentRepository = assignmentRepository;
        this.adminTokenRepository = adminTokenRepository;
        this.notificationSentRepository = notificationSentRepository;
        NotificationThread notificationThread = new NotificationThread(this, assignmentRepository, notificationSentRepository);
        Thread notifyThread = new Thread(notificationThread);
        notifyThread.setDaemon(true);
        notifyThread.start();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();
            Integer messageId = update.getMessage().getMessageId();
            executorService.execute(OperationManager.getRightOperation(this, chatId.toString(), userId.toString(),
                    messageId.toString(), messageText));
        }
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            int lastUnderscore = callbackData.lastIndexOf("_");
            if (lastUnderscore == -1)
                return;
            String actualData = callbackData.substring(0, lastUnderscore);
            String replyId = callbackData.substring(lastUnderscore + 1);
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Long userId = update.getCallbackQuery().getFrom().getId();
            if (!replyId.equals(userId.toString()))
                return;
            executorService.execute(OperationManager.getRightOperation(this, chatId.toString(), userId.toString(),
                    null, actualData));
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
}
