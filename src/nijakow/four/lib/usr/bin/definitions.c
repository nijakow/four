inherits "/std/app.c";

use $definitions;

void start()
{
    if (length(argv) != 2)
        printf("Argument error!\n");
    else {
        foreach (string s : $definitions(resolve(pwd(), argv[1])))
            printf("%s\n", s);
    }
    exit();
}
