package mainBody;

import operations.OperationManager;
import sqlTables.Assignment;
import sqlTables.AssignmentRepository;
import sqlTables.NotificationSentRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class NotificationThread implements Runnable {
    public final int MILLI_BETWEEN_MESSAGES = 6000;
    private final AssignmentRepository assignmentRepository;
    private final NotificationSentRepository notificationSentRepository;
    private final MyTelegramBot bot;

    public NotificationThread(MyTelegramBot bot, AssignmentRepository assignmentRepository,
                              NotificationSentRepository notificationSentRepository) {
        this.bot = bot;
        this.assignmentRepository = assignmentRepository;
        this.notificationSentRepository = notificationSentRepository;
    }
    public void run() {
        while (true) {
            assignmentRepository.deleteExpiredAssignments(LocalDateTime.now());
            for (Assignment assignment : assignmentRepository.findAll()) {
                List<String> busyId = new LinkedList<>();
                for (IdPair userId : bot.getAuthorizedUsers().keySet())
                    if (!busyId.contains(userId.getChatId())) {
                        busyId.add(userId.getChatId());
                        bot.executorService.execute(OperationManager.getNotificationOperation(bot, userId, assignment,
                                notificationSentRepository));
                    }
            }
            try {
                Thread.sleep(MILLI_BETWEEN_MESSAGES);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
