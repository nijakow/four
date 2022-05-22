package nijakow.four.smalltalk.logging;

import nijakow.four.share.lang.FourCompilerException;

public class CompilationLogger {
    private final Logger logger;
    private final StringBuilder transcript = new StringBuilder();

    public CompilationLogger(Logger logger) {
        this.logger = logger;
    }

    public void visitFile(String path) {
        this.logger.println(LogLevel.INFO, "Compiling " + path + "...");
    }

    public void tell(FourCompilerException e) {
        this.logger.println(LogLevel.WARNING, "Received a compilation error: " + e.getMessage());
        transcript.append(e.getErrorText());
    }

    public String getTranscript() {
        return transcript.toString();
    }
}
