inherits "/std/app.c";

use $interface;
use $supers;

void list_interface(string bp, int depth)
{
    for (int x = 0; x < depth; x++)
        printf("    ");
    printf(" + %s\n", bp);
    foreach (string s : $interface(bp))
    {
        for (int x = 0; x < depth + 1; x++)
                printf("    ");
        printf("   - %s\n", s);
    }
    foreach (string s : $supers(bp))
    {
        list_interface(s, depth + 1);
    }
}

void start()
{
    if (length(argv) != 2)
        printf("Argument error!\n");
    else {
        list_interface(resolve(pwd(), argv[1]), 0);
    }
    exit();
}
