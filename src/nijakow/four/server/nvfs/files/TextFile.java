package nijakow.four.server.nvfs.files;

import nijakow.four.server.logging.CompilationLogger;
import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.nvfs.FileParent;
import nijakow.four.server.serialization.fs.IFSSerializer;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.security.users.Group;
import nijakow.four.server.runtime.security.users.Identity;
import nijakow.four.server.runtime.security.users.User;
import nijakow.four.server.serialization.base.ISerializer;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.ast.ASTClass;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.share.lang.c.parser.Parser;
import nijakow.four.share.lang.c.parser.StringCharStream;
import nijakow.four.share.lang.c.parser.Tokenizer;

import java.nio.charset.StandardCharsets;

public class TextFile extends File {
    private byte[] contents;
    private Blueprint blueprint;
    private boolean isDirty = true;
    private Blue blue;

    TextFile(FileParent parent, User owner, Group gowner) {
        super(parent, owner, gowner);
        this.blue = blue;
        this.contents = new byte[0];
    }

    @Override
    public TextFile asTextFile() {
        return this;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    public String getContents() { return new String(contents, StandardCharsets.UTF_8); }
    public void setContents(byte[] bytes) { this.contents = bytes; this.isDirty = true; }
    public void setContents(String contents) {
        setContents(contents.getBytes(StandardCharsets.UTF_8));
    }

    public String readContents(Identity identity) {
        if (getRights().checkReadAccess(identity)) {
            return getContents();
        } else {
            return null;
        }
    }

    public boolean writeContents(String contents, Identity identity) {
        if (getRights().checkWriteAccess(identity)) {
            setContents(contents);
            return true;
        } else {
            return false;
        }
    }

    public Blueprint getBlueprint() { return blueprint; }
    private Blue getBlue() { return blue; }
    public Blue getInstance() {
        if (blue == null)
            blue = new Blue();
        return blue;
    }

    public void ensureCompiled(CompilationLogger logger) throws CompilationException, ParseException {
        if (blueprint == null)
            compile(logger);
    }

    public Blueprint compile(CompilationLogger logger) throws ParseException, CompilationException {
        if (blueprint == null || isDirty) {
            final String name = getFullName();
            logger.visitFile(name);
            Parser parser = new Parser(new Tokenizer(new StringCharStream(name, getContents())));
            ASTClass file = parser.parseFile();
            blueprint = file.compile(getFullName(), new FourClassLoader() {
                @Override
                public Blueprint load(String path) throws ParseException, CompilationException {
                    final File resolved = resolve(path);
                    if (resolved == null || resolved.asTextFile() == null)
                        throw new CompilationException("Resource not found: " + path);
                    return resolved.asTextFile().compile(logger);
                }

                @Override
                public Blueprint load(Key name) {
                    return name.getBlueprint();
                }
            });
            getInstance().updateBlueprint(blueprint);
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
        Blue instance = getBlue();
        if (instance != null)
            serializer.openProperty("textfile.instance").writeObject(instance).close();
    }

    @Override
    public void writeOutPayload(IFSSerializer serializer) {
        serializer.writeType("data-file");
        serializer.writeBase64Encoded(this.contents);
    }
}
