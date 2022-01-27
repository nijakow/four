inherits "/std/cli.c";

void start(list argv)
{
    connection()->write(uname(getuid()), "\n");
    exit();
}
