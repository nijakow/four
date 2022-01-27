inherits "/std/cli.c";

void start(list argv)
{
    connection()->write("Hello, world!\n");
    exit();
}
