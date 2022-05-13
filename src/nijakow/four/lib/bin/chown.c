#include "/lib/app.c"
#include "/lib/sys/fs/permissions.c"
#include "/lib/sys/fs/paths.c"

private bool process(string path, string new_owner)
{
    return FileSystem_SetFileOwner(FileSystem_ResolveHere(path), new_owner);
}

void main(string* argv)
{
    if (argv.length <= 1)
        printf("Argument error!\n");
    else {
        for (int i = 2; i < argv.length; i++)
        {
            if (!process(argv[i], argv[1]))
                printf("%s: error!\n", argv[i]);
        }
    }
    exit(0);
}
