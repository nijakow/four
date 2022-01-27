inherits "/std/cli.c";

list _argv;

list argv() { return the_argv; }

void create(object connection, func finish_cb, object me, list the_argv)
{
    "/std/cli.c"::create(connection, finish_cb, me);
    this._argv = the_argv;
}
