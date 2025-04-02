package operations;

import mainBody.MyTelegramBot;
import sqlTables.Assignment;
import sqlTables.NotificationSentRepository;

public class OperationManager {
    public static Operation getRightOperation(MyTelegramBot bot, String chatId, String userId, String messageId,
                                              String message) {
        if (bot.getLogInUserStates().containsKey(chatId))
            return new LogIn(chatId, userId, messageId, bot, message, bot.getUserRepository());
        if (bot.getSignInUserStates().containsKey(chatId))
            return new SignIn(chatId, userId, messageId, bot, message, bot.getUserRepository());
        if (bot.getEnterGroupUsers().contains(chatId))
            return new EnterGroup(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (bot.getExitGroupUsers().contains(chatId))
            return new ExitGroup(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (bot.getEnterTokenUsers().contains(chatId))
            return new GetAdminRights(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (bot.getDeleteTokenUsers().contains(chatId))
            return new DeleteToken(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (bot.getDeleteGroupUsers().contains(chatId))
            return new DeleteGroup(chatId, userId, messageId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
        if (bot.getCreateGroupUsers().contains(chatId))
            return new CreateGroup(chatId, userId, messageId, bot, message, bot.getGroupRepository());
        if (bot.getCreateAssignmentUsers().containsKey(chatId))
            return new CreateAssignment(chatId, userId, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
        if (bot.getDeleteAssignmentUsers().contains(chatId))
            return new DeleteAssignment(chatId, userId, messageId, bot, message, bot.getAssignmentRepository());
        if (bot.getCreateSubjectUsers().contains(chatId))
            return new CreateSubject(chatId, userId, messageId, bot, message, bot.getSubjectRepository());
        if (bot.getDeleteSubjectUsers().contains(chatId))
            return new DeleteSubject(chatId, userId, messageId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());

        if (message.trim().equals("/login"))
            return new LogIn(chatId, userId, messageId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/register"))
            return new SignIn(chatId, userId, messageId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/logout"))
            return new LogOut(chatId, userId, messageId, bot, message);
        if (message.trim().equals("/enterGroup"))
            return new EnterGroup(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (message.trim().equals("/exitGroup"))
            return new ExitGroup(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (message.trim().equals("/enableNotifications"))
            return new EnableAutoMailing(chatId, userId, messageId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/disableNotifications"))
            return new DisableAutoMailing(chatId, userId, messageId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/generateToken"))
            return new GenerateToken(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (message.trim().equals("/getAdminRights"))
            return new GetAdminRights(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (message.trim().equals("/getMyTokens"))
            return new GetTokens(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (message.trim().equals("/deleteToken"))
            return new DeleteToken(chatId, userId, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (message.trim().equals("/createGroup"))
            return new CreateGroup(chatId, userId, messageId, bot, message, bot.getGroupRepository());
        if (message.trim().equals("/deleteGroup"))
            return new DeleteGroup(chatId, userId, messageId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
        if (message.trim().equals("/createAssignment"))
            return new CreateAssignment(chatId, userId, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
        if (message.trim().equals("/deleteAssignment"))
            return new DeleteAssignment(chatId, userId, messageId, bot, message, bot.getAssignmentRepository());
        if (message.trim().equals("/createSubject"))
            return new CreateSubject(chatId, userId, messageId, bot, message, bot.getSubjectRepository());
        if (message.trim().equals("/deleteSubject"))
            return new DeleteSubject(chatId, userId, messageId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());
        if (message.trim().equals("/start"))
            return new Start(chatId, userId, messageId, bot, message);
        if (message.trim().equals("/admin"))
            return new SetAdminKeyboard(chatId, userId, messageId, bot, message);
        if (message.trim().equals("/info"))
            return new GetInfo(chatId, userId, messageId, bot, message, bot.getAssignmentRepository());
        if (message.trim().equals("/about"))
            return new AboutOperation(chatId, userId, messageId, bot, message);
        if (message.trim().equals("/help"))
            return new HelpUser(chatId, userId, messageId, bot, message);
        if (message.trim().equals("/helpAdmin"))
            return new HelpAdmin(chatId, userId, messageId, bot, message);
        return new MisUnderstand(chatId, userId, messageId, bot, message);
    }
    public static Operation getShutDownOperation(MyTelegramBot bot, String userId,
                                                 String messageId, String chatId, String message) {
        return new ShutDownOperation(chatId, userId, messageId, bot, message);
    }
    public static Operation getNotificationOperation(MyTelegramBot bot, String userId,
                                                     String messageId, String chatId, String message,
                                                     Assignment assignment,
                                                     NotificationSentRepository notificationSentRepository) {
        return new NotifyOperation(chatId, userId, messageId, bot, message, assignment, notificationSentRepository);
    }
}
