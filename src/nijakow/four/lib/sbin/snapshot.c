#include "/lib/app.c"

use $dumpfs;
use $loadfs;


private void usage(string file)
{
    printf("usage: %s save or %s restore\n", file, file);
}

void main(string* argv)
{
    if (argv.length == 2 && argv[1] == "save") {
        printf("saving... ");
        if ($dumpfs())
            printf("ok!\n");
        else
            printf("error!\n");
    } else if (argv.length == 2 && argv[1] == "restore") {
        printf("loading... ");
        if ($loadfs())
            printf("ok!\n");
        else
            printf("error!\n");
    } else {
        usage(argv[0]);
    }

    exit(0);
}
