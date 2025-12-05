package in.harshitkumar.centsaiapi.controller;

import in.harshitkumar.centsaiapi.dto.*;
import in.harshitkumar.centsaiapi.service.AiService;
import in.harshitkumar.centsaiapi.service.AuthService;
import in.harshitkumar.centsaiapi.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
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
    private final TransactionService transactionService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        log.info("User Controller: Registering user");
        ResponseEntity<AuthResponse> response = authService.registerUser(registrationRequest);
        log.info("User Controller: User registered successfully");
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        log.info("User Controller: Logging in user");
        return authService.login(request);
    }

    @PostMapping("/ai/{userId}/transaction")
    public ResponseEntity<AiResponse> addTransaction(@PathVariable Long userId,
                                                     Authentication authentication,
                                                     @RequestBody UserPrompt prompt) {
        Long loggedInUserId = extractUserId(authentication);
        if (!loggedInUserId.equals(userId)) {
            log.error("UserController: User {} is not authorized to add transaction for userId {}", loggedInUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("UserController: Adding transaction for userId {}", userId);
        return aiService.saveData(userId, prompt);
    }

    @PostMapping("/{userId}/transaction")
    public ResponseEntity<TransactionResponse> addTransaction(@PathVariable Long userId,
                                                              Authentication authentication,
                                                              @RequestBody TransactionRequest request) {
        Long loggedInUserId = extractUserId(authentication);
        if (!loggedInUserId.equals(userId)) {
            log.error("UserController: User {} is not authorized to add transaction for userId {}", loggedInUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("UserController: Adding transaction for userId {}", userId);
        return transactionService.addTransaction(userId, request);
    }

    @DeleteMapping("/{userId}/transaction/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long userId,
                                               Authentication authentication,
                                               @PathVariable Long transactionId) {
        Long loggedInUserId = extractUserId(authentication);
        if (!loggedInUserId.equals(userId)) {
            log.error("UserController: User {} is not authorized to delete transaction for userId {}", loggedInUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("UserController: Deleting transaction for userId {}", userId);
        return transactionService.deleteTransaction(userId, transactionId);
    }

    @PatchMapping("/{userId}/transaction/{transactionId}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long userId,
                                               @PathVariable Long transactionId,
                                               Authentication authentication,
                                               @RequestBody TransactionRequest transactionRequest) {
        Long loggedInUserId = extractUserId(authentication);
        if (!loggedInUserId.equals(userId)) {
            log.error("UserController: User {} is not authorized to update transaction for userId {}", loggedInUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("UserController: Updating transaction for userId {}", userId);
        return transactionService.updateTransaction(userId, transactionId, transactionRequest);
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<UserTransactions> getTransactions(@PathVariable Long userId,
                                                            Authentication authentication) {
        Long loggedInUserId = extractUserId(authentication);
        if (!loggedInUserId.equals(userId)) {
            log.error("UserController: User {} is not authorized to retrieve transactions for userId {}", loggedInUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("UserController: Retrieving transactions for userId {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.retrieveTransactions(userId));
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            log.error("Authentication is missing");
            throw new IllegalStateException("Authentication is missing");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return Long.parseLong(principal.toString());
    }
}
