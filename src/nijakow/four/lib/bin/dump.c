use $dump;

inherits "/std/cli.c";

void start(list argv)
{
    connection()->write("Dumping...\n");
    $dump();
    exit();
}
