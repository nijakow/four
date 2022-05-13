#include "/lib/app.c"

use $syminfo;
use $disassemble;

void main(string* argv)
{
    if (argv.length != 3)
        printf("usage: %s <blueprint> <symbol>\n", argv[0]);
    else {
        string text = $syminfo(argv[1], argv[2]);
        if (text == nil)
            printf("Symbol not found!\n");
        else
            printf("%s", text);
        text = $disassemble(argv[1], argv[2]);
        if (text != nil) {
            printf("\nDisassembly:\n");
            printf("%s", text);
        }
    }
    exit(0);
}
