package operations;

abstract public class Operation implements Runnable {
    private final String chatId;
    public Operation(String chatId) {
        this.chatId = chatId;
    }
}
