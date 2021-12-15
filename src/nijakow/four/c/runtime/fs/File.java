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
	
	public File(FSNode parent, String name) {
		super(parent, name);
	}
	
	public File asFile() { return this; }

	protected FSNode findChild(String name) { return null; }
	
	public String getContents() { return contents; }
	public File setContents(String newContents) { contents = newContents; return this; }
	
	public Blueprint compile() {
		Parser parser = new Parser(new Tokenizer(new StringCharStream(contents)));
		ASTFile file = parser.parse();
		return file.compile();
	}
	
	public Blueprint getBlueprint() {
		if (blueprint == null)
			blueprint = compile();
		return blueprint;
	}
	
	public Blue getInstance() {
		if (instance == null)
			instance = getBlueprint().createBlue();
		return instance;
	}
}
