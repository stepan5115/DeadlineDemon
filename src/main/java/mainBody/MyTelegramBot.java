package mainBody;

import lombok.Getter;
import operations.IncludeAssignmentOperation;
import operations.OperationManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import sqlTables.*;
import states.*;

import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Component
public class MyTelegramBot extends TelegramLongPollingBot implements AuthorizedUsersProvider {
    private final String botToken = "8054120880:AAF78Qz9kPvwR3p2OhN4GEFCVznCw-Kf2No";
    private final String botName = "DeadlineDemonBot";

    private static final Logger logger = Logger.getLogger(MyTelegramBot.class.getName());
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
    @Getter
    private final PendingNotificationRepository pendingNotificationRepository;

    final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Getter
    private final ConcurrentHashMap<IdPair, User> authorizedUsers = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<IdPair, AuthState> logInUserStates = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<IdPair, AuthState> signUpUserStates = new ConcurrentHashMap<>();
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
    @Getter
    private final ConcurrentLinkedQueue<IdPair> setIntervalUsers = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentHashMap<IdPair, AssignmentInfoState> getAssignmentInfo = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<IdPair, ExcludeAssignmentState> excludeAssignmentStates = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<IdPair, IncludeAssignmentState> includeAssignmentState = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<IdPair, ExcludeSubjectState> excludeSubjectState = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentLinkedQueue<IdPair> includeSubject = new ConcurrentLinkedQueue<>();

    public MyTelegramBot(UserRepository userRepository,
                         SubjectRepository subjectRepository,
                         GroupRepository groupRepository,
                         AssignmentRepository assignmentRepository,
                         AdminTokenRepository adminTokenRepository,
                         NotificationSentRepository notificationSentRepository,
                         PendingNotificationRepository pendingNotificationRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.assignmentRepository = assignmentRepository;
        this.adminTokenRepository = adminTokenRepository;
        this.notificationSentRepository = notificationSentRepository;
        this.pendingNotificationRepository = pendingNotificationRepository;
        NotificationThread notificationThread = new NotificationThread(this, assignmentRepository, notificationSentRepository);
        Thread notifyThread = new Thread(notificationThread);
        notifyThread.setDaemon(true);
        notifyThread.start();
        List<PendingNotification> pendingUsers = pendingNotificationRepository.findAll();

        for (PendingNotification user : pendingUsers) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Бот снова в сети! 🚀");
            sendMessage.setChatId(user.getChatId().toString());
            try {
                execute(sendMessage);
            } catch (Exception ex) {
                logger.severe("Can't send message: " + ex.getMessage());
            }
        }

        pendingNotificationRepository.deleteAll();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Long chatId = message.getChatId();
            Long userId = message.getFrom().getId();
            Integer messageId = message.getMessageId();
            boolean isReplyToBot = message.getReplyToMessage() != null
                    && message.getReplyToMessage().getFrom().getUserName().equals(botName);
            boolean isPrivate = message.getChat().isUserChat();
            if (messageText.startsWith("/") || isPrivate || isReplyToBot)
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
    /*
    @PostMapping("/login")
    public DeferredResult<ResponseEntity<String>> login(
            @RequestParam String username,
            @RequestParam String password) {

        DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerLogInOperation(username, password, );

        return deferredResult;
    }
     */

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public ConcurrentHashMap<IdPair, User> getAuthorizedUsers() {
        return authorizedUsers;
    }
}
