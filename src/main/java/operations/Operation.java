package operations;
import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sqlTables.User;
import states.State;
import java.util.logging.Logger;

abstract public class Operation implements Runnable {
    private static final Logger logger = Logger.getLogger(Operation.class.getName());
    protected final IdPair id;
    protected final String messageId;
    protected final MyTelegramBot bot;
    protected final String message;
    protected final SendMessage sendMessage;

    public final String IGNORE_ERROR_SIMILARITY_MARKUP = "Error editing message " +
            "reply markup: [400] Bad Request: message is not modified: " +
            "specified new message content and reply markup are exactly " +
            "the same as a current content and reply markup of the message";
    public final String IGNORE_ERROR_SIMILARITY_TEXT = "Error editing message " +
            "text: [400] Bad Request: message is not modified: " +
            "specified new message content and reply markup are exactly " +
            "the same as a current content and reply markup of the message";

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
    protected Integer sendReply() {
        try {
            return bot.execute(sendMessage).getMessageId();
        } catch (Exception e) {
            logger.severe("Can't send message: " + e.getMessage());
            return null;
        }
    }

    protected void synchronizedUsers() {
        User user = bot.getAuthorizedUsers().get(id);
        for (IdPair userId : bot.getAuthorizedUsers().keySet()) {
            if (userId.getUserId().equals(id.getUserId()))
                bot.getAuthorizedUsers().put(userId, user);
        }
    }

    protected void chooseOperation(IdPair id) {}
    protected boolean checkUnAuthorized() {
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            sendReply();
            return true;
        }
        return false;
    }
    protected boolean checkAuthorized() {
        if (bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Вы уже вошли");
            sendReply();
            return true;
        }
        return false;
    }
    protected boolean checkAdminRights() {
        User user = bot.getAuthorizedUsers().get(id);
        if ((user == null) || (!user.isCanEditTasks())) {
            sendMessage.setText("У вас нет прав для этого");
            sendReply();
            return true;
        }
        return false;
    }
    protected boolean basePaginationCheck(State state, String message) {
        boolean flag = false;
        if (message.equals(InlineKeyboardBuilder.NEXT_COMMAND)) {
            state.setPageNumber(state.getPageNumber() + 1);
            flag = true;
        }
        if (message.equals(InlineKeyboardBuilder.PREV_COMMAND)) {
            state.setPageNumber(state.getPageNumber() - 1);
            flag = true;
        }
        return flag;
    }
    protected void setLastMessage(State state, String message, InlineKeyboardMarkup replyMarkup) {
        try {
            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(id.getChatId());
            editMessage.setMessageId(state.getMessageForWorkId());
            editMessage.setText(message);
            editMessage.setReplyMarkup(replyMarkup);
            bot.execute(editMessage);
        } catch (Throwable e) {
            if (e.getMessage().equals(IGNORE_ERROR_SIMILARITY_MARKUP) ||
                e.getMessage().equals(IGNORE_ERROR_SIMILARITY_TEXT))
                return;
            //logger.severe("Can't set text last message: " + e.getMessage());
            sendMessage.setText(message);
            sendMessage.setReplyMarkup(replyMarkup);
            state.setMessageForWorkId(sendReply());
        }
    }
    protected void deleteLastUserMessage() {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(id.getChatId());
        deleteMessage.setMessageId(Integer.parseInt(messageId));
        try {
            bot.execute(deleteMessage);
            sendMessage.setReplyToMessageId(null);
        } catch (TelegramApiException e) {
            SendMessage tmp = new SendMessage();
            tmp.setChatId(id.getChatId());
            tmp.setText("Если я нахожусь в чате, то дайте мне права администратора! " +
                    "Тогда я смогу удалять из чата ваши пароли для приватности! " +
                    "Это очень важно!");
            try {
                bot.execute(tmp);
            } catch (Exception ex) {
                logger.severe("Can't delete message: " + e.getMessage());
            }
        }
    }
    protected InlineKeyboardMarkup getInlineKeyboardMarkup(String userId, State state) {return null;}
}