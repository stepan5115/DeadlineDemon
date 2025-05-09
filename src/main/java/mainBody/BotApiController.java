package mainBody;

import APIOperations.APIOperationManager;
import APIResponses.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import sqlTables.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BotApiController {
    private final UserRepository userRepository;

    public BotApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                result -> {
                    setResponse(result, deferredResult);
                }
        );

        return deferredResult;
    }
}