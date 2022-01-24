use $dump;

inherits "/std/cli.c";

void start(list argv)
{
    connection()->write("Dumping...\n");
    $dump();
    exit();
}

void create(object connection, func finish_cb)
{
    "/std/cli.c"::create(connection, finish_cb);
}
