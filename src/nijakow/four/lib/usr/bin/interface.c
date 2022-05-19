#include "/lib/app.c"
#include "/lib/sys/fs/paths.c"

use $interface;
use $supers;

private void print_indentation(int indent)
{
    int i;

    for (i = 0; i < indent; i++)
        printf("%c", (i > 0 && ((i % 4) == 0)) ? '|' : ' ');
}

private void list_interface(string obj, int indent)
{
    print_indentation(indent);
    printf("+---+ %s\n", obj);
    for (string s : $interface(obj))
    {
        print_indentation(indent + 4);
        printf("|\n");
        print_indentation(indent + 4);
        printf("+-- %s\n", s);
    }
    print_indentation(indent + 4);
    printf("\n");
    for (string s : $supers(obj))
        list_interface(s, indent + 4);
}

void main(string* argv)
{
    if (argv.length != 2)
        printf("usage: %s <blueprint>\n", argv[0]);
    else {
        list_interface(FileSystem_ResolveHere(argv[1]), 0);
    }
}
