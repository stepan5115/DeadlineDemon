package APIOperations;

import mainBody.AuthorizedUsersProvider;
import sqlTables.*;

import java.time.LocalDateTime;
import java.util.List;
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
    public static void registerGetAllAssignmentsIndependenceUser(String name, String password, AssignmentRepository assignmentRepository,
                                                                 UserRepository userRepository, OperationCallback callback) {
        executeOperation(new GetAllAssignmentsIndependenceUserOperation(name, password, userRepository, assignmentRepository), callback);
    }
    public static void registerGetAllSubjectsIndependenceUser(String name, String password, SubjectRepository subjectRepository,
                                                                 UserRepository userRepository, OperationCallback callback) {
        executeOperation(new GetAllSubjectsIndependenceUserOperation(name, password, userRepository, subjectRepository), callback);
    }
    public static void registerGetAllGroupsIndependenceUser(String name, String password, GroupRepository groupRepository,
                                                                 UserRepository userRepository, OperationCallback callback) {
        executeOperation(new GetAllGroupsIndependenceUserOperation(name, password, userRepository, groupRepository), callback);
    }
    public static void registerDeleteAssignment(String name, String password, String assignmentId, UserRepository userRepository,
                                                AssignmentRepository assignmentRepository, OperationCallback callback) {
        executeOperation(new DeleteAssignmentOperation(name, password, assignmentId, userRepository, assignmentRepository), callback);
    }
    public static void registerCreateAssignment(String name, String password, String title, String description, List<String> groupsId,
                                                LocalDateTime deadline, String subjectId, UserRepository userRepository,
                                                AssignmentRepository assignmentRepository, GroupRepository groupRepository,
                                                SubjectRepository subjectRepository, OperationCallback callback) {
        executeOperation(new CreateAssignmentOperation(name, password, title, description, groupsId, deadline, subjectId,
                userRepository, assignmentRepository, groupRepository, subjectRepository), callback);
    }
    public static void registerGenerateToken(String name, String password, UserRepository userRepository,
                                             AdminTokenRepository adminTokenRepository, OperationCallback callback) {
        executeOperation(new GenerateTokenOperation(name, password, userRepository, adminTokenRepository), callback);
    }
    public static void registerGetTokens(String name, String password, UserRepository userRepository,
                                         AdminTokenRepository adminTokenRepository, OperationCallback callback) {
        executeOperation(new GetTokensOperation(name, password, userRepository, adminTokenRepository), callback);
    }
    public static void registerDeleteToken(String name, String password, String tokenId, UserRepository userRepository,
                                           AdminTokenRepository adminTokenRepository, OperationCallback callback) {
        executeOperation(new DeleteTokenOperation(name, password, tokenId, userRepository, adminTokenRepository), callback);
    }
    public static void registerCreateSubject(String name, String password, String subjectName,
                                             UserRepository userRepository, SubjectRepository subjectRepository, OperationCallback callback) {
        executeOperation(new CreateSubjectOperation(name, password, subjectName, userRepository, subjectRepository), callback);
    }
    public static void registerDeleteSubject(String name, String password, String subjectId,
                                             UserRepository userRepository, SubjectRepository subjectRepository,
                                             AssignmentRepository assignmentRepository, OperationCallback callback) {
        executeOperation(new DeleteSubjectOperation(name, password, subjectId, userRepository, subjectRepository, assignmentRepository), callback);
    }
    public static void registerCreateGroup(String name, String password, String groupName,
                                             UserRepository userRepository, GroupRepository groupRepository, OperationCallback callback) {
        executeOperation(new CreateGroupOperation(name, password, groupName, userRepository, groupRepository), callback);
    }
    public static void registerDeleteGroup(String name, String password, String groupId,
                                             UserRepository userRepository, GroupRepository groupRepository, OperationCallback callback) {
        executeOperation(new DeleteGroupOperation(name, password, groupId, userRepository, groupRepository), callback);
    }
}