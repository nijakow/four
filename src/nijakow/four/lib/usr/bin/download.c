inherits "/std/app.c";

void start()
{
    if (length(argv) != 2)
        printf("Argument error!\n");
    else {
        string path = resolve(pwd(), argv[1]);
        string text = connection()->query_last_uploaded_file();
        if (text == nil)
            printf("No upload detected!\n");
        else {
            if (!(touch(path) && echo_into(path, text))) {
                printf("Could not write \"%s\"!\n", path);
            } else {
                printf("\"%s\" written.\n", path);
            }
        }
    }
    exit();
}
