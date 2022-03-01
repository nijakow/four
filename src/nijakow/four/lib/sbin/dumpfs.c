inherits "/std/app.c";

use $dumpfs;

void start()
{
    if (length(argv) != 2)
        printf("Error: No path was given!\n");
    else {
        printf("Dumping the FS...");
        $dumpfs("/tmp/" + argv[1]);
        printf("done!\n");
    }
    exit();
}
