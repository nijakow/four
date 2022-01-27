inherits "/std/cli.c";

list argv;
string working_dir;

string pwd() { return working_dir; }
list args() { return argv; }

void create(object connection, func finish_cb, object me, string working_dir, list the_argv)
{
    "/std/cli.c"::create(connection, finish_cb, me);
    this.working_dir = working_dir;
    this.argv = the_argv;
}
