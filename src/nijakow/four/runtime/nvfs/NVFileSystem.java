package nijakow.four.runtime.nvfs;

public class NVFileSystem implements FileParent {
    private Directory root;

    public NVFileSystem() {
        new Directory(this);
    }

    @Override
    public FileParent replaceThis(File me, File newMe) {
        System.out.println("NVFileSystem changed: " + newMe);
        root = newMe.asDirectory();
        return this;
    }

    public TextFile touch(String name) {
        return root.touch(name);
    }

    public static void main(String... args) {
        NVFileSystem fs = new NVFileSystem();
        System.out.println(fs);
        TextFile tf = fs.touch("Hello");
        System.out.println(tf + "  " + tf.getState());
        tf = tf.setContents("Hello, world!");
        System.out.println(tf + "  " + tf.getState() + " " + tf.getContents());
    }
}
