package cheminot.exception;

/**
 *
 * @author hw
 */
public class CompressionException extends Exception {
    public CompressionException() {
        super();
    }

    public CompressionException(String message) {
        super(message);
    }

    public CompressionException(Throwable cause) {
        super(cause);
    }

    public CompressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
