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
            return new LogInOperation(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getSignUpUserStates().containsKey(id))
            return new SignUpOperation(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getSetIntervalUsers().contains(id))
            return new SetInterval(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getGetAssignmentInfo().containsKey(id))
            return new GetAssignments(id, messageId, bot, message, bot.getAssignmentRepository(), bot.getSubjectRepository());
        if (bot.getExcludeAssignmentStates().containsKey(id))
            return new ExcludeAssignmentOperation(id, messageId, bot, message, bot.getUserRepository(),
                    bot.getAssignmentRepository(), bot.getSubjectRepository(), bot.getGroupRepository());
        if (bot.getIncludeAssignmentState().containsKey(id))
            return new IncludeAssignmentOperation(id, messageId, bot, message, bot.getUserRepository(),
                    bot.getAssignmentRepository(), bot.getSubjectRepository(), bot.getGroupRepository());
        if (bot.getExcludeSubjectState().containsKey(id))
            return new ExcludeSubjectOperation(id, messageId, bot, message, bot.getUserRepository(),
                    bot.getSubjectRepository());
        if (bot.getIncludeSubject().contains(id))
            return new IncludeSubject(id, messageId, bot, message, bot.getUserRepository(),
                    bot.getAssignmentRepository(), bot.getSubjectRepository());

        return switch (message.trim()) {
            case "/help" -> new HelpUser(id, messageId, bot, message);
            case "/back" ->
                new BackKeyboardOperation(id, messageId, bot, message);
            //Notifications Menu
            case "/notifications" ->
                new NotificationsKeyboardGet(id, messageId, bot, message);
            case "/enable" ->
                    new EnableAutoMailing(id, messageId, bot, message, bot.getUserRepository());
            case "/disable" ->
                    new DisableAutoMailing(id, messageId, bot, message, bot.getUserRepository());
            case "/excludeAssignment" ->
                new ExcludeAssignmentOperation(id, messageId, bot, message, bot.getUserRepository(),
                        bot.getAssignmentRepository(), bot.getSubjectRepository(), bot.getGroupRepository());
            case "/includeAssignment" ->
                new IncludeAssignmentOperation(id, messageId, bot, message, bot.getUserRepository(),
                        bot.getAssignmentRepository(), bot.getSubjectRepository(), bot.getGroupRepository());
            case "/excludeSubject" ->
                new ExcludeSubjectOperation(id, messageId, bot, message, bot.getUserRepository(),
                        bot.getSubjectRepository());
            case "/includeSubject" ->
                    new IncludeSubject(id, messageId, bot, message, bot.getUserRepository(),
                            bot.getAssignmentRepository(), bot.getSubjectRepository());
            //Groups Menu
            case "/groups" ->
                new GroupsKeyboardGet(id, messageId, bot, message);
            case "/enter" ->
                    new EnterGroup(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
            case "/exit" ->
                    new ExitGroup(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
            case "/createGroup" ->
                    new CreateGroup(id, messageId, bot, message, bot.getGroupRepository());
            case "/deleteGroup" ->
                    new DeleteGroup(id, messageId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
            //Tasks Menu
            case "/tasks" ->
                new TasksKeyboardGet(id, messageId, bot, message);
            case "/getTasks" -> new GetAssignments(id, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getSubjectRepository());
            case "/createTask" -> new CreateAssignment(id, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
            case "/deleteTask" ->
                    new DeleteAssignment(id, messageId, bot, message, bot.getAssignmentRepository());
            case "/subjects" ->
                new SubjectsKeyboardGet(id, messageId, bot, message);
            case "/createSubject" ->
                    new CreateSubject(id, messageId, bot, message, bot.getSubjectRepository());
            case "/deleteSubject" ->
                    new DeleteSubject(id, messageId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());
            //Tokens Menu
            case "/tokens" ->
                new TokensKeyboardGet(id, messageId, bot, message);
            case "/generate" ->
                    new GenerateToken(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/delete" ->
                    new DeleteToken(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/getMyTokens" ->
                    new GetTokens(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            //Profile Menu
            case "/profile" ->
                new ProfileKeyboardGet(id, messageId, bot, message);
            case "/getAdminRights" ->
                    new GetAdminRights(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/info" -> new GetInfo(id, messageId, bot, message, bot.getAssignmentRepository());
            case "/logout" -> new LogOut(id, messageId, bot, message);
            case "/setInterval" -> new SetInterval(id, messageId, bot, message, bot.getUserRepository());
            //Start Menu
            case "/start" -> new Start(id, messageId, bot, message);
            case "/login" -> new LogInOperation(id, messageId, bot, message, bot.getUserRepository());
            case "/register" -> new SignUpOperation(id, messageId, bot, message, bot.getUserRepository());
            case "/about" -> new AboutOperation(id, messageId, bot, message);
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
