package mainBody;

import APIOperations.APIOperationManager;
import APIResponses.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import sqlTables.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}