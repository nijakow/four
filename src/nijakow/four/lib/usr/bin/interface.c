#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"

use $interface;

void main(string* argv)
{
    if (argv.length != 2)
        printf("usage: %s <blueprint>\n", argv[0]);
    else {
        for (string s : $interface(FileSystem_ResolveHere(argv[1])))
            printf("%s\n", s);
    }
    exit(0);
}
