package nijakow.four.c.parser;

import nijakow.four.c.ast.OperatorType;

public class OperatorInfo {
	private final OperatorType type;
	private final int unary;
	private final int binary;
	private boolean leftToRight;
	
	public OperatorInfo(OperatorType type, int unary, int binary, boolean leftToRight) {
		this.type = type;
		this.unary = unary;
		this.binary = binary;
		this.leftToRight = leftToRight;
	}
	
	public OperatorType getType() { return type; }
	public int getUnaryPrecedence() { return unary; }
	public int getBinaryPrecedence() { return binary; }
	public boolean isLeftToRight() { return leftToRight; }
}
