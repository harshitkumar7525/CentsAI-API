package in.harshitkumar.centsaiapi.controller;

import in.harshitkumar.centsaiapi.dto.*;
import in.harshitkumar.centsaiapi.service.AiService;
import in.harshitkumar.centsaiapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Data
@AllArgsConstructor
public class UserController {
    private final AuthService authService;
    private final AiService aiService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        log.info("User Controller: Registering user");
        ResponseEntity<AuthResponse> response = authService.registerUser(registrationRequest);
        log.info("User Controller: User registered successfully");
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/{userId}/transaction")
    public ResponseEntity<AiResponse> addTransaction(@PathVariable Long userId,
                                                     Authentication authentication,
                                                     @RequestBody UserPrompt prompt) {
        log.info("UserController: Adding transaction for userId {}", userId);
        return aiService.saveData(userId, prompt);
    }
}
