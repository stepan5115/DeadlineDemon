package operations;

import mainBody.MyTelegramBot;

public class OperationManager {
    public static Operation getRightOperation(MyTelegramBot bot, String chatId, String message) {
        if (bot.getLogInUserStates().containsKey(chatId))
            return new LogIn(chatId, bot, message, bot.getUserRepository());
        if (bot.getSignInUserStates().containsKey(chatId))
            return new SignIn(chatId, bot, message, bot.getUserRepository());
        if (bot.getEnterGroupUsers().contains(chatId))
            return new EnterGroup(chatId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (bot.getExitGroupUsers().contains(chatId))
            return new ExitGroup(chatId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (bot.getEnterTokenUsers().contains(chatId))
            return new GetAdminRights(chatId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (bot.getDeleteTokenUsers().contains(chatId))
            return new DeleteToken(chatId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (bot.getDeleteGroupUsers().contains(chatId))
            return new DeleteGroup(chatId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
        if (bot.getCreateGroupUsers().contains(chatId))
            return new CreateGroup(chatId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
        if (bot.getCreateAssignmentUsers().containsKey(chatId))
            return new CreateAssignment(chatId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
        if (bot.getDeleteAssignmentUsers().contains(chatId))
            return new DeleteAssignment(chatId, bot, message, bot.getAssignmentRepository());
        if (bot.getCreateSubjectUsers().contains(chatId))
            return new CreateSubject(chatId, bot, message, bot.getSubjectRepository());
        if (bot.getDeleteSubjectUsers().contains(chatId))
            return new DeleteSubject(chatId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());

        if (message.trim().equals("/login"))
            return new LogIn(chatId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/register"))
            return new SignIn(chatId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/logout"))
            return new LogOut(chatId, bot, message);
        if (message.trim().equals("/enterGroup"))
            return new EnterGroup(chatId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (message.trim().equals("/exitGroup"))
            return new ExitGroup(chatId, bot, message, bot.getUserRepository(), bot.getGroupRepository());
        if (message.trim().equals("/enableNotifications"))
            return new EnableAutoMailing(chatId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/disableNotifications"))
            return new DisableAutoMailing(chatId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/generateToken"))
            return new GenerateToken(chatId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (message.trim().equals("/getAdminRights"))
            return new GetAdminRights(chatId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (message.trim().equals("/getMyTokens"))
            return new GetTokens(chatId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (message.trim().equals("/deleteToken"))
            return new DeleteToken(chatId, bot, message, bot.getUserRepository(), bot.getAdminTokenRepository());
        if (message.trim().equals("/createGroup"))
            return new CreateGroup(chatId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
        if (message.trim().equals("/deleteGroup"))
            return new DeleteGroup(chatId, bot, message, bot.getGroupRepository(), bot.getUserRepository());
        if (message.trim().equals("/createAssignment"))
            return new CreateAssignment(chatId, bot, message,
                    bot.getAssignmentRepository(), bot.getGroupRepository(),
                    bot.getSubjectRepository());
        if (message.trim().equals("/deleteAssignment"))
            return new DeleteAssignment(chatId, bot, message, bot.getAssignmentRepository());
        if (message.trim().equals("/createSubject"))
            return new CreateSubject(chatId, bot, message, bot.getSubjectRepository());
        if (message.trim().equals("/deleteSubject"))
            return new DeleteSubject(chatId, bot, message, bot.getSubjectRepository(), bot.getAssignmentRepository());
        if (message.trim().equals("/start"))
            return new Start(chatId, bot, message);
        if (message.trim().equals("/admin"))
            return new SetAdminKeyboard(chatId, bot, message);
        if (message.trim().equals("/info"))
            return new GetInfo(chatId, bot, message);
        return new MisUnderstand(chatId, bot, message);
    }
    public static Operation getShutDownOperation(MyTelegramBot bot, String chatId, String message) {
        return new ShutDownOperation(chatId, bot, message);
    }
}
