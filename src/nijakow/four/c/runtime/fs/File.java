package nijakow.four.c.runtime.fs;

import nijakow.four.c.ast.ASTFile;
import nijakow.four.c.parser.ParseException;
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
	
	public Blueprint compile() throws ParseException {
		final String name = getFullName();
		System.out.println("Compiling file " + name);
		Parser parser = new Parser(new Tokenizer(new StringCharStream(contents)));
		ASTFile file = parser.parse();
		return file.compile(name, getFilesystem());
	}

	public void recompile() throws ParseException {
		if (instance == null)
			getInstance();
		else {
			Blueprint result = compile();
			if (result != null) {
				isDirty = false;
				instance.updateBlueprint(result);
			}
		}
	}
	
	public Blueprint getBlueprint() {
		if (blueprint == null || isDirty) {
			try {
				blueprint = compile();
			} catch (ParseException e) {
				e.printStackTrace();
				return null;  // TODO: Delegate this error to the system
			}
		}
		isDirty = false;
		return blueprint;
	}
	
	public Blue getInstance() {
		if (instance == null)
			instance = getBlueprint().createBlue();
		else if (isDirty) {
			try {
				recompile();
				isDirty = false;
			} catch (ParseException e) {
				// TODO: Delegate this error to the system
				e.printStackTrace();
			}
		}
		return instance;
	}
}
