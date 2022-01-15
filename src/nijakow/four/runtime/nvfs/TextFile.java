package nijakow.four.runtime.nvfs;

import nijakow.four.c.ast.ASTFile;
import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.c.parser.Parser;
import nijakow.four.c.parser.StringCharStream;
import nijakow.four.c.parser.Tokenizer;
import nijakow.four.runtime.Blue;
import nijakow.four.runtime.Blueprint;
import nijakow.four.serialization.base.ISerializer;

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
            System.out.println("Compiling " + getFullName() + "...");
            Parser parser = new Parser(new Tokenizer(new StringCharStream(contents)));
            ASTFile file = parser.parse();
            blueprint = file.compile(getFullName(), name -> resolve(name).asTextFile().compile());
            getState().updateBlueprint(blueprint);
            isDirty = false;
        }
        return blueprint;
    }

    @Override
    public String getSerializationClassID() {
        return "NVFSFile";
    }

    @Override
    public int getSerializationClassRevision() {
        return 0;
    }

    @Override
    public void serialize(ISerializer serializer) {
        serializer.writeString(getContents());
        serializer.writeObject(getInstance());
    }
}
