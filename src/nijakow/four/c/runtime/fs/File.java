package nijakow.four.c.runtime.fs;

import nijakow.four.c.ast.ASTFile;
import nijakow.four.c.parser.Parser;
import nijakow.four.c.parser.StringCharStream;
import nijakow.four.c.parser.Tokenizer;
import nijakow.four.c.runtime.Blueprint;

public class File extends FSNode {
	private String contents = "";
	private Blueprint blueprint;
	
	public File(FSNode parent, String name) {
		super(parent, name);
	}

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
}
