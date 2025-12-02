package in.harshitkumar.centsaiapi.exception;

public class UserAlreadyExistsError extends RuntimeException{
    public UserAlreadyExistsError(String message) {
        super(message);
    }
}
