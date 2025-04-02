package mainBody;

import operations.OperationManager;
import sqlTables.Assignment;
import sqlTables.AssignmentRepository;
import sqlTables.NotificationSentRepository;
import sqlTables.User;

import java.time.LocalDateTime;

public class NotificationThread implements Runnable {
    public final int MILLI_BETWEEN_MESSAGES = 6000;
    private AssignmentRepository assignmentRepository;
    private NotificationSentRepository notificationSentRepository;
    private MyTelegramBot bot;

    public NotificationThread(MyTelegramBot bot, AssignmentRepository assignmentRepository,
                              NotificationSentRepository notificationSentRepository) {
        this.bot = bot;
        this.assignmentRepository = assignmentRepository;
        this.notificationSentRepository = notificationSentRepository;
    }
    public void run() {
        while (true) {
            assignmentRepository.deleteExpiredAssignments(LocalDateTime.now());
            for (String userId : bot.getAuthorizedUsers().keySet())
                for (Assignment assignment : assignmentRepository.findAll())
                    bot.executorService.execute(OperationManager.getNotificationOperation(bot, userId, null, userId,"", assignment,
                            notificationSentRepository));
            try {
                Thread.sleep(MILLI_BETWEEN_MESSAGES);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
