#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"
#include "/lib/sys/fs/io.c"
#include "/lib/sys/fs/stat.c"

private bool copy(string from, string to)
{
    printf("%s -> %s...\n", from, to);
    if (FileSystem_IsTextFile(from)) {
        string target;
        if (FileSystem_IsDirectory(to)) {
            string target = to + "/" + FileSystem_Basename(from);
        } else {
            target = to;
        }
        string text = FileSystem_ReadFile(from);
        if (text == nil || !FileSystem_CreateAndWriteFile(target, text))
            return false;
        // TODO: File attributes
    } else if (FileSystem_IsDirectory(from)) {
        if (!FileSystem_Exists(to))
            if (!FileSystem_Mkdir(to))
                return false;
        // TODO: File attributes
        string* files = FileSystem_GetFilesIn(from);
        if (files != nil)
        {
            foreach (string s : files) {
                if (!copy(from + "/" + s, to + "/" + s))
                    return false;
            }
        }
    } else {
        return false;
    }
    return true;
}

void main(string* argv)
{
    if (argv.length != 3)
        printf("%s: argument error!\n", argv[0]);
    else {
        if (!copy(FileSystem_ResolveHere(argv[1]), FileSystem_ResolveHere(argv[2])))
            printf("Error!\n");
    }
    exit(0);
}
