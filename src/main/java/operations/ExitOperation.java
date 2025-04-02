package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;

public class ExitOperation extends Operation {
    public ExitOperation(IdPair id, String messageId,
                         MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        bot.getLogInUserStates().remove(id);
        bot.getSignInUserStates().remove(id);
        bot.getEnterGroupUsers().remove(id);
        bot.getExitGroupUsers().remove(id);
        bot.getEnterTokenUsers().remove(id);
        bot.getDeleteTokenUsers().remove(id);
        bot.getCreateGroupUsers().remove(id);
        bot.getDeleteGroupUsers().remove(id);
        bot.getCreateAssignmentUsers().remove(id);
        bot.getDeleteAssignmentUsers().remove(id);
        bot.getCreateSubjectUsers().remove(id);
        bot.getDeleteSubjectUsers().remove(id);
        sendMessage.setText("Break all operations if they have been exited");
        sendReply();
    }
}
