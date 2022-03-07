inherits "/std/app.c";

use $implementors;

void list_implementors(string bp, int depth)
{
    for (int x = 0; x < depth; x++)
        printf("    ");
    printf(" - %s\n", bp);
    foreach (string s : $implementors(bp))
    {
        list_implementors(s, depth + 1);
    }
}

void start()
{
    if (length(argv) != 2)
        printf("Argument error!\n");
    else {
        list_implementors(argv[1], 0);
    }
    exit();
}
