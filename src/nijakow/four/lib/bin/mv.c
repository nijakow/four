#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"
#include "/lib/sys/fs/io.c"

private bool move(string from, string to)
{
    return FileSystem_Move(from, to);
}

void main(string* argv)
{
    if (argv.length != 3)
        printf("%s: argument error!\n", argv[0]);
    else {
        if (!move(FileSystem_ResolveHere(argv[1]), FileSystem_ResolveHere(argv[2])))
            printf("%s: error!\n", argv[1]);
    }
    exit(0);
}
