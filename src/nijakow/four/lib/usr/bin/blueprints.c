inherits "/std/app.c";

use $blueprints;

void start()
{
    if (length(argv) != 1)
        printf("Argument error!\n");
    else {
        foreach (string s : $blueprints())
            printf("%s\n", s);
    }
    exit();
}
