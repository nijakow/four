package nijakow.four.server.logging;

public class CompilationLogger {
    private final Logger logger;

    public CompilationLogger(Logger logger) {
        this.logger = logger;
    }

    public void visitFile(String path) {
        this.logger.println(LogLevel.INFO, "Compiling " + path + "...");
    }
}
