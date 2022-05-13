#include "/lib/app.c"
#include "/lib/sys/fs/stat.c"
#include "/lib/sys/fs/paths.c"

private int parse_arg(string arg)
{
    int i;
    int v;

    v = 0;
    i = 0;
    while (i < arg.length)
    {
        char c = arg[i];
        if (c >= '0' && c <= '7')
            v = (v * 8) + (c - '0');
        else
            return -1;
        i++;
    }
    return v;
}

private bool process(string path, int mod)
{
    return FileSystem_Chmod(FileSystem_ResolveHere(path), mod);
}

void main(string* argv)
{
    if (argv.length <= 1)
        printf("Argument error!\n");
    else {
        int v = parse_arg(argv[1]);
        if (v < 0)
            printf("Flag error!\n");
        else {
            for (int i = 2; i < argv.length; i++)
            {
                if (!process(argv[i], v))
                    printf("%s: error!\n", argv[i]);
            }
        }
    }
    exit(0);
}
