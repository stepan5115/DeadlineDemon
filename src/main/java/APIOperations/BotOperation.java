package APIOperations;

public interface BotOperation extends Runnable {
    void setCallback(OperationCallback callback);
    String getResult();
}