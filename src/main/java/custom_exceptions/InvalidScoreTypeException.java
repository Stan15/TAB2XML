package custom_exceptions;

public class InvalidScoreTypeException extends TXMLException {
    public InvalidScoreTypeException(String message) {
        super(message);
    }
}
