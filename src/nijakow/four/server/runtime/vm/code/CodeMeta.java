package nijakow.four.server.runtime.vm.code;

import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class CodeMeta {
    private final StreamPosition streamPosition;
    private final String cDoc;
    private final Type returnType;
    private final Type[] argTypes;

    public CodeMeta(StreamPosition streamPosition, String cDoc, Type returnType, Type[] argTypes) {
        this.streamPosition = streamPosition;
        this.cDoc = cDoc;
        this.returnType = returnType;
        this.argTypes = argTypes;
    }

    public StreamPosition getStreamPosition() {
        return streamPosition;
    }

    public String getCDoc() { return cDoc; }

    public Type getReturnType() {
        return returnType;
    }

    public Type[] getArgTypes() {
        return argTypes;
    }
}
