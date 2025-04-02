package operations;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sqlTables.User;

abstract public class Operation implements Runnable {
    protected final IdPair id;
    protected final String messageId;
    protected final MyTelegramBot bot;
    protected final String message;
    protected final SendMessage sendMessage;

    public Operation(IdPair id, String messageId, MyTelegramBot bot, String message) {
        this.id = id;
        this.messageId = messageId;
        this.bot = bot;
        this.message = message;
        this.sendMessage = new SendMessage();
        this.sendMessage.setChatId(id.getChatId());
        if (messageId != null)
            this.sendMessage.setReplyToMessageId(Integer.parseInt(messageId));
        else
            this.sendMessage.setReplyToMessageId(null);
    }
    protected void sendReply() {
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void replyForPrivacyMessage() {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(id.getChatId());
        deleteMessage.setMessageId(Integer.parseInt(messageId));
        try {
            bot.execute(deleteMessage);
            sendMessage.setReplyToMessageId(null);
        } catch (TelegramApiException e) {
            SendMessage tmp = new SendMessage();
            tmp.setChatId(id.getChatId());
            tmp.setText("If I'm in a chat, give me admin rights! This way I can delete the passwords you entered for privacy! This is very important!");
            try {
                bot.execute(tmp);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void synchronizedUsers() {
        User user = bot.getAuthorizedUsers().get(id);
        for (IdPair userId : bot.getAuthorizedUsers().keySet()) {
            if (userId.getUserId().equals(id.getUserId()))
                bot.getAuthorizedUsers().put(userId, user);
        }
    }
}