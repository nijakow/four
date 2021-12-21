package nijakow.four.c.runtime.fs;

import nijakow.four.c.ast.ASTFile;
import nijakow.four.c.parser.Parser;
import nijakow.four.c.parser.StringCharStream;
import nijakow.four.c.parser.Tokenizer;
import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Blueprint;

public class File extends FSNode {
	private String contents = "";
	private Blueprint blueprint;
	private Blue instance;
	private boolean isDirty;
	
	public File(Filesystem fs, FSNode parent, String name) {
		super(fs, parent, name);
		this.isDirty = false;
	}
	
	public File asFile() { return this; }

	protected FSNode findChild(String name) { return null; }
	
	public String getContents() { return contents; }
	public File setContents(String newContents) { contents = newContents; this.isDirty = true; return this; }
	
	public Blueprint compile() {
		System.out.println("Compiling file " + getFullName());
		Parser parser = new Parser(new Tokenizer(new StringCharStream(contents)));
		ASTFile file = parser.parse();
		return file.compile(getFilesystem());
	}
	
	public Blueprint getBlueprint() {
		if (blueprint == null || isDirty)
			blueprint = compile();
		isDirty = false;
		return blueprint;
	}
	
	public Blue getInstance() {
		if (instance == null)
			instance = getBlueprint().createBlue();
		return instance;
	}
}
