inherits "/std/app.c";

use $loadfs;

void start()
{
    if (length(argv) != 1)
        printf("Argument error!\n");
    else {
        printf("Loading the FS...");
        if ($loadfs())
            printf("done!\n");
        else
            printf("failed!\n");
    }
    exit();
}
