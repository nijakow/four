inherits "/std/app.c";

void start()
{
    connection()->write(uname(getuid()), "\n");
    exit();
}
