inherits "/std/app.c";

use $loadfs;

void start()
{
    if (length(argv) != 2)
        printf("Error: No path was given!\n");
    else {
        printf("Loading the FS...");
        $loadfs(argv[1]);
        printf("done!\n");
    }
    exit();
}
