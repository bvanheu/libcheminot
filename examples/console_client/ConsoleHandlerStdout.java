package newchem;

import java.util.logging.ConsoleHandler;

public class ConsoleHandlerStdout extends ConsoleHandler {
    public ConsoleHandlerStdout() {
        super();
        this.setOutputStream(System.out);
    }
}
