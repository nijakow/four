inherits "/std/app.c";

use $httpdl;


void start()
{
    if (length(argv) != 3)
        printf("arg error!\n");
    else {
        string text = $httpdl(argv[1]);
        if (text == nil)
            printf("Error occured while downloading!\n");
        else {
            string path = resolve(pwd(), argv[2]);
            if (!touch(path))
                printf("Error occured while creating the file!\n");
            else {
                if (!echo_into(path, text))
                    printf("Error occured while writing the data!\n");
            }
        }
    }
    exit();
}
