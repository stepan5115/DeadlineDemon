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
        bot.getSignUpUserStates().remove(id);
        bot.getEnterGroupStates().remove(id);
        bot.getExitGroupStates().remove(id);
        bot.getEnterTokenUsers().remove(id);
        bot.getDeleteTokenUsers().remove(id);
        bot.getCreateGroupStates().remove(id);
        bot.getDeleteGroupStates().remove(id);
        bot.getCreateAssignmentStates().remove(id);
        bot.getDeleteAssignmentStates().remove(id);
        bot.getCreateSubjectUsers().remove(id);
        bot.getDeleteSubjectUsers().remove(id);
        bot.getSetIntervalStates().remove(id);
        bot.getGetAssignmentsStates().remove(id);
        bot.getExcludeAssignmentStates().remove(id);
        bot.getIncludeAssignmentStates().remove(id);
        bot.getExcludeSubjectStates().remove(id);
        bot.getIncludeSubjectStates().remove(id);
        sendMessage.setText("Операция завершена");
        sendReply();
    }
}
