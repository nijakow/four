inherits "/std/app.c";

use $dumpfs;

void start()
{
    if (length(argv) != 1)
        printf("Error: No path was given!\n");
    else {
        printf("Dumping the FS...");
        if ($dumpfs())
            printf("done!\n");
        else
            printf("failed!\n");
    }
    exit();
}
