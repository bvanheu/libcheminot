package cheminot.exception;

/**
 *
 * @author hw
 */
public class NotConnectedException extends Exception {
    public NotConnectedException() {
        super();
    }

    public NotConnectedException(String message) {
        super(message);
    }

    public NotConnectedException(Throwable cause) {
        super(cause);
    }

    public NotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
