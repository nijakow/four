inherits "/std/app.c";

use $syminfo;

void start()
{
    if (length(argv) != 3)
        printf("Argument error!\n");
    else {
        string info = $syminfo(resolve(pwd(), argv[1]), argv[2]);
        if (info != nil)
            printf("%s\n", info);
    }
    exit();
}
