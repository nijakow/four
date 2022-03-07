inherits "/std/app.c";

use $interface;

void start()
{
    if (length(argv) != 2)
        printf("Argument error!\n");
    else {
        foreach (string s : $interface(argv[1]))
            printf("%s\n", s);
    }
    exit();
}
