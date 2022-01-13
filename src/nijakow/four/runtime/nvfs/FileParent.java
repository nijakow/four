package nijakow.four.runtime.nvfs;

public interface FileParent {
    FileParent replaceThis(File me, File newMe);
}
