package operations;

import mainBody.MyTelegramBot;

public class OperationManager {
    public static Operation getRightOperation(MyTelegramBot bot, String chatId, String message) {
        if (bot.getLogInUserStates().containsKey(chatId))
            return new LogIn(chatId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/login"))
            return new LogIn(chatId, bot, message, bot.getUserRepository());
        if (bot.getSignInUserStates().containsKey(chatId))
            return new SignIn(chatId, bot, message, bot.getUserRepository());
        if (message.trim().equals("/register")) {
            return new SignIn(chatId, bot, message, bot.getUserRepository());
        }
        if (message.trim().equals("/logout")) {
            return new LogOut(chatId, bot, message);
        }
        return new MisUnderstand(chatId, bot, message);
    }
}
