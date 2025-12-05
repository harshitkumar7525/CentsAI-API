package in.harshitkumar.centsaiapi.service;

import in.harshitkumar.centsaiapi.dto.AuthResponse;
import in.harshitkumar.centsaiapi.dto.LoginRequest;
import in.harshitkumar.centsaiapi.dto.RegistrationRequest;
import in.harshitkumar.centsaiapi.exception.InvalidCredentials;
import in.harshitkumar.centsaiapi.exception.UserAlreadyExistsError;
import in.harshitkumar.centsaiapi.models.User;
import in.harshitkumar.centsaiapi.repository.UserRepository;
import in.harshitkumar.centsaiapi.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<AuthResponse> registerUser(RegistrationRequest registrationRequest) {
        log.info("Auth Service: Registering user");
        User newUser  = toModel(registrationRequest);
        if(userRepository.existsByEmail(newUser.getEmail())){
            log.error("Auth Service: User already exists");
            throw new UserAlreadyExistsError("User with this email address already exists");
        }
        userRepository.save(newUser);
        AuthResponse authResponse = toResponseDto(newUser);
        log.info("Auth Service: User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    public User toModel(RegistrationRequest request){
        log.info("Auth Service: Converting RegistrationRequest to User");
        return User
                .builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    public AuthResponse toResponseDto(User user){
        log.info("Auth Service: Converting User to AuthResponse");
        return AuthResponse.builder()
                .username(user.getUsername())
                .id(user.getId())
                .token(jwtUtil.generateJwtToken(user))
                .build();
    }

    public ResponseEntity<AuthResponse> login(@Valid LoginRequest request) {
        log.info("Auth Service: Logging in user");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentials("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            log.error("Auth Service: Invalid credentials");
            throw new InvalidCredentials("Invalid email or password");
        }

        AuthResponse authResponse = toResponseDto(user);
        log.info("Auth Service: User logged in successfully");
        return ResponseEntity.ok(authResponse);
    }
}
