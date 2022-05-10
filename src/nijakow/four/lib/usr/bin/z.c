#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"
#include "/lib/sys/fs/io.c"
#include "/lib/sys/fs/stat.c"

use $compress;

private void save_text(string text, string path)
{
    if (text != nil)
    {
        if (!FileSystem_Exists(path)) {
            if (!FileSystem_CreateFile(path)) {
                printf("%s: could not create file!\n", path);
                return;
            }
        }

        if (FileSystem_WriteFile(path, text))
            printf("%s: written!\n", path);
        else
            printf("%s: error when writing file!\n", path);
    }
}

void main(string* argv)
{
    if (argv.length != 3)
        printf("Usage: %s <file> <path>\n", argv[0]);
    else {
        string path = FileSystem_ResolveHere(argv[2]);
        string compressed = $compress(path);
        if (compressed == nil)
            printf("%s: error!\n", argv[1]);
        else {
            string file = FileSystem_ResolveHere(argv[1]);
            save_text(compressed, file);
        }
    }
    exit(0);
}
