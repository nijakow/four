package nijakow.four.runtime.nvfs;

public class TextFile extends File<SharedTextFileState> {
    private final String contents;

    TextFile(FileParent parent, String contents) {
        this(parent, null, contents);
    }

    TextFile(FileParent parent, TextFile previousThis, String contents) {
        super(parent, previousThis, previousThis.getState());
        this.contents = contents;
    }

    @Override
    protected SharedTextFileState createFileState() {
        return new SharedTextFileState();
    }

    public TextFile setContents(String newContents) {
        return new TextFile(getParent(), this, newContents);
    }
}
