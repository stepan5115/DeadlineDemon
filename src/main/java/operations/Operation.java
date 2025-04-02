package operations;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

abstract public class Operation implements Runnable {
    protected final String chatId;
    protected final String userId;
    protected final String messageId;
    protected final MyTelegramBot bot;
    protected final String message;
    protected final SendMessage sendMessage;

    public Operation(String chatId, String userId, String messageId, MyTelegramBot bot, String message) {
        this.chatId = chatId;
        this.userId = userId;
        this.messageId = messageId;
        this.bot = bot;
        this.message = message;
        this.sendMessage = new SendMessage();
        this.sendMessage.setChatId(chatId);
        this.sendMessage.setReplyToMessageId(Integer.parseInt(messageId));
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
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(Integer.parseInt(messageId));
        try {
            bot.execute(deleteMessage);
            sendMessage.setReplyToMessageId(null);
        } catch (TelegramApiException e) {
            SendMessage tmp = new SendMessage();
            tmp.setChatId(chatId);
            tmp.setText("If I'm in a chat, give me admin rights! This way I can delete the passwords you entered for privacy! This is very important!");
            try {
                bot.execute(tmp);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}