inherits "/std/app.c";

use $loadfs;

void start()
{
    if (length(argv) != 2 && length(argv) != 3)
        printf("Argument error!\n");
    else {
        printf("Loading the FS...");
        bool result;
        if (length(argv) == 2) {
            result = $loadfs(argv[1]);
        } else {
            result = $loadfs(argv[1], argv[2]);
        }
        if (result)
            printf("done!\n");
        else
            printf("failed!\n");
    }
    exit();
}
