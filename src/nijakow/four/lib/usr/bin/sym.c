#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"

use $syminfo;
use $disassemble;

void main(string* argv)
{
    if (argv.length != 3)
        printf("usage: %s <blueprint> <symbol>\n", argv[0]);
    else {
        string path = FileSystem_ResolveHere(argv[1]);
        string text = $syminfo(path, argv[2]);
        if (text == nil)
            printf("Symbol not found!\n");
        else
            printf("%s", text);
        text = $disassemble(path, argv[2]);
        if (text != nil) {
            printf("\nDisassembly:\n");
            printf("%s", text);
        }
    }
    exit(0);
}
