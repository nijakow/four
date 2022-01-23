inherits "/std/cli.c";

void start(list argv)
{
    connection()->write("Hello, world!\n");
    exit();
}

void create(object connection, func finish_cb)
{
    "/std/cli.c"::create(connection, finish_cb);
}
