inherits "/std/cli.c";

void start(list argv)
{
    connection()->write(pwd(), "\n");
    exit();
}
