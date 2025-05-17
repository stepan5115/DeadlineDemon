package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Assignment;
import sqlTables.NotificationSentRepository;

public class OperationManager {
    public static Operation getRightOperation(MyTelegramBot bot, String chatId, String userId, String messageId,
                                              String message) {
        IdPair id = new IdPair(chatId, userId);

        if (bot.getEnterGroupStates().containsKey(id))
            return new EnterGroupOperation(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (bot.getExitGroupStates().containsKey(id))
            return new ExitGroupOperation(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (bot.getEnterTokenStates().containsKey(id))
            return new EnterTokenOperation(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (bot.getDeleteTokenStates().containsKey(id))
            return new DeleteTokenOperation(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (bot.getDeleteGroupStates().containsKey(id))
            return new DeleteGroupOperation(id, messageId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
        if (bot.getCreateGroupStates().containsKey(id))
            return new CreateGroupOperation(id, messageId, bot, message, bot.getGroupRepository());
        if (bot.getCreateAssignmentStates().containsKey(id))
            return new CreateAssignmentOperation(id, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
        if (bot.getDeleteAssignmentStates().containsKey(id))
            return new DeleteAssignmentOperation(id, messageId, bot, message, bot.getAssignmentRepository(),
                    bot.getSubjectRepository(), bot.getGroupRepository());
        if (bot.getCreateSubjectStates().containsKey(id))
            return new CreateSubjectOperation(id, messageId, bot, message, bot.getSubjectRepository());
        if (bot.getDeleteSubjectStates().containsKey(id))
            return new DeleteSubjectOperation(id, messageId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());
        if (bot.getLogInUserStates().containsKey(id))
            return new LogInOperation(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getSignUpUserStates().containsKey(id))
            return new SignUpOperation(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getSetIntervalStates().containsKey(id))
            return new SetIntervalOperation(id, messageId, bot, message, bot.getUserRepository());
        if (bot.getGetAssignmentsStates().containsKey(id))
            return new GetAssignmentsOperation(id, messageId, bot, message, bot.getAssignmentRepository(), bot.getSubjectRepository(),
                    bot.getGroupRepository());
        if (bot.getExcludeAssignmentStates().containsKey(id))
            return new ExcludeAssignmentOperation(id, messageId, bot, message, bot.getUserRepository(),
                    bot.getAssignmentRepository(), bot.getSubjectRepository(), bot.getGroupRepository());
        if (bot.getIncludeAssignmentStates().containsKey(id))
            return new IncludeAssignmentOperation(id, messageId, bot, message, bot.getUserRepository(),
                    bot.getAssignmentRepository(), bot.getSubjectRepository(), bot.getGroupRepository());
        if (bot.getExcludeSubjectStates().containsKey(id))
            return new ExcludeSubjectOperation(id, messageId, bot, message, bot.getUserRepository(),
                    bot.getSubjectRepository());
        if (bot.getIncludeSubjectStates().containsKey(id))
            return new IncludeSubjectOperation(id, messageId, bot, message, bot.getUserRepository(),
                    bot.getSubjectRepository());

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
                    new IncludeSubjectOperation(id, messageId, bot, message, bot.getUserRepository(),
                            bot.getSubjectRepository());
            //Groups Menu
            case "/groups" ->
                new GroupsKeyboardGet(id, messageId, bot, message);
            case "/enter" ->
                    new EnterGroupOperation(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
            case "/exit" ->
                    new ExitGroupOperation(id, messageId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
            case "/createGroup" ->
                    new CreateGroupOperation(id, messageId, bot, message, bot.getGroupRepository());
            case "/deleteGroup" ->
                    new DeleteGroupOperation(id, messageId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
            //Tasks Menu
            case "/tasks" ->
                new TasksKeyboardGet(id, messageId, bot, message);
            case "/getTasks" -> new GetAssignmentsOperation(id, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getSubjectRepository(),
                    bot.getGroupRepository());
            case "/createTask" -> new CreateAssignmentOperation(id, messageId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
            case "/deleteTask" ->
                    new DeleteAssignmentOperation(id, messageId, bot, message, bot.getAssignmentRepository(),
                            bot.getSubjectRepository(), bot.getGroupRepository());
            case "/subjects" ->
                new SubjectsKeyboardGet(id, messageId, bot, message);
            case "/createSubject" ->
                    new CreateSubjectOperation(id, messageId, bot, message, bot.getSubjectRepository());
            case "/deleteSubject" ->
                    new DeleteSubjectOperation(id, messageId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());
            //Tokens Menu
            case "/tokens" ->
                new TokensKeyboardGet(id, messageId, bot, message);
            case "/generate" ->
                    new GenerateToken(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/delete" ->
                    new DeleteTokenOperation(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/getMyTokens" ->
                    new GetTokens(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            //Profile Menu
            case "/profile" ->
                new ProfileKeyboardGet(id, messageId, bot, message);
            case "/getAdminRights" ->
                    new EnterTokenOperation(id, messageId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
            case "/info" -> new GetInfo(id, messageId, bot, message, bot.getAssignmentRepository());
            case "/logout" -> new LogOut(id, messageId, bot, message);
            case "/setInterval" -> new SetIntervalOperation(id, messageId, bot, message, bot.getUserRepository());
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
