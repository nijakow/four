package nijakow.four.runtime.nvfs;

import nijakow.four.c.ast.ASTFile;
import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.c.parser.Parser;
import nijakow.four.c.parser.StringCharStream;
import nijakow.four.c.parser.Tokenizer;
import nijakow.four.runtime.Blue;
import nijakow.four.runtime.Blueprint;

public class TextFile extends File<SharedTextFileState> {
    private String contents = "";
    private Blueprint blueprint;
    private boolean isDirty = true;

    TextFile(FileParent parent) {
        super(parent);
    }

    @Override
    public TextFile asTextFile() {
        return this;
    }

    @Override
    protected SharedTextFileState createFileState() {
        return new SharedTextFileState();
    }

    public TextFile setContents(String contents) {
        this.contents = contents;
        this.isDirty = true;
        return this;
    }

    public String getContents() { return contents; }

    public Blue getInstance() {
        return getState().getBlue();
    }

    public Blueprint compile() throws ParseException, CompilationException {
        if (blueprint == null || isDirty) {
            System.out.println("Compiling " + getName() + "...");
            Parser parser = new Parser(new Tokenizer(new StringCharStream(contents)));
            ASTFile file = parser.parse();
            blueprint = file.compile(getName(), name -> resolve(name).asTextFile().compile());
            getState().updateBlueprint(blueprint);
            isDirty = false;
        }
        return blueprint;
    }
}
