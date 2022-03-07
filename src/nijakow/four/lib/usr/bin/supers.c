inherits "/std/app.c";

use $supers;

void list_supers(string bp, int depth)
{
    for (int x = 0; x < depth; x++)
        printf("    ");
    printf(" - %s\n", bp);
    foreach (string s : $supers(bp))
    {
        list_supers(s, depth + 1);
    }
}

void start()
{
    if (length(argv) != 2)
        printf("Argument error!\n");
    else {
        list_supers(argv[1], 0);
    }
    exit();
}
