package nijakow.four.server.runtime.nvfs.files;

import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.security.users.Group;
import nijakow.four.server.runtime.security.users.User;
import nijakow.four.share.lang.c.ast.ASTClass;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.share.lang.c.parser.Parser;
import nijakow.four.share.lang.c.parser.StringCharStream;
import nijakow.four.share.lang.c.parser.Tokenizer;
import nijakow.four.server.runtime.nvfs.FileParent;
import nijakow.four.server.runtime.nvfs.shared.SharedTextFileState;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.serialization.base.ISerializer;

public class TextFile extends File<SharedTextFileState> {
    private String contents = "";
    private Blueprint blueprint;
    private boolean isDirty = true;

    TextFile(FileParent parent, User owner, Group gowner) {
        super(parent, owner, gowner);
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

    public Blueprint getBlueprint() { return blueprint; }
    public Blue getInstance() {
        return getState().getBlue();
    }

    public void ensureCompiled() throws CompilationException, ParseException {
        if (blueprint == null)
            compile();
    }

    public Blueprint compile() throws ParseException, CompilationException {
        if (blueprint == null || isDirty) {
            System.out.println("Compiling " + getFullName() + "...");
            Parser parser = new Parser(new Tokenizer(new StringCharStream(contents)));
            ASTClass file = parser.parseFile();
            blueprint = file.compile(getFullName(), new FourClassLoader() {
                @Override
                public Blueprint load(String path) throws ParseException, CompilationException {
                    return resolve(path).asTextFile().compile();
                }

                @Override
                public Blueprint load(Key name) {
                    return name.getBlueprint();
                }
            });
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
