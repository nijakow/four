inherits "/std/app.c";

void start()
{
    printf("%s\n", uname(getuid()));
    exit();
}
