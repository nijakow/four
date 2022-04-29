inherits "/lib/app.c";
inherits "/lib/sys/fs/paths.c";
inherits "/lib/sys/fs/io.c";

void main(string* argv)
{
    for (int index = 1; index < argv.length; index++)
    {
        string file = argv[index];
        string path = FileSystem_ResolveHere(file);
        if (path == nil)
            printf("%s: does not exist!\n", file);
        else {
            string error = FileSystem_Compile(path);
            if (error == nil) {
                printf("%s: ok.\n", file);
            } else {
                printf("%s: error!\n", file);
                if (error != "")
                    printf("%s\n", error);
            }
        }
    }
    exit(0);
}
