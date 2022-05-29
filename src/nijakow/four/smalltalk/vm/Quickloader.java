package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.SmalltalkVM;
import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.STClass;

import java.io.InputStream;
import java.util.Scanner;

public class Quickloader {
    private final Scanner scanner;

    public Quickloader(InputStream stream) {
        this.scanner = new Scanner(stream);
    }

    public void loadInto(SmalltalkVM vm, World world) throws FourException {
        STClass clazz = null;
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (line.startsWith("---")) {
                if (builder.length() > 0) {
                    if (clazz == null) {
                        clazz = vm.runThisNow(builder.toString()).asClass();
                    } else {
                        clazz.addMethodFromSource(builder.toString());
                    }
                }
                builder = new StringBuilder();
            } else {
                builder.append(line + "\n");
            }
        }

        final String lastLine = builder.toString();
        if (!lastLine.isEmpty() && clazz != null)
            clazz.addMethodFromSource(lastLine);
    }
}
