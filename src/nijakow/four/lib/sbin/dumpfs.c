inherits "/std/app.c";

use $dumpfs;

void start()
{
    connection()->write("Dumping the FS...");
    $dumpfs();
    connection()->write("done!\n");
    exit();
}
