#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"
#include "/lib/sys/fs/io.c"
#include "/lib/sys/fs/stat.c"

private void save_cb(string text, string path)
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
    if (argv.length <= 1)
        printf("usage: %s <filename>\n", argv[0]);
    else {
        string path = FileSystem_ResolveHere(argv[1]);
        terminal()->download(this::(path)save_cb);
    }
    exit(0);
}
