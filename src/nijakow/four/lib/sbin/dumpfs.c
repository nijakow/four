inherits "/std/app.c";

use $dumpfs;

void start()
{
    printf("Dumping the FS...");
    $dumpfs();
    printf("done!\n");
    exit();
}
