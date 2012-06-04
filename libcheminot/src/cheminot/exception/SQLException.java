package cheminot.exception;

public class SQLException extends Exception {
    public SQLException() {
        super();
    }

    public SQLException(String message) {
        super(message);
    }

    public SQLException(Throwable cause) {
        super(cause);
    }

    public SQLException(String message, Throwable cause) {
        super(message, cause);
    }
}
