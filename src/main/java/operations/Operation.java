package operations;
import mainBody.MyTelegramBot;

abstract public class Operation implements Runnable {
    protected final String chatId;
    protected final MyTelegramBot bot;
    protected final String message;

    public Operation(String chatId, MyTelegramBot bot, String message) {
        this.chatId = chatId;
        this.bot = bot;
        this.message = message;
    }
}