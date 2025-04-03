package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Assignment;
import sqlTables.NotificationSentRepository;

public class OperationManager {
    public static Operation getRightOperation(MyTelegramBot bot, String chatId, String userId, String messageId,
                                              String message) {
        IdPair id = new IdPair(chatId, userId);

        if (message.trim().equals("/breakOperation"))
            return new ExitOperation(id, messageId, bot, message);

        if (bot.getEnterGroupUsers().contains(id))
            return new EnterGroup(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (bot.getExitGroupUsers().contains(id))
            return new ExitGroup(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (bot.getEnterTokenUsers().contains(id))
            return new GetAdminRights(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (bot.getDeleteTokenUsers().contains(id))
            return new DeleteToken(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (bot.getDeleteGroupUsers().contains(id))
            return new DeleteGroup(id, messageId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
        if (bot.getCreateGroupUsers().contains(id))
            return new CreateGroup(id, messageId, bot, message, bot.getGroupRepository());
        if (bot.getCreateAssignmentUsers().containsKey(id))
            return new CreateAssignment(id, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
        if (bot.getDeleteAssignmentUsers().contains(id))
            return new DeleteAssignment(id, messageId, bot, message, bot.getAssignmentRepository());
        if (bot.getCreateSubjectUsers().contains(id))
            return new CreateSubject(id, messageId, bot, message, bot.getSubjectRepository());
        if (bot.getDeleteSubjectUsers().contains(id))
            return new DeleteSubject(id, messageId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());
        if (bot.getLogInUserStates().containsKey(id))
            return new LogIn(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getSignInUserStates().containsKey(id))
            return new SignIn(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getSetIntervalUsers().contains(id))
            return new SetInterval(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getGetAssignmentInfo().containsKey(id))
            return new GetAssignments(id, messageId, bot, message, bot.getUserRepository(), bot.getAssignmentRepository(), bot.getSubjectRepository());

        return switch (message.trim()) {
            case "/enterGroup" ->
                    new EnterGroup(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
            case "/exitGroup" ->
                    new ExitGroup(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
            case "/enableNotifications" ->
                    new EnableAutoMailing(id, messageId, bot, message, bot.getUserRepository());
            case "/disableNotifications" ->
                    new DisableAutoMailing(id, messageId, bot, message, bot.getUserRepository());
            case "/generateToken" ->
                    new GenerateToken(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/getAdminRights" ->
                    new GetAdminRights(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/getMyTokens" ->
                    new GetTokens(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/deleteToken" ->
                    new DeleteToken(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/createGroup" -> new CreateGroup(id, messageId, bot, message, bot.getGroupRepository());
            case "/deleteGroup" ->
                    new DeleteGroup(id, messageId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
            case "/createAssignment" -> new CreateAssignment(id, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
            case "/deleteAssignment" ->
                    new DeleteAssignment(id, messageId, bot, message, bot.getAssignmentRepository());
            case "/createSubject" ->
                    new CreateSubject(id, messageId, bot, message, bot.getSubjectRepository());
            case "/deleteSubject" ->
                    new DeleteSubject(id, messageId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());
            case "/admin" -> new SetAdminKeyboard(id, messageId, bot, message);
            case "/info" -> new GetInfo(id, messageId, bot, message, bot.getAssignmentRepository());
            case "/help" -> new HelpUser(id, messageId, bot, message);
            case "/helpAdmin" -> new HelpAdmin(id, messageId, bot, message);
            case "/login" -> new LogIn(id, messageId, bot, message, bot.getUserRepository());
            case "/register" -> new SignIn(id, messageId, bot, message, bot.getUserRepository());
            case "/logout" -> new LogOut(id, messageId, bot, message);
            case "/start" -> new Start(id, messageId, bot, message);
            case "/about" -> new AboutOperation(id, messageId, bot, message);
            case "/setInterval" -> new SetInterval(id, messageId, bot, message, bot.getUserRepository());
            case "/getAssignments" -> new GetAssignments(id, messageId, bot, message, bot.getUserRepository(), bot.getAssignmentRepository(), bot.getSubjectRepository());
            default -> new MisUnderstand(id, messageId, bot, message);
        };

    }
    public static Operation getShutDownOperation(MyTelegramBot bot, IdPair id,
                                                 String messageId, String message) {
        return new ShutDownOperation(id, messageId, bot, message);
    }
    public static Operation getNotificationOperation(MyTelegramBot bot, IdPair id,
                                                     Assignment assignment,
                                                     NotificationSentRepository notificationSentRepository) {
        return new NotifyOperation(id, null, bot, null, assignment, notificationSentRepository);
    }
}
