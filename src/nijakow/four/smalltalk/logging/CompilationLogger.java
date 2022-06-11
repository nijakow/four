package nijakow.four.smalltalk.logging;

public class CompilationLogger {
    private final Logger logger;
    private final StringBuilder transcript = new StringBuilder();

    public CompilationLogger(Logger logger) {
        this.logger = logger;
    }

    public String getTranscript() {
        return transcript.toString();
    }
}
