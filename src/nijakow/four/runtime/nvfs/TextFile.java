package nijakow.four.runtime.nvfs;

import nijakow.four.c.ast.ASTFile;
import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.c.parser.Parser;
import nijakow.four.c.parser.StringCharStream;
import nijakow.four.c.parser.Tokenizer;
import nijakow.four.runtime.Blueprint;

public class TextFile extends File<SharedTextFileState> {
    private final String contents;
    private Blueprint blueprint;

    TextFile(FileParent parent, String contents) {
        this(parent, null, contents);
    }

    TextFile(FileParent parent, TextFile previousThis, String contents) {
        super(parent, previousThis, previousThis == null ? null : previousThis.getState());
        this.contents = contents;
    }

    @Override
    public TextFile asTextFile() {
        return this;
    }

    @Override
    protected SharedTextFileState createFileState() {
        return new SharedTextFileState();
    }

    public TextFile setContents(String newContents) {
        return new TextFile(getParent(), this, newContents);
    }

    public String getContents() {
        return contents;
    }

    public Blueprint compile() throws ParseException, CompilationException {
        if (blueprint == null) {
            Parser parser = new Parser(new Tokenizer(new StringCharStream(contents)));
            ASTFile file = parser.parse();
            blueprint = file.compile(getName(), name -> lookup(name).asTextFile().compile());
        }
        return blueprint;
    }
}
