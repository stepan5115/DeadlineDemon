package mainBody;

import APIOperations.APIOperationManager;
import APIResponses.LoginResponse;
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
    @PostMapping("/login")
    public DeferredResult<ResponseEntity<LoginResponse>> login(
            @RequestParam String username,
            @RequestParam String password) {

        DeferredResult<ResponseEntity<LoginResponse>> deferredResult = new DeferredResult<>();

        APIOperationManager.registerLogInOperation(
                username,
                password,
                userRepository,
                result -> {
                    LoginResponse responseBody;
                    ResponseEntity<LoginResponse> response;

                    if (result.startsWith("WRONG")) {
                        responseBody = new LoginResponse("error", result);
                        response = ResponseEntity.status(401).body(responseBody);
                    } else {
                        responseBody = new LoginResponse("success", result);
                        response = ResponseEntity.ok(responseBody);
                    }

                    deferredResult.setResult(response);
                }
        );

        return deferredResult;
    }
}