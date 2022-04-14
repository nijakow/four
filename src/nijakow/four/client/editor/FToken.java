package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.StreamPosition;

public final class FToken {
    private final FTokenType type;
    private final StreamPosition startPos;
    private final StreamPosition endPos;
    private final Object payload;


    public FToken(FTokenType type, StreamPosition startPos, Object payload, StreamPosition endPos) {
        this.type = type;
        this.startPos = startPos;
        this.endPos = endPos;
        this.payload = payload;
    }

    public FTokenType getType() {
        return type;
    }

    public StreamPosition getStartPos() {
        return startPos;
    }

    public StreamPosition getEndPos() {
        return endPos;
    }

    public Object getPayload() {
        return payload;
    }
}
