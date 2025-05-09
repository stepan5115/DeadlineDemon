package APIOperations;

import sqlTables.UserRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APIOperationManager {
    private APIOperationManager() {}

    private static final int THREAD_POOL_SIZE = 100;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private static void executeOperation(BotOperation operation, OperationCallback callback) {
        operation.setCallback(callback);
        executor.submit(operation);
    }

    public static void registerLogInOperation(String name, String password, UserRepository userRepository,
                                                        OperationCallback callback) {
        executeOperation(new LogInOperation(name, password, userRepository), callback);
    }

    public static void registerSignUpOperation(String name, String password, UserRepository userRepository,
                                               OperationCallback callback) {
        executeOperation(new SignUpOperation(name, password, userRepository), callback);
    }
    public static void registerGetInfoOperation(String name, String password, UserRepository userRepository,
                                               OperationCallback callback) {
        executeOperation(new GetInfoOperation(name, password, userRepository), callback);
    }
}