package nijakow.four.lang.c.compiler;

public interface Label {
	
	void compileJump();
	void compileJumpIfNot();

	void place();
	void close();

}
