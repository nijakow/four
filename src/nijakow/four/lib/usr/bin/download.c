#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"
#include "/lib/sys/fs/io.c"

private bool try_upload(string filename)
{
    string path = FileSystem_ResolveHere(filename);
    string text = FileSystem_ReadFile(path);
    if (text == nil) return false;
    terminal()->upload(text);
    return true;
}

void main(string* argv)
{
    for (int i = 1; i < argv.length; i++)
    {
        printf("%s: %s\n", argv[i], {"error!", "ok."}[try_upload(argv[i]) ? 1 : 0]);
    }
    exit(0);
}
