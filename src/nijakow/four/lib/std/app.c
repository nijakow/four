inherits "/std/cli.c";

list argv;

list args() { return argv; }

void create(object connection, func finish_cb, object me, list the_argv)
{
    "/std/cli.c"::create(connection, finish_cb, me);
    this.argv = the_argv;
}
