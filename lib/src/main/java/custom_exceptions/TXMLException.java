package custom_exceptions;

public abstract class TXMLException extends Exception {
    TXMLException(String message) {
        super(message);
    }
}
