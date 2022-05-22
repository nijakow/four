package nijakow.four.smalltalk.compiler;

import nijakow.four.smalltalk.vm.instructions.VMInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class STCompilerLabel {
    private final STCompiler compiler;
    private VMInstruction instruction;
    private List<Consumer<VMInstruction>> pending = new ArrayList<>();

    public STCompilerLabel(STCompiler compiler) {
        this.compiler = compiler;
    }

    private void withLocationDo(Consumer<VMInstruction> action) {
        if (instruction != null)
            action.accept(instruction);
        else
            pending.add(action);
    }

    public void place() {
        if (instruction == null) {
            instruction = compiler.currentInstruction();
            for (Consumer<VMInstruction> consumer : pending)
                consumer.accept(instruction);
            pending.clear();
        }
    }

    public void jump() {
        withLocationDo(compiler.writeJump());
    }

    public void jumpIfNot() {
        withLocationDo(compiler.writeJumpIfNot());
    }
}
