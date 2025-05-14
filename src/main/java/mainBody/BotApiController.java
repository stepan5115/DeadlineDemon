package mainBody;

import APIOperations.APIOperationManager;
import APIResponses.BaseResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import sqlTables.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.DateParser;
import utils.JsonUtil;

@RestController
@RequestMapping("/api")
public class BotApiController {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final AdminTokenRepository adminTokenRepository;

    public BotApiController(UserRepository userRepository, AssignmentRepository assignmentRepository, SubjectRepository subjectRepository,
                            GroupRepository groupRepository, AdminTokenRepository adminTokenRepository) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.adminTokenRepository = adminTokenRepository;
    }
    private static void setResponse(String result,
                                    DeferredResult<ResponseEntity<BaseResponse>> deferredResult) {
        BaseResponse responseBody;
        ResponseEntity<BaseResponse> response;
        if (result.startsWith("WRONG")) {
            responseBody = new BaseResponse("error", result);
            response = ResponseEntity.status(401).body(responseBody);
        } else {
            responseBody = new BaseResponse("success", result);
            response = ResponseEntity.ok(responseBody);
        }

        deferredResult.setResult(response);
    }

    @PostMapping("/login")
    public DeferredResult<ResponseEntity<BaseResponse>> login(
            @RequestParam String username,
            @RequestParam String password) {
        System.out.println("HERE");
        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerLogInOperation(
                username,
                password,
                userRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/signup")
    public DeferredResult<ResponseEntity<BaseResponse>> signup(
            @RequestParam String username,
            @RequestParam String password) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerSignUpOperation(
                username,
                password,
                userRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/getInfo")
    public DeferredResult<ResponseEntity<BaseResponse>> getInfo(
            @RequestParam String username,
            @RequestParam String password) {
        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerGetInfoOperation(
                username,
                password,
                userRepository,
                assignmentRepository,
                subjectRepository,
                groupRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/completeAssignment")
    public DeferredResult<ResponseEntity<BaseResponse>> completeAssignment(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String assignmentId) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerCompleteAssignment(
                username,
                password,
                assignmentId,
                userRepository,
                assignmentRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/inCompleteAssignment")
    public DeferredResult<ResponseEntity<BaseResponse>> inCompleteAssignment(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String assignmentId) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerInCompleteAssignment(
                username,
                password,
                assignmentId,
                userRepository,
                assignmentRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/setNotificationStatus")
    public DeferredResult<ResponseEntity<BaseResponse>> setNotificationStatus(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam Boolean allowNotifications) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerSetNotificationStatus(
                username,
                password,
                allowNotifications,
                userRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/getAdminRights")
    public DeferredResult<ResponseEntity<BaseResponse>>getAdminRights(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String token) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerGetAdminRights(
                username,
                password,
                token,
                userRepository,
                adminTokenRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/setInterval")
    public DeferredResult<ResponseEntity<BaseResponse>>setInterval(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam Long seconds) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerSetInterval(
                username,
                password,
                seconds,
                userRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/getAllSubjects")
    public DeferredResult<ResponseEntity<BaseResponse>>getAllSubjects(
            @RequestParam String username,
            @RequestParam String password) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerGetAllSubjects(
                username,
                password,
                userRepository,
                subjectRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/excludeSubject")
    public DeferredResult<ResponseEntity<BaseResponse>>excludeSubject(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String subjectId) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerExcludeSubject(
                username,
                password,
                subjectId,
                userRepository,
                subjectRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/includeSubject")
    public DeferredResult<ResponseEntity<BaseResponse>>includeSubject(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String subjectId) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerIncludeSubject(
                username,
                password,
                subjectId,
                userRepository,
                subjectRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/getAllGroups")
    public DeferredResult<ResponseEntity<BaseResponse>>getAllGroups(
            @RequestParam String username,
            @RequestParam String password) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerGetAllGroups(
                username,
                password,
                userRepository,
                groupRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/exitGroup")
    public DeferredResult<ResponseEntity<BaseResponse>>exitGroup(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String groupName) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerExitGroup(
                username,
                password,
                groupName,
                userRepository,
                groupRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/enterGroup")
    public DeferredResult<ResponseEntity<BaseResponse>>enterGroup(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String groupName) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerEnterGroup(
                username,
                password,
                groupName,
                userRepository,
                groupRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/getAllAssignmentsIndependenceUser")
    public DeferredResult<ResponseEntity<BaseResponse>>getAllAssignmentsIndependenceUser(
            @RequestParam String username,
            @RequestParam String password) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerGetAllAssignmentsIndependenceUser(
                username,
                password,
                assignmentRepository,
                userRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/getAllSubjectsIndependenceUser")
    public DeferredResult<ResponseEntity<BaseResponse>>getAllSubjectsIndependenceUser(
            @RequestParam String username,
            @RequestParam String password) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerGetAllSubjectsIndependenceUser(
                username,
                password,
                subjectRepository,
                userRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/getAllGroupsIndependenceUser")
    public DeferredResult<ResponseEntity<BaseResponse>>getAllGroupsIndependenceUser(
            @RequestParam String username,
            @RequestParam String password) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerGetAllGroupsIndependenceUser(
                username,
                password,
                groupRepository,
                userRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/deleteAssignment")
    public DeferredResult<ResponseEntity<BaseResponse>>deleteAssignment(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String assignmentId) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerDeleteAssignment(
                username,
                password,
                assignmentId,
                userRepository,
                assignmentRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
    @PostMapping("/createAssignment")
    public DeferredResult<ResponseEntity<BaseResponse>>createAssignment(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String groupsId,
            @RequestParam String deadline,
            @RequestParam String subjectId) {

        DeferredResult<ResponseEntity<BaseResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerCreateAssignment(
                username,
                password,
                title,
                description,
                JsonUtil.parseGroupNames(groupsId),
                DateParser.parseDeadline(deadline),
                subjectId,
                userRepository,
                assignmentRepository,
                groupRepository,
                subjectRepository,
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
}