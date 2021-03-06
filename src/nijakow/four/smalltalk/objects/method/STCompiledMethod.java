package nijakow.four.smalltalk.objects.method;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STNil;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Context;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

public class STCompiledMethod extends STInstance implements STMethod {
    private final STClass clazz;
    private final STSymbol name;
    private final VMInstruction instructions;
    private final int args;
    private final int locals;
    private final String documentar;
    private final String source;
    private STSymbol[] categories;

    public STCompiledMethod(STClass clazz, STSymbol name, int args, int locals, VMInstruction first, String documentar, String source) {
        this.clazz = clazz;
        this.name = name;
        this.instructions = first;
        this.args = args;
        this.locals = locals;
        this.documentar = documentar;
        this.source = source;
        if (documentar != null) {
            /*
             * Parse the documentar, look for "@category" to determine
             * the categories this method will be sorted into
             */
            int i = documentar.indexOf("@category");
            if (i >= 0) {
                /*
                 * Category declaration detected!
                 */
                i += 9;
                int j = documentar.indexOf('\n', i);
                if (j < 0) j = documentar.length();
                /*
                 * An @category declaration can contain multiple
                 * categories, separated by spaces
                 */
                String[] tokens = documentar.substring(i, j).trim().split("\\s+");
                /*
                 * Check if the list of categories is empty. In such a case,
                 * String.split() will return a String[] with one element - the
                 * empty string!
                 */
                if (!(tokens.length == 1 && tokens[0].isEmpty())) {
                    this.categories = new STSymbol[tokens.length];
                    for (int x = 0; x < this.categories.length; x++) {
                        this.categories[x] = STSymbol.get(tokens[x].trim());
                    }
                }
            }
        }
    }

    public STCompiledMethod(STClass clazz, STSymbol name, int args, int locals, VMInstruction first, String documentar) {
        this(clazz, name, args, locals, first, documentar, null);
    }

    public STCompiledMethod(int args, int locals, VMInstruction first, String documentar, String source) {
        this(null, null, args, locals, first, documentar, source);
    }

    public STCompiledMethod(int args, int locals, VMInstruction first, String documentar) {
        this(null, null, args, locals, first, documentar);
    }

    @Override
    public String toString() {
        return "CompiledMethod";
    }

    @Override
    public STCompiledMethod asCompiledMethod() throws FourException {
        return this;
    }

    @Override
    public void execute(Fiber fiber, int args, Context context) {
        // TODO: Check args!
        fiber.enter(context, this, this.instructions, args, this.locals);
    }

    @Override
    public STClass getClass(World world) {
        return world.getCompiledMethodClass();
    }

    @Override
    public STInstance asInstance() {
        return this;
    }

    public STSymbol getName() { return this.name; }

    public STClass getHoldingClass() { return this.clazz; }

    public String getDocumentar() {
        return this.documentar == null ? "" : this.documentar;
    }

    public String getSource() {
        return this.source == null ? "" : this.source;
    }

    public String disassemble() {
        final StringBuilder src = new StringBuilder();
        VMInstruction instruction = this.instructions;
        while (instruction != null) {
            src.append(instruction);
            src.append('\n');
            instruction = instruction.getNext();
        }
        return src.toString();
    }

    public STSymbol getCategory() {
        if (categories == null || categories.length == 0)
            return null;
        return categories[0];
    }
}
