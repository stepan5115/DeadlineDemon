package operations;

import mainBody.MyTelegramBot;

public class ExitOperation extends Operation {
    public ExitOperation(String chatId, String userId, String messageId,
                         MyTelegramBot bot, String message) {
        super(chatId, userId, messageId, bot, message);
    }
    public void run() {
        bot.getLogInUserStates().remove(userId);
        bot.getSignInUserStates().remove(userId);
        bot.getEnterGroupUsers().remove(userId);
        bot.getExitGroupUsers().remove(userId);
        bot.getEnterTokenUsers().remove(userId);
        bot.getDeleteTokenUsers().remove(userId);
        bot.getCreateGroupUsers().remove(userId);
        bot.getDeleteGroupUsers().remove(userId);
        bot.getCreateAssignmentUsers().remove(userId);
        bot.getDeleteAssignmentUsers().remove(userId);
        bot.getCreateSubjectUsers().remove(userId);
        bot.getDeleteSubjectUsers().remove(userId);
        sendMessage.setText("Break all operations if they have been exited");
        sendReply();
    }
}
