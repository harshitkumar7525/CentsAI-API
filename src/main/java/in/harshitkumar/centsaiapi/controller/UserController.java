package in.harshitkumar.centsaiapi.controller;

import in.harshitkumar.centsaiapi.dto.AuthResponse;
import in.harshitkumar.centsaiapi.dto.LoginRequest;
import in.harshitkumar.centsaiapi.dto.RegistrationRequest;
import in.harshitkumar.centsaiapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Data
@AllArgsConstructor
public class UserController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        log.info("User Controller: Registering user");
        ResponseEntity<AuthResponse> response = authService.registerUser(registrationRequest);
        log.info("User Controller: User registered successfully");
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }
}
