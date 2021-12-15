package nijakow.four.c.compiler;

public interface Label {
	
	void compileJump();
	void compileJumpIfNot();

	void place();
	void close();

}
