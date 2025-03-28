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
        return new MisUnderstand(chatId, bot, message);
    }
    public static Operation getShutDownOperation(MyTelegramBot bot, String chatId, String message) {
        return new ShutDownOperation(chatId, bot, message);
    }
}
