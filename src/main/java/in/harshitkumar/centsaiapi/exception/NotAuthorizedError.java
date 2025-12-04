package in.harshitkumar.centsaiapi.exception;

public class NotAuthorizedError extends RuntimeException {
    public NotAuthorizedError(String message) {
        super(message);
    }
}
