package APIOperations;

import mainBody.AuthorizedUsersProvider;
import sqlTables.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APIOperationManager {
    private static AuthorizedUsersProvider usersProvider;

    public static void init(AuthorizedUsersProvider provider) {
        usersProvider = provider;
    }
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
                                                AssignmentRepository assignmentRepository, SubjectRepository subjectRepository,
                                                GroupRepository groupRepository, OperationCallback callback) {
        executeOperation(new GetInfoOperation(name, password, userRepository, assignmentRepository, subjectRepository, groupRepository), callback);
    }
    public static void registerCompleteAssignment(String name, String password, String assignmentId, UserRepository userRepository,
                                                  AssignmentRepository assignmentRepository, OperationCallback callback) {
        executeOperation(new CompleteAssignmentOperation(name, password,assignmentId, userRepository, assignmentRepository, usersProvider), callback);
    }
    public static void registerInCompleteAssignment(String name, String password, String assignmentId, UserRepository userRepository,
                                                    AssignmentRepository assignmentRepository, OperationCallback callback) {
        executeOperation(new InCompleteAssignmentOperation(name, password,assignmentId, userRepository, assignmentRepository, usersProvider), callback);
    }
    public static void registerSetNotificationStatus(String name, String password, Boolean allowNotifications,
                                                     UserRepository userRepository, OperationCallback callback) {
        executeOperation(new SetNotificationStatusOperation(name, password, allowNotifications, userRepository, usersProvider), callback);
    }
    public static void registerGetAdminRights(String name, String password, String token, UserRepository userRepository,
                                              AdminTokenRepository adminTokenRepository, OperationCallback callback) {
        executeOperation(new GetAdminRightsOperation(name, password, token, userRepository, adminTokenRepository, usersProvider), callback);
    }
    public static void registerSetInterval(String name, String password, Long seconds, UserRepository userRepository,
                                           OperationCallback callback) {
        executeOperation(new SetIntervalOperation(name, password, seconds, userRepository, usersProvider), callback);
    }
    public static void registerGetAllSubjects(String name, String password, UserRepository userRepository,
                                              SubjectRepository subjectRepository, OperationCallback callback) {
        executeOperation(new GetAllSubjectsOperation(name, password, userRepository, subjectRepository), callback);
    }
    public static void registerExcludeSubject(String name, String password, String subjectId, UserRepository userRepository,
                                              SubjectRepository subjectRepository, OperationCallback callback) {
        executeOperation(new ExcludeSubjectOperation(name, password, subjectId, userRepository, subjectRepository, usersProvider), callback);
    }
    public static void registerIncludeSubject(String name, String password, String subjectId, UserRepository userRepository,
                                              SubjectRepository subjectRepository, OperationCallback callback) {
        executeOperation(new IncludeSubjectOperation(name, password, subjectId, userRepository, subjectRepository, usersProvider), callback);
    }
    public static void registerGetAllGroups(String name, String password, UserRepository userRepository, GroupRepository groupRepository,
                                            OperationCallback callback) {
        executeOperation(new GetAllGroupsOperation(name, password, userRepository, groupRepository), callback);
    }
    public static void registerExitGroup(String name, String password, String groupName, UserRepository userRepository,
                                         GroupRepository groupRepository, OperationCallback callback) {
        executeOperation(new ExitGroupOperation(name, password, groupName, userRepository, groupRepository, usersProvider), callback);
    }
    public static void registerEnterGroup(String name, String password, String groupName, UserRepository userRepository,
                                         GroupRepository groupRepository, OperationCallback callback) {
        executeOperation(new EnterGroupOperation(name, password, groupName, userRepository, groupRepository, usersProvider), callback);
    }
}