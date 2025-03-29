package operations;

import mainBody.MyTelegramBot;

public class ExitOperation extends Operation {
    public ExitOperation(String chatId, MyTelegramBot bot, String message) {
        super(chatId, bot, message);
    }
    public void run() {
        bot.getLogInUserStates().remove(chatId);
        bot.getSignInUserStates().remove(chatId);
        bot.getEnterGroupUsers().remove(chatId);
        bot.getExitGroupUsers().remove(chatId);
        bot.getEnterTokenUsers().remove(chatId);
        bot.getDeleteTokenUsers().remove(chatId);
        bot.getCreateGroupUsers().remove(chatId);
        bot.getDeleteGroupUsers().remove(chatId);
        bot.getCreateAssignmentUsers().remove(chatId);
        bot.getDeleteAssignmentUsers().remove(chatId);
        bot.getCreateSubjectUsers().remove(chatId);
        bot.getDeleteSubjectUsers().remove(chatId);
    }
}
