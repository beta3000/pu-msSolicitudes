package rodriguez.ciro.model.exception;

public class EmailEnUsoException extends RuntimeException {
    public EmailEnUsoException(String message) {
        super(message);
    }
}