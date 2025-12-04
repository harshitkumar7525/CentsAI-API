package in.harshitkumar.centsaiapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e){
        log.error("Validation exception handler: Validation failed");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation failed");
        Map<String, Object> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsError.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsError(UserAlreadyExistsError e){
        log.error("UserAlreadyExists exception handler: User Already Exists");
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsError(InvalidCredentials e){
        log.error("InvalidCredentials exception handler: Invalid Credentials");
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AiMicroserviceNotWorking.class)
    public ResponseEntity<Map<String, String>> handleAiMicroserviceNotWorkingError(AiMicroserviceNotWorking e){
        log.error("AiMicroserviceNotWorking exception handler: Ai Microservice not working");
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundError(UserNotFound e){
        log.error("UserNotFound exception handler: User not found");
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionNotFound.class)
    public ResponseEntity<Map<String, String>> handleTransactionNotFoundError(TransactionNotFound e){
        log.error("TransactionNotFound exception handler: Transaction not found");
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedError.class)
    public ResponseEntity<Map<String, String>> handleNotAuthorizedError(NotAuthorizedError e){
        log.error("NotAuthorizedError exception handler: Not authorized");
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex){
        log.error("Generic Exception", ex);
        Map<String, Object> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Something went wrong");
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
