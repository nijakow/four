package nijakow.four.server.runtime.nvfs.files;

import nijakow.four.share.lang.c.ast.ASTFile;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.share.lang.c.parser.Parser;
import nijakow.four.share.lang.c.parser.StringCharStream;
import nijakow.four.share.lang.c.parser.Tokenizer;
import nijakow.four.server.runtime.nvfs.FileParent;
import nijakow.four.server.runtime.nvfs.shared.SharedTextFileState;
import nijakow.four.server.runtime.objects.Blue;
import nijakow.four.server.runtime.objects.Blueprint;
import nijakow.four.server.serialization.base.ISerializer;

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
        return "NVFSTextFile";
    }

    @Override
    public void serialize(ISerializer serializer) {
        serializeCore(serializer);
        serializer.openProperty("textfile.contents").writeString(getContents()).close();
        Blue instance = getInstance();
        if (instance != null)
            serializer.openProperty("textfile.instance").writeObject(instance).close();
    }
}
